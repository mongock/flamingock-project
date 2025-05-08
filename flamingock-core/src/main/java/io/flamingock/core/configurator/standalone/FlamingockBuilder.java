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

package io.flamingock.core.configurator.standalone;

import io.flamingock.commons.utils.JsonObjectMapper;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.commons.utils.http.Http;
import io.flamingock.core.cloud.CloudEngine;
import io.flamingock.core.cloud.transaction.CloudTransactioner;
import io.flamingock.core.configurator.FrameworkPlugin;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfigurator;
import io.flamingock.core.configurator.cloud.CloudConfiguratorDelegate;
import io.flamingock.core.configurator.cloud.CloudSystemModuleManager;
import io.flamingock.core.configurator.core.CoreConfigurable;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.configurator.core.CoreConfigurator;
import io.flamingock.core.configurator.core.CoreConfiguratorDelegate;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.event.CompositeEventPublisher;
import io.flamingock.core.event.EventPublisher;
import io.flamingock.core.event.SimpleEventPublisher;
import io.flamingock.core.event.model.IPipelineCompletedEvent;
import io.flamingock.core.event.model.IPipelineFailedEvent;
import io.flamingock.core.event.model.IPipelineIgnoredEvent;
import io.flamingock.core.event.model.IPipelineStartedEvent;
import io.flamingock.core.event.model.IStageCompletedEvent;
import io.flamingock.core.event.model.IStageFailedEvent;
import io.flamingock.core.event.model.IStageIgnoredEvent;
import io.flamingock.core.event.model.IStageStartedEvent;
import io.flamingock.core.pipeline.Pipeline;
import io.flamingock.core.pipeline.PipelineDescriptor;
import io.flamingock.core.preview.PreviewPipeline;
import io.flamingock.core.preview.PreviewStage;
import io.flamingock.core.runner.PipelineRunnerCreator;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerBuilder;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.runtime.dependency.PriorityDependencyContext;
import io.flamingock.core.runtime.dependency.SimpleDependencyInjectableContext;
import io.flamingock.core.system.CloudSystemModule;
import io.flamingock.core.task.filter.TaskFilter;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Consumer;

