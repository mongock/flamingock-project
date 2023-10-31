package io.flamingock.core.configurator.standalone;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.CoreConfigurator;
import io.flamingock.core.configurator.CoreConfiguratorDelegate;
import io.flamingock.core.configurator.LegacyMigration;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.local.LocalConfigurator;
import io.flamingock.core.configurator.local.LocalConfiguratorDelegate;
import io.flamingock.core.configurator.standalone.StandaloneConfigurator;
import io.flamingock.core.configurator.standalone.StandaloneConfiguratorDelegate;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
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
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerBuilder;
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.template.TemplateModule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

abstract class AbstractStandaloneBuilder<HOLDER>
        implements
        CoreConfigurator<HOLDER>,
        StandaloneConfigurator<HOLDER>,
        RunnerBuilder {

    abstract protected CoreConfiguratorDelegate<HOLDER> coreConfiguratorDelegate();

    abstract protected StandaloneConfiguratorDelegate<HOLDER> standaloneConfiguratorDelegate();




    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    abstract public Runner build();

    protected void registerTemplates() {

    }

    @NotNull
    protected EventPublisher buildEventPublisher() {
        return new EventPublisher()
                //pipeline events
                .addListener(IPipelineStartedEvent.class, getPipelineStartedListener())
                .addListener(IPipelineCompletedEvent.class, getPipelineCompletedListener())
                .addListener(IPipelineIgnoredEvent.class, getPipelineIgnoredListener())
                .addListener(IPipelineFailedEvent.class, getPipelineFailureListener())
                //stage events
                .addListener(IStageStartedEvent.class, getStageStartedListener())
                .addListener(IStageCompletedEvent.class, getStageCompletedListener())
                .addListener(IStageIgnoredEvent.class, getStageIgnoredListener())
                .addListener(IStageFailedEvent.class, getStageFailureListener())
                ;
    }


    @NotNull
    protected Pipeline buildPipeline() {
        return Pipeline.builder()
                .addStages(coreConfiguratorDelegate().getCoreProperties().getStages())
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CoreConfiguration getCoreProperties() {
        return coreConfiguratorDelegate().getCoreProperties();
    }

    @Override
    public HOLDER addStage(Stage stage) {
        return coreConfiguratorDelegate().addStage(stage);
    }

    @Override
    public HOLDER setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreConfiguratorDelegate().setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public HOLDER setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreConfiguratorDelegate().setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public HOLDER setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreConfiguratorDelegate().setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public HOLDER setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreConfiguratorDelegate().setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public HOLDER setTrackIgnored(boolean trackIgnored) {
        return coreConfiguratorDelegate().setTrackIgnored(trackIgnored);
    }

    @Override
    public HOLDER setEnabled(boolean enabled) {
        return coreConfiguratorDelegate().setEnabled(enabled);
    }

    @Override
    public HOLDER setStartSystemVersion(String startSystemVersion) {
        return coreConfiguratorDelegate().setStartSystemVersion(startSystemVersion);
    }

    @Override
    public HOLDER setEndSystemVersion(String endSystemVersion) {
        return coreConfiguratorDelegate().setEndSystemVersion(endSystemVersion);
    }

    @Override
    public HOLDER setServiceIdentifier(String serviceIdentifier) {
        return coreConfiguratorDelegate().setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public HOLDER setMetadata(Map<String, Object> metadata) {
        return coreConfiguratorDelegate().setMetadata(metadata);
    }

    @Override
    public HOLDER setLegacyMigration(LegacyMigration legacyMigration) {
        return coreConfiguratorDelegate().setLegacyMigration(legacyMigration);
    }

    @Override
    public HOLDER setTransactionEnabled(Boolean transactionEnabled) {
        return coreConfiguratorDelegate().setTransactionEnabled(transactionEnabled);
    }

    @Override
    public HOLDER setDefaultAuthor(String publicMigrationAuthor) {
        return coreConfiguratorDelegate().setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public HOLDER setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreConfiguratorDelegate().setTransactionStrategy(transactionStrategy);
    }

    @Override
    public HOLDER addTemplateModule(TemplateModule templateModule) {
        return coreConfiguratorDelegate().addTemplateModule(templateModule);
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
    public boolean isTrackIgnored() {
        return coreConfiguratorDelegate().isTrackIgnored();
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
    public LegacyMigration getLegacyMigration() {
        return coreConfiguratorDelegate().getLegacyMigration();
    }

    @Override
    public Boolean getTransactionEnabled() {
        return coreConfiguratorDelegate().getTransactionEnabled();
    }

    @Override
    public String getDefaultAuthor() {
        return coreConfiguratorDelegate().getDefaultAuthor();
    }

    @Override
    public TransactionStrategy getTransactionStrategy() {
        return coreConfiguratorDelegate().getTransactionStrategy();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  STANDALONE
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public HOLDER addDependency(Object instance) {
        return standaloneConfiguratorDelegate().addDependency(instance);
    }

    @Override
    public HOLDER addDependency(String name, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(name, instance);
    }

    @Override
    public HOLDER addDependency(Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(type, instance);
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneConfiguratorDelegate().getDependencyContext();
    }

    @Override
    public HOLDER addDependency(String name, Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate().addDependency(name, type, instance);
    }

    @Override
    public HOLDER setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineStartedListener(listener);
    }

    @Override
    public HOLDER setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineCompletedListener(listener);
    }

    @Override
    public HOLDER setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineIgnoredListener(listener);
    }

    @Override
    public HOLDER setPipelineFailedListener(Consumer<IPipelineFailedEvent> listener) {
        return standaloneConfiguratorDelegate().setPipelineFailedListener(listener);
    }

    @Override
    public HOLDER setStageStartedListener(Consumer<IStageStartedEvent> listener) {
        return standaloneConfiguratorDelegate().setStageStartedListener(listener);
    }

    @Override
    public HOLDER setStageCompletedListener(Consumer<IStageCompletedEvent> listener) {
        return standaloneConfiguratorDelegate().setStageCompletedListener(listener);
    }

    @Override
    public HOLDER setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate().setStageIgnoredListener(listener);
    }

    @Override
    public HOLDER setStageFailedListener(Consumer<IStageFailedEvent> listener) {
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
}
