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

import io.flamingock.commons.utils.CollectionUtil;
import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.template.TemplateFactory;
import io.flamingock.core.configurator.FrameworkPlugin;
import io.flamingock.core.configurator.SystemModuleManager;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.core.CoreConfiguration;
import io.flamingock.core.configurator.core.CoreConfigurator;
import io.flamingock.core.engine.ConnectionEngine;
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
import io.flamingock.core.runner.PipelineRunnerCreator;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerBuilder;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.PriorityDependencyContext;
import io.flamingock.core.task.filter.TaskFilter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public abstract class AbstractFlamingockBuilder<HOLDER extends AbstractFlamingockBuilder<HOLDER>>
        implements
        CoreConfigurator<HOLDER>,
        StandaloneConfigurator<HOLDER>,
        RunnerBuilder {
    private static final Logger logger = LoggerFactory.getLogger(AbstractFlamingockBuilder.class);

    private final SystemModuleManager<?> systemModuleManager;

    protected StandaloneConfigurator<HOLDER> standaloneConfiguratorDelegate;


    protected final CoreConfiguration coreConfiguration;

    private final Supplier<HOLDER> holderSupplier;


    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD

    /// ////////////////////////////////////////////////////////////////////////////////

    protected AbstractFlamingockBuilder(SystemModuleManager<?> systemModuleManager,
                                        CoreConfiguration coreConfiguration) {
        this.systemModuleManager = systemModuleManager;
        this.coreConfiguration = coreConfiguration;
        holderSupplier = this::getSelf;
    }

    protected abstract ConnectionEngine getConnectionEngine(RunnerId runnerId);

    protected abstract void configureSystemModules();

    protected abstract HOLDER getSelf();

    @Override
    public final Runner build() {

        TemplateFactory.loadTemplates();

        RunnerId runnerId = RunnerId.generate();
        logger.info("Generated runner id:  {}", runnerId);

        //START SPECIFIC CONFIG BLOCK: Cloud VS Local
        ConnectionEngine engine = getConnectionEngine(runnerId);
        configureSystemModules();
        /*
         * FINISHED SPECIFIC CONFIG BLOCK
         */
        systemModuleManager
                .getDependencies()
                .forEach(d -> addDependency(d.getName(), d.getType(), d.getInstance()));


        //Injecting auditWriter
        addDependency(AuditWriter.class, engine.getAuditWriter());

        List<FrameworkPlugin> frameworkPlugins = getPluginList();
        initializeFrameworkPlugins(frameworkPlugins);


        // Builds a hierarchical dependency context by chaining all plugin-provided contexts,
        // placing Flamingock's core context at the top.

        Pipeline pipeline = Pipeline.builder()
                .addFilters(getTaskFiltersFromPlugins(frameworkPlugins))
                .addPreviewPipeline(coreConfiguration.getPreviewPipeline())
                .addBeforeUserStages(systemModuleManager.getSortedSystemStagesBefore())
                .addAfterUserStages(systemModuleManager.getSortedSystemStagesAfter())
                .build();


        //injecting the pipeline descriptor to the dependencies
        addDependency(PipelineDescriptor.class, pipeline);

        return PipelineRunnerCreator.createWithFinalizer(
                runnerId,
                pipeline,
                engine,
                coreConfiguration,
                buildEventPublisher(frameworkPlugins),
                getMergedDependencyContext(frameworkPlugins),
                coreConfiguration.isThrowExceptionIfCannotObtainLock(),
                engine.getCloser()
        );
    }

    private DependencyContext getMergedDependencyContext(List<FrameworkPlugin> frameworkPlugins) {
        List<DependencyContext> dependencyContextsFromPlugins = frameworkPlugins.stream()
                .map(FrameworkPlugin::getDependencyContext)
                .flatMap(CollectionUtil::optionalToStream)
                .collect(Collectors.toList());
        return dependencyContextsFromPlugins
                .stream()
                .filter(Objects::nonNull)
                .reduce((previous, current) -> new PriorityDependencyContext(current, previous))
                .<DependencyContext>map(accumulated -> new PriorityDependencyContext(standaloneConfiguratorDelegate.getDependencyContext(), accumulated))
                .orElse(standaloneConfiguratorDelegate.getDependencyContext());
    }

    private void initializeFrameworkPlugins(List<FrameworkPlugin> frameworkPlugins) {
        frameworkPlugins
                .forEach(plugin -> plugin.initialize(standaloneConfiguratorDelegate.getDependencyContext()));

    }

    private List<TaskFilter> getTaskFiltersFromPlugins(List<FrameworkPlugin> frameworkPlugins) {
        return frameworkPlugins.stream()
                .map(FrameworkPlugin::getTaskFilters)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<FrameworkPlugin> getPluginList() {
        return StreamSupport
                .stream(ServiceLoader.load(FrameworkPlugin.class).spliterator(), false)
                .collect(Collectors.toList());
    }

    @NotNull
    protected EventPublisher buildEventPublisher(List<FrameworkPlugin> frameworkPlugins) {


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

        List<EventPublisher> eventPublishersFromPlugins = frameworkPlugins.stream()
                .map(FrameworkPlugin::getEventPublisher)
                .flatMap(CollectionUtil::optionalToStream)
                .collect(Collectors.toList());
        eventPublishersFromPlugins.add(simpleEventPublisher);
        return new CompositeEventPublisher(eventPublishersFromPlugins);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE

    /// ////////////////////////////////////////////////////////////////////////////////


    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        coreConfiguration.setLockAcquiredForMillis(lockAcquiredForMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        coreConfiguration.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        coreConfiguration.setLockTryFrequencyMillis(lockTryFrequencyMillis);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        coreConfiguration.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        coreConfiguration.setEnabled(enabled);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setStartSystemVersion(String startSystemVersion) {
        coreConfiguration.setStartSystemVersion(startSystemVersion);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        coreConfiguration.setEndSystemVersion(endSystemVersion);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        coreConfiguration.setServiceIdentifier(serviceIdentifier);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        coreConfiguration.setMetadata(metadata);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setDefaultAuthor(String publicMigrationAuthor) {
        coreConfiguration.setDefaultAuthor(publicMigrationAuthor);
        return holderSupplier.get();
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        coreConfiguration.setTransactionStrategy(transactionStrategy);
        return holderSupplier.get();
    }

    @Override
    public long getLockAcquiredForMillis() {
        return coreConfiguration.getLockAcquiredForMillis();
    }

    @Override
    public Long getLockQuitTryingAfterMillis() {
        return coreConfiguration.getLockQuitTryingAfterMillis();
    }

    @Override
    public long getLockTryFrequencyMillis() {
        return coreConfiguration.getLockTryFrequencyMillis();
    }

    @Override
    public boolean isThrowExceptionIfCannotObtainLock() {
        return coreConfiguration.isThrowExceptionIfCannotObtainLock();
    }

    @Override
    public boolean isEnabled() {
        return coreConfiguration.isEnabled();
    }

    @Override
    public String getStartSystemVersion() {
        return coreConfiguration.getStartSystemVersion();
    }

    @Override
    public String getEndSystemVersion() {
        return coreConfiguration.getEndSystemVersion();
    }

    @Override
    public String getServiceIdentifier() {
        return coreConfiguration.getServiceIdentifier();
    }

    @Override
    public Map<String, Object> getMetadata() {
        return coreConfiguration.getMetadata();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguration.getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguration.getTransactionStrategy();
    }


    @Override
    public HOLDER withImporter(CoreConfiguration.ImporterConfiguration mongockImporterConfiguration) {
        coreConfiguration.setLegacyMongockChangelogSource(mongockImporterConfiguration.getLegacySourceName());
        return holderSupplier.get();
    }

    @Override
    public CoreConfiguration.ImporterConfiguration getMongockImporterConfiguration() {
        return coreConfiguration.getMongockImporterConfiguration();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  STANDALONE

    /// ////////////////////////////////////////////////////////////////////////////////

    @Override
    public HOLDER addDependency(Object instance) {
        return standaloneConfiguratorDelegate.addDependency(instance);
    }

    @Override
    public HOLDER addDependency(String name, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(name, instance);
    }

    @Override
    public HOLDER addDependency(Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(type, instance);
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneConfiguratorDelegate.getDependencyContext();
    }

    @Override
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(name, type, instance);
    }

    @Override
    public HOLDER setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineStartedListener(listener);
    }

    @Override
    public HOLDER setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineCompletedListener(listener);
    }

    @Override
    public HOLDER setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineIgnoredListener(listener);
    }

    @Override
    public HOLDER setPipelineFailedListener(Consumer<IPipelineFailedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineFailedListener(listener);
    }

    @Override
    public HOLDER setStageStartedListener(Consumer<IStageStartedEvent> listener) {
        return standaloneConfiguratorDelegate.setStageStartedListener(listener);
    }

    @Override
    public HOLDER setStageCompletedListener(Consumer<IStageCompletedEvent> listener) {
        return standaloneConfiguratorDelegate.setStageCompletedListener(listener);
    }

    @Override
    public HOLDER setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate.setStageIgnoredListener(listener);
    }

    @Override
    public HOLDER setStageFailedListener(Consumer<IStageFailedEvent> listener) {
        return standaloneConfiguratorDelegate.setStageFailedListener(listener);
    }

    @Override
    public Consumer<IPipelineStartedEvent> getPipelineStartedListener() {
        return standaloneConfiguratorDelegate.getPipelineStartedListener();
    }

    @Override
    public Consumer<IPipelineCompletedEvent> getPipelineCompletedListener() {
        return standaloneConfiguratorDelegate.getPipelineCompletedListener();
    }

    @Override
    public Consumer<IPipelineIgnoredEvent> getPipelineIgnoredListener() {
        return standaloneConfiguratorDelegate.getPipelineIgnoredListener();
    }


    @Override
    public Consumer<IPipelineFailedEvent> getPipelineFailureListener() {
        return standaloneConfiguratorDelegate.getPipelineFailureListener();
    }

    @Override
    public Consumer<IStageStartedEvent> getStageStartedListener() {
        return standaloneConfiguratorDelegate.getStageStartedListener();
    }

    @Override
    public Consumer<IStageCompletedEvent> getStageCompletedListener() {
        return standaloneConfiguratorDelegate.getStageCompletedListener();
    }

    @Override
    public Consumer<IStageIgnoredEvent> getStageIgnoredListener() {
        return standaloneConfiguratorDelegate.getStageIgnoredListener();
    }

    @Override
    public Consumer<IStageFailedEvent> getStageFailureListener() {
        return standaloneConfiguratorDelegate.getStageFailureListener();
    }
}