public class FlamingockBuilder
        implements
        CoreConfigurator<FlamingockBuilder, CloudSystemModule, CloudSystemModuleManager>,
        StandaloneConfigurator<FlamingockBuilder>,
        RunnerBuilder,
        CloudConfigurator<FlamingockBuilder> {

    private static final Logger logger = LoggerFactory.getLogger(FlamingockBuilder.class);

    public static FlamingockBuilder builder() {
        return new FlamingockBuilder(new CoreConfiguration(), new CloudConfiguration(), new SimpleDependencyInjectableContext(), new §CloudSystemModuleManager);
    }


    private final CoreConfiguratorDelegate<FlamingockBuilder, CloudSystemModule, CloudSystemModuleManager> coreConfiguratorDelegate;

    private final StandaloneConfiguratorDelegate<FlamingockBuilder> standaloneConfiguratorDelegate;

    private final CloudConfiguratorDelegate<FlamingockBuilder> cloudConfiguratorDelegate;

    private FlamingockBuilder(CoreConfiguration coreConfiguration,
                              CloudConfiguration cloudConfiguration,
                              DependencyInjectableContext dependencyInjectableContext,
                              CloudSystemModuleManager systemModuleManager) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this, systemModuleManager);
        this.standaloneConfiguratorDelegate = new StandaloneConfiguratorDelegate<>(dependencyInjectableContext, () -> this);
        this.cloudConfiguratorDelegate = new CloudConfiguratorDelegate<>(cloudConfiguration, () -> this);

    }

    protected CoreConfiguratorDelegate<FlamingockBuilder, CloudSystemModule, CloudSystemModuleManager> coreConfiguratorDelegate() {
        return coreConfiguratorDelegate;
    }

    protected StandaloneConfiguratorDelegate<FlamingockBuilder> standaloneConfiguratorDelegate() {
        return standaloneConfiguratorDelegate;
    }
    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD

    /// ////////////////////////////////////////////////////////////////////////////////


    @Override
    public Runner build() {

        coreConfiguratorDelegate.initialize();
        cloudConfiguratorDelegate.initialize();
        standaloneConfiguratorDelegate.initialize();

        RunnerId runnerId = RunnerId.generate();
        logger.info("Generated runner id:  {}", runnerId);


        CloudEngine.Factory engineFactory = CloudEngine.newFactory(
                runnerId,
                coreConfiguratorDelegate.getCoreConfiguration(),
                cloudConfiguratorDelegate.getCloudConfiguration(),
                getCloudTransactioner().orElse(null),
                Http.builderFactory(HttpClients.createDefault(), JsonObjectMapper.DEFAULT_INSTANCE)
        );

        CloudEngine engine = engineFactory.initializeAndGet();

        CloudSystemModuleManager systemModuleManager = coreConfiguratorDelegate.getSystemModuleManager();
        systemModuleManager
                .initialize(engine.getEnvironmentId(), engine.getServiceId(), engine.getJwt(), cloudConfiguratorDelegate.getCloudConfiguration().getHost());

        systemModuleManager
                .getDependencies()
                .forEach(d -> addDependency(d.getName(), d.getType(), d.getInstance()));


        CoreConfigurable coreConfiguration = coreConfiguratorDelegate().getCoreConfiguration();

        //Injecting auditWriter
        addDependency(AuditWriter.class, engine.getAuditWriter());


        List<TaskFilter> taskFilters = new LinkedList<>();
        List<EventPublisher> eventPublishersFromPlugins = new LinkedList<>();
        DependencyContext dependencyContextFromBuilder = getDependencyContext();

        List<DependencyContext> dependencyContextsFromPlugins = new LinkedList<>();
        for (FrameworkPlugin plugin : ServiceLoader.load(FrameworkPlugin.class)) {
            plugin.initialize(dependencyContextFromBuilder);

            if (plugin.getEventPublisher().isPresent()) {
                eventPublishersFromPlugins.add(plugin.getEventPublisher().get());
            }

            if (plugin.getDependencyContext().isPresent()) {
                dependencyContextsFromPlugins.add(plugin.getDependencyContext().get());
            }
            taskFilters.addAll(plugin.getTaskFilters());
        }

        // Builds a hierarchical dependency context by chaining all plugin-provided contexts,
        // placing Flamingock's core context at the top.
        DependencyContext mergedContext = dependencyContextsFromPlugins
                .stream()
                .filter(Objects::nonNull)
                .reduce((previous, current) -> new PriorityDependencyContext(current, previous))
                .<DependencyContext>map(accumulated -> new PriorityDependencyContext(dependencyContextFromBuilder, accumulated))
                .orElse(dependencyContextFromBuilder);


        Pipeline pipeline = buildPipeline(
                taskFilters,
                systemModuleManager.getSortedSystemStagesBefore(),
                coreConfiguration.getPreviewPipeline(),
                systemModuleManager.getSortedSystemStagesAfter()
        );

        //injecting the pipeline descriptor to the dependencies
        addDependency(PipelineDescriptor.class, pipeline);

        return PipelineRunnerCreator.createCloud(
                runnerId,
                pipeline,
                engine,
                coreConfiguration,
                buildEventPublisher(eventPublishersFromPlugins),
                mergedContext,
                getCoreConfiguration().isThrowExceptionIfCannotObtainLock(),
                engineFactory.getCloser()
        );
    }

    private EventPublisher buildEventPublisher(List<EventPublisher> eventPublishers) {
        SimpleEventPublisher simpleEventPublisher = new SimpleEventPublisher()
                //pipeline events
                .addListener(IPipelineStartedEvent.class, getPipelineStartedListener())
                .addListener(IPipelineCompletedEvent.class, getPipelineCompletedListener())
                .addListener(IPipelineIgnoredEvent.class, getPipelineIgnoredListener())
                .addListener(IPipelineFailedEvent.class, getPipelineFailureListener())
                //stage events
                .addListener(IStageStartedEvent.class, getStageStartedListener())
                .addListener(IStageCompletedEvent.class, getStageCompletedListener())
                .addListener(IStageIgnoredEvent.class, getStageIgnoredListener())
                .addListener(IStageFailedEvent.class, getStageFailureListener());
        //TODO this addition is not good, but it will be refactored, once all the builders merged
        eventPublishers.add(simpleEventPublisher);
        return new CompositeEventPublisher(eventPublishers);
    }

    private Pipeline buildPipeline(Collection<TaskFilter> taskFilters,
                                   Collection<PreviewStage> beforeUserStages,
                                   PreviewPipeline previewPipeline,
                                   Collection<PreviewStage> afterUserStages) {
        return Pipeline.builder()
                .addFilters(taskFilters)
                .addPreviewPipeline(previewPipeline)
                .addBeforeUserStages(beforeUserStages)
                .addAfterUserStages(afterUserStages)
                .build();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE

    /// ////////////////////////////////////////////////////////////////////////////////
    @Override
    public CoreConfigurable getCoreConfiguration() {
        return coreConfiguratorDelegate().getCoreConfiguration();
    }

    @Override
    public FlamingockBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreConfiguratorDelegate().setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public FlamingockBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreConfiguratorDelegate().setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public FlamingockBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreConfiguratorDelegate().setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public FlamingockBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreConfiguratorDelegate().setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public FlamingockBuilder setEnabled(boolean enabled) {
        return coreConfiguratorDelegate().setEnabled(enabled);
    }

    @Override
    public FlamingockBuilder setStartSystemVersion(String startSystemVersion) {
        return coreConfiguratorDelegate().setStartSystemVersion(startSystemVersion);
    }

    @Override
    public FlamingockBuilder setEndSystemVersion(String endSystemVersion) {
        return coreConfiguratorDelegate().setEndSystemVersion(endSystemVersion);
    }

    @Override
    public FlamingockBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreConfiguratorDelegate().setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public FlamingockBuilder setMetadata(Map<String, Object> metadata) {
        return coreConfiguratorDelegate().setMetadata(metadata);
    }

    @Override
    public FlamingockBuilder setDefaultAuthor(String publicMigrationAuthor) {
        return coreConfiguratorDelegate().setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public FlamingockBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreConfiguratorDelegate().setTransactionStrategy(transactionStrategy);
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguratorDelegate().getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguratorDelegate().getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguratorDelegate().getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguratorDelegate().isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguratorDelegate().isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguratorDelegate().getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguratorDelegate().getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguratorDelegate().getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguratorDelegate().getMetadata();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguratorDelegate().getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguratorDelegate().getTransactionStrategy();
    }

    @Override
    public FlamingockBuilder addSystemModule(CloudSystemModule systemModule) {
        return coreConfiguratorDelegate().addSystemModule(systemModule);
    }

    @Override
    public CloudSystemModuleManager getSystemModuleManager() {
        return coreConfiguratorDelegate().getSystemModuleManager();
    }


    @Override
    public FlamingockBuilder withImporter(CoreConfiguration.ImporterConfiguration mongockImporterConfiguration) {
        return coreConfiguratorDelegate().withImporter(mongockImporterConfiguration);
    }

    @Override
    public CoreConfiguration.ImporterConfiguration getMongockImporterConfiguration() {
        return coreConfiguratorDelegate().getMongockImporterConfiguration();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  STANDALONE

    /// ////////////////////////////////////////////////////////////////////////////////

    @Override
    public FlamingockBuilder addDependency(Object instance) {
        return standaloneConfiguratorDelegate().addDependency(instance);
    }

    @Override
    public FlamingockBuilder addDependency(String name, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(name, instance);
    }

    @Override
    public FlamingockBuilder addDependency(Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(type, instance);
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneConfiguratorDelegate().getDependencyContext();
    }

    @Override
    public FlamingockBuilder addDependency(String name, Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(name, type, instance);
    }

    @Override
    public FlamingockBuilder setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineStartedListener(listener);
    }

    @Override
    public FlamingockBuilder setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineCompletedListener(listener);
    }

    @Override
    public FlamingockBuilder setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineIgnoredListener(listener);
    }

    @Override
    public FlamingockBuilder setPipelineFailedListener(Consumer<IPipelineFailedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineFailedListener(listener);
    }

    @Override
    public FlamingockBuilder setStageStartedListener(Consumer<IStageStartedEvent> listener) {
        return standaloneConfiguratorDelegate().setStageStartedListener(listener);
    }

    @Override
    public FlamingockBuilder setStageCompletedListener(Consumer<IStageCompletedEvent> listener) {
        return standaloneConfiguratorDelegate().setStageCompletedListener(listener);
    }

    @Override
    public FlamingockBuilder setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate().setStageIgnoredListener(listener);
    }

    @Override
    public FlamingockBuilder setStageFailedListener(Consumer<IStageFailedEvent> listener) {
        return standaloneConfiguratorDelegate().setStageFailedListener(listener);
    }

    @Override
    public Consumer<IPipelineStartedEvent> getPipelineStartedListener() {
        return standaloneConfiguratorDelegate().getPipelineStartedListener();
    }

    @Override
    public Consumer<IPipelineCompletedEvent> getPipelineCompletedListener() {
        return standaloneConfiguratorDelegate().getPipelineCompletedListener();
    }

    @Override
    public Consumer<IPipelineIgnoredEvent> getPipelineIgnoredListener() {
        return standaloneConfiguratorDelegate().getPipelineIgnoredListener();
    }


    @Override
    public Consumer<IPipelineFailedEvent> getPipelineFailureListener() {
        return standaloneConfiguratorDelegate().getPipelineFailureListener();
    }

    @Override
    public Consumer<IStageStartedEvent> getStageStartedListener() {
        return standaloneConfiguratorDelegate().getStageStartedListener();
    }

    @Override
    public Consumer<IStageCompletedEvent> getStageCompletedListener() {
        return standaloneConfiguratorDelegate().getStageCompletedListener();
    }

    @Override
    public Consumer<IStageIgnoredEvent> getStageIgnoredListener() {
        return standaloneConfiguratorDelegate().getStageIgnoredListener();
    }

    @Override
    public Consumer<IStageFailedEvent> getStageFailureListener() {
        return standaloneConfiguratorDelegate().getStageFailureListener();
    }


    /**
     * CLOUD
     */


    @Override
    public FlamingockBuilder setHost(String host) {
        return cloudConfiguratorDelegate.setHost(host);
    }

    @Override
    public FlamingockBuilder setService(String service) {
        return cloudConfiguratorDelegate.setService(service);
    }

    @Override
    public FlamingockBuilder setEnvironment(String environment) {
        return cloudConfiguratorDelegate.setEnvironment(environment);
    }

    @Override
    public FlamingockBuilder setApiToken(String clientSecret) {
        return cloudConfiguratorDelegate.setApiToken(clientSecret);
    }

    @Override
    public FlamingockBuilder setCloudTransactioner(CloudTransactioner cloudTransactioner) {
        return cloudConfiguratorDelegate.setCloudTransactioner(cloudTransactioner);
    }

    @Override
    public Optional<CloudTransactioner> getCloudTransactioner() {
        return cloudConfiguratorDelegate.getCloudTransactioner();
    }

}
