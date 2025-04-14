/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.springboot.v2;

import io.flamingock.core.api.annotations.Change;
import io.flamingock.core.api.template.ChangeFileDescriptor;
import io.flamingock.core.preview.TemplatePreviewChangeUnit;
import io.flamingock.core.preview.builder.PreviewTaskBuilder;
import io.flamingock.core.task.loaded.AbstractLoadedTask;
import io.flamingock.core.task.loaded.LoadedTaskBuilder;
import io.flamingock.springboot.v2.SpringProfileFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpringProfileFilterTemplateTaskTest {

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[] and taskProfiles=[]")
    void trueIfActiveProfilesEmptyAndNotAnnotated() {
        assertTrue(new SpringProfileFilter().filter(getTemplateLoadedChangeUnit()));
    }

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[P1] and taskProfiles=[P1]")
    void trueIfActiveProfilesAndAnnotatedWhenMatched() {
        assertTrue(new SpringProfileFilter("P1").filter(getTemplateLoadedChangeUnit("P1")));
    }

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[P1,P2] and taskProfiles=[P1]")
    void trueIfActiveProfilesContainAnnotatedProfile() {
        assertTrue(new SpringProfileFilter("P1", "P2").filter(getTemplateLoadedChangeUnit("P1")));
    }

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[P1] and taskProfiles=[P1,P2]")
    void trueIfAnnotatedProfilesContainActiveProfile() {
        assertTrue(new SpringProfileFilter("P1").filter(getTemplateLoadedChangeUnit("P1,P2")));
    }

    @Test
    @DisplayName("SHOULD return true WHEN activeProfiles=[P2] and taskProfiles=[!P1]")
    void trueIfAnnotatedProfileIsNegativeP1AndActiveProfileIsP2() {
        assertTrue(new SpringProfileFilter("P2").filter(getTemplateLoadedChangeUnit("!P1")));
    }

    @Test
    @DisplayName("SHOULD return false WHEN activeProfiles=[] and taskProfiles=[!P1]")
    void trueIfActiveProfileEmptyAndTaskProfileNegativeP1() {
        assertTrue(new SpringProfileFilter().filter(getTemplateLoadedChangeUnit("!P1")));
    }

    @Test
    @DisplayName("SHOULD return false WHEN activeProfiles=[] and taskProfiles=[P1]")
    void falseIfActiveProfileEmptyAndTaskProfileP1() {
        assertFalse(new SpringProfileFilter().filter(getTemplateLoadedChangeUnit("P1")));
    }

    @Test
    @DisplayName("SHOULD return false WHEN activeProfiles=[P2] and taskProfiles=[P1]")
    void falseIfActiveProfileAndTaskProfileDontMatch() {
        assertFalse(new SpringProfileFilter("P2").filter(getTemplateLoadedChangeUnit("P1")));
    }

    @Test
    @DisplayName("SHOULD return false WHEN activeProfiles=[P1] and taskProfiles=[!P1]")
    void falseIfActiveProfileIsP1AndTaskProfileNegativeP1() {
        assertFalse(new SpringProfileFilter("P1").filter(getTemplateLoadedChangeUnit("!P1")));
    }

    @Test
    @DisplayName("SHOULD return false WHEN activeProfiles=[P1,P2] and taskProfiles=[!P1]")
    void falseIfActiveProfileIsP1P2AndTaskProfileNegativeP1() {
        assertFalse(new SpringProfileFilter("P1", "P2").filter(getTemplateLoadedChangeUnit("!P1")));
    }

    private AbstractLoadedTask getTemplateLoadedChangeUnit() {
        return getTemplateLoadedChangeUnit(null);
    }

    private AbstractLoadedTask getTemplateLoadedChangeUnit(String profiles) {

        ChangeFileDescriptor changeFileDescriptor = new ChangeFileDescriptor(
                "template-base-change-id",
                "1",
                TemplateSimulate.class.getName(),
                profiles,
                true,
                new HashMap<>()
        );

        TemplatePreviewChangeUnit preview = PreviewTaskBuilder.getTemplateBuilder(changeFileDescriptor).build();

        return LoadedTaskBuilder.getInstance(preview).build();

    }

    public static class TemplateSimulate {}

    @Change(id = "not-annotated", order = "0")
    public static class NotAnnotated {
    }

    @Profile("P1")
    @Change(id = "annotated-p1", order = "1")
    public static class P1 {
    }

    @Profile("!P1")
    @Change(id = "annotated-!-p1", order = "2")
    public static class NotP1 {
    }

    @Profile({"P1", "P2"})
    @Change(id = "annotated-p1-p2", order = "3")
    public static class P1AndP2 {
    }
}