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

package io.flamingock.springboot.event;


import io.flamingock.internal.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.internal.core.event.model.IStageIgnoredEvent;
import org.springframework.context.ApplicationEvent;

public class SpringStageIgnoredEvent extends ApplicationEvent implements IPipelineIgnoredEvent {

    private final IStageIgnoredEvent event;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public SpringStageIgnoredEvent(Object source, IStageIgnoredEvent event) {
        super(source);
        this.event = event;
    }


    @Override
    public String toString() {
        return "SpringPipelineIgnoredEvent{" +
                "event=" + event +
                ", source=" + source +
                '}';
    }
}
