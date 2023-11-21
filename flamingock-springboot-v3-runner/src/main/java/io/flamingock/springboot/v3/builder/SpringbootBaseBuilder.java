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

package io.flamingock.springboot.v3.builder;

import io.flamingock.core.configurator.CoreConfigurable;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.CoreConfigurator;
import io.flamingock.core.configurator.CoreConfiguratorDelegate;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.legacy.LegacyMigration;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.event.model.IPipelineCompletedEvent;
import io.flamingock.core.event.model.IPipelineFailedEvent;
import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.core.event.model.IPipelineStartedEvent;
import io.flamingock.core.event.model.IStageCompletedEvent;
import io.flamingock.core.event.model.IStageFailedEvent;
import io.flamingock.core.event.model.IStageIgnoredEvent;
import io.flamingock.core.event.model.IStageStartedEvent;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.springboot.v3.SpringProfileFilter;
import io.flamingock.springboot.v3.SpringRunnerBuilder;
import io.flamingock.springboot.v3.configurator.SpringRunnerType;
import io.flamingock.springboot.v3.configurator.SpringbootConfiguration;
import io.flamingock.springboot.v3.configurator.SpringbootConfigurator;
import io.flamingock.springboot.v3.configurator.SpringbootConfiguratorDelegate;
import io.flamingock.springboot.v3.event.SpringPipelineCompletedEvent;
import io.flamingock.springboot.v3.event.SpringPipelineFailedEvent;
import io.flamingock.springboot.v3.event.SpringPipelineIgnoredEvent;
import io.flamingock.springboot.v3.event.SpringPipelineStartedEvent;
import io.flamingock.springboot.v3.event.SpringStageCompletedEvent;
import io.flamingock.springboot.v3.event.SpringStageFailedEvent;
import io.flamingock.springboot.v3.event.SpringStageIgnoredEvent;
import io.flamingock.springboot.v3.event.SpringStageStartedEvent;
import io.flamingock.template.TemplateModule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Collections;
import java.util.Map;

public abstract class SpringbootBaseBuilder<HOLDER extends SpringbootBaseBuilder<HOLDER>>
        implements
        CoreConfigurator<HOLDER>,
        SpringbootConfigurator<HOLDER>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SpringbootBaseBuilder.class);

    private final CoreConfiguratorDelegate<HOLDER> coreConfiguratorDelegate;

    private final SpringbootConfiguratorDelegate<HOLDER> springbootConfiguratorDelegate;


    protected SpringbootBaseBuilder(CoreConfiguration coreConfiguration,
                                    SpringbootConfiguration springbootConfiguration) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, this::getSelf);
        this.springbootConfiguratorDelegate = new SpringbootConfiguratorDelegate<>(springbootConfiguration, this::getSelf);
    }

    protected abstract HOLDER getSelf();


    @NotNull
    final protected EventPublisher createEventPublisher() {

        return new EventPublisher()
                //pipeline
                .addListener(IPipelineStartedEvent.class, e -> getEventPublisher().publishEvent(new SpringPipelineStartedEvent(this, e)))
                .addListener(IPipelineCompletedEvent.class, e -> getEventPublisher().publishEvent(new SpringPipelineCompletedEvent(this, e)))
                .addListener(IPipelineIgnoredEvent.class, e -> getEventPublisher().publishEvent(new SpringPipelineIgnoredEvent(this, e)))
                .addListener(IPipelineFailedEvent.class, e -> getEventPublisher().publishEvent(new SpringPipelineFailedEvent(this, e)))
                //stage
                .addListener(IStageStartedEvent.class, e -> getEventPublisher().publishEvent(new SpringStageStartedEvent(this, e)))
                .addListener(IStageCompletedEvent.class, e -> getEventPublisher().publishEvent(new SpringStageCompletedEvent(this, e)))
                .addListener(IStageIgnoredEvent.class, e -> getEventPublisher().publishEvent(new SpringStageIgnoredEvent(this, e)))
                .addListener(IStageFailedEvent.class, e -> getEventPublisher().publishEvent(new SpringStageFailedEvent(this, e)));
    }

    @NotNull
    final protected Pipeline buildPipeline(String[] activeProfiles) {
        return Pipeline.builder()
                .setFilters(Collections.singletonList(new SpringProfileFilter(activeProfiles)))
                .addStages(coreConfiguratorDelegate.getCoreProperties().getStages())
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CoreConfigurable getCoreProperties() {
        return coreConfiguratorDelegate.getCoreProperties();
    }

    @Override
    public HOLDER addStage(Stage stage) {
        return coreConfiguratorDelegate.addStage(stage);
    }

    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreConfiguratorDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreConfiguratorDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreConfiguratorDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreConfiguratorDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public HOLDER setTrackIgnored(boolean trackIgnored) {
        return coreConfiguratorDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        return coreConfiguratorDelegate.setEnabled(enabled);
    }

    @Override
    public HOLDER setStartSystemVersion(String startSystemVersion) {
        return coreConfiguratorDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        return coreConfiguratorDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        return coreConfiguratorDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        return coreConfiguratorDelegate.setMetadata(metadata);
    }

    @Override
    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        return coreConfiguratorDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        return coreConfiguratorDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public HOLDER setDefaultAuthor(String publicMigrationAuthor) {
        return coreConfiguratorDelegate.setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreConfiguratorDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public HOLDER addTemplateModule(TemplateModule templateModule) {
        return coreConfiguratorDelegate.addTemplateModule(templateModule);
    }


    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguratorDelegate.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguratorDelegate.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguratorDelegate.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguratorDelegate.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isTrackIgnored() {
        return coreConfiguratorDelegate.isTrackIgnored();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguratorDelegate.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguratorDelegate.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguratorDelegate.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguratorDelegate.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguratorDelegate.getMetadata();
    }

    @Override
    public LegacyMigration getLegacyMigration() {
        return coreConfiguratorDelegate.getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreConfiguratorDelegate.getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguratorDelegate.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguratorDelegate.getTransactionStrategy();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  SPRINGBOOT
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public HOLDER setSpringContext(ApplicationContext springContext) {
        return springbootConfiguratorDelegate.setSpringContext(springContext);
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springbootConfiguratorDelegate.getSpringContext();
    }

    @Override
    public HOLDER setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return springbootConfiguratorDelegate.setEventPublisher(applicationEventPublisher);
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return springbootConfiguratorDelegate.getEventPublisher();
    }

    @Override
    public HOLDER setRunnerType(SpringRunnerType runnerType) {
        return springbootConfiguratorDelegate.setRunnerType(runnerType);
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfiguratorDelegate.getRunnerType();
    }
}