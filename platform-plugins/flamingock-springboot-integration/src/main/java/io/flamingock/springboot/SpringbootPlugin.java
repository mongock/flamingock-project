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

package io.flamingock.springboot;

import io.flamingock.internal.core.plugin.Plugin;
import io.flamingock.internal.core.event.EventPublisher;
import io.flamingock.internal.core.event.SimpleEventPublisher;
import io.flamingock.internal.core.event.model.IPipelineCompletedEvent;
import io.flamingock.internal.core.event.model.IPipelineFailedEvent;
import io.flamingock.internal.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.internal.core.event.model.IPipelineStartedEvent;
import io.flamingock.internal.core.event.model.IStageCompletedEvent;
import io.flamingock.internal.core.event.model.IStageFailedEvent;
import io.flamingock.internal.core.event.model.IStageIgnoredEvent;
import io.flamingock.internal.core.event.model.IStageStartedEvent;
import io.flamingock.internal.common.core.context.ContextResolver;
import io.flamingock.internal.core.task.filter.TaskFilter;
import io.flamingock.springboot.event.SpringPipelineCompletedEvent;
import io.flamingock.springboot.event.SpringPipelineFailedEvent;
import io.flamingock.springboot.event.SpringPipelineIgnoredEvent;
import io.flamingock.springboot.event.SpringPipelineStartedEvent;
import io.flamingock.springboot.event.SpringStageCompletedEvent;
import io.flamingock.springboot.event.SpringStageFailedEvent;
import io.flamingock.springboot.event.SpringStageIgnoredEvent;
import io.flamingock.springboot.event.SpringStageStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpringbootPlugin implements Plugin {

    private ApplicationContext applicationContext;
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void initialize(ContextResolver dependencyContext) {
        if (dependencyContext.getDependency(ApplicationContext.class).isPresent()) {
            applicationContext = (ApplicationContext) dependencyContext
                    .getDependency(ApplicationContext.class)
                    .get()
                    .getInstance();
        }
        if (dependencyContext.getDependency(ApplicationEventPublisher.class).isPresent()) {
            eventPublisher = (ApplicationEventPublisher) dependencyContext
                    .getDependency(ApplicationContext.class)
                    .get()
                    .getInstance();
        }
    }

    @Override
    public Optional<EventPublisher> getEventPublisher() {
        if (eventPublisher != null) {
            return Optional.of(
                    new SimpleEventPublisher()
                            //pipeline
                            .addListener(IPipelineStartedEvent.class, e -> eventPublisher.publishEvent(new SpringPipelineStartedEvent(this, e)))
                            .addListener(IPipelineCompletedEvent.class, e -> eventPublisher.publishEvent(new SpringPipelineCompletedEvent(this, e)))
                            .addListener(IPipelineIgnoredEvent.class, e -> eventPublisher.publishEvent(new SpringPipelineIgnoredEvent(this, e)))
                            .addListener(IPipelineFailedEvent.class, e -> eventPublisher.publishEvent(new SpringPipelineFailedEvent(this, e)))
                            //stage
                            .addListener(IStageStartedEvent.class, e -> eventPublisher.publishEvent(new SpringStageStartedEvent(this, e)))
                            .addListener(IStageCompletedEvent.class, e -> eventPublisher.publishEvent(new SpringStageCompletedEvent(this, e)))
                            .addListener(IStageIgnoredEvent.class, e -> eventPublisher.publishEvent(new SpringStageIgnoredEvent(this, e)))
                            .addListener(IStageFailedEvent.class, e -> eventPublisher.publishEvent(new SpringStageFailedEvent(this, e)))
            );
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<ContextResolver> getDependencyContext() {
        return applicationContext != null
                ? Optional.of(new SpringbootDependencyContext(applicationContext))
                : Optional.empty();
    }

    @Override
    public List<TaskFilter> getTaskFilters() {
        if (applicationContext != null) {
            String[] activeProfiles = SpringbootUtil.getActiveProfiles(applicationContext);
            return Collections.singletonList(new SpringbootProfileFilter(activeProfiles));
        } else {
            return Collections.emptyList();
        }
    }
}
