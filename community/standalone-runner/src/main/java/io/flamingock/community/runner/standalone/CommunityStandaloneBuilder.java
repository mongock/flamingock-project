package io.flamingock.community.runner.standalone;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.LocalConfigurator;
import io.flamingock.core.configurator.LocalConfiguratorDelegate;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.CoreConfigurator;
import io.flamingock.core.configurator.CoreConfiguratorDelegate;
import io.flamingock.core.configurator.LegacyMigration;
import io.flamingock.core.configurator.TransactionStrategy;
import io.flamingock.core.configurator.standalone.StandaloneConfigurator;
import io.flamingock.core.configurator.standalone.StandaloneConfiguratorDelegate;
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
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerBuilder;
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.core.runtime.dependency.DependencyContext;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.template.TemplateModule;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.function.Consumer;

public class CommunityStandaloneBuilder
        implements
        CoreConfigurator<CommunityStandaloneBuilder>,
        LocalConfigurator<CommunityStandaloneBuilder>,
        StandaloneConfigurator<CommunityStandaloneBuilder>,
        RunnerBuilder {

    private final CoreConfiguratorDelegate<CommunityStandaloneBuilder> coreConfiguratorDelegate;

    private final LocalConfiguratorDelegate<CommunityStandaloneBuilder> communityConfiguratorDelegate;

    private final StandaloneConfiguratorDelegate<CommunityStandaloneBuilder> standaloneConfiguratorDelegate;


    CommunityStandaloneBuilder(CoreConfiguration coreConfiguration,
                               CommunityConfiguration communityConfiguration,
                               DependencyInjectableContext dependencyInjectableContext) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this);
        this.communityConfiguratorDelegate = new LocalConfiguratorDelegate<>(communityConfiguration, () -> this);
        this.standaloneConfiguratorDelegate = new StandaloneConfiguratorDelegate<>(dependencyInjectableContext, () -> this);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = getAndInitilizeConnectionEngine();
        registerTemplates();
        return RunnerCreator.create(
                buildPipeline(),
                connectionEngine.getAuditor(),
                connectionEngine.getAuditor(),
                connectionEngine.getTransactionWrapper().orElse(null),
                connectionEngine.getLockProvider(),
                coreConfiguratorDelegate.getCoreProperties(),
                buildEventPublisher(),
                getDependencyContext(),
                getCoreProperties().isThrowExceptionIfCannotObtainLock()
        );
    }

    private void registerTemplates() {

    }

    @NotNull
    private EventPublisher buildEventPublisher() {
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
    private ConnectionEngine getAndInitilizeConnectionEngine() {
        ConnectionEngine connectionEngine = communityConfiguratorDelegate
                .getDriver()
                .getConnectionEngine(coreConfiguratorDelegate.getCoreProperties(), communityConfiguratorDelegate.getCommunityProperties());
        connectionEngine.initialize();
        return connectionEngine;
    }

    @NotNull
    private Pipeline buildPipeline() {
        return Pipeline.builder()
                .addStages(coreConfiguratorDelegate.getCoreProperties().getStages())
                .build();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  CORE
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CoreConfiguration getCoreProperties() {
        return coreConfiguratorDelegate.getCoreProperties();
    }

    @Override
    public CommunityStandaloneBuilder addStage(Stage stage) {
        return coreConfiguratorDelegate.addStage(stage);
    }

    @Override
    public CommunityStandaloneBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreConfiguratorDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreConfiguratorDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunityStandaloneBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreConfiguratorDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunityStandaloneBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreConfiguratorDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunityStandaloneBuilder setTrackIgnored(boolean trackIgnored) {
        return coreConfiguratorDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunityStandaloneBuilder setEnabled(boolean enabled) {
        return coreConfiguratorDelegate.setEnabled(enabled);
    }

    @Override
    public CommunityStandaloneBuilder setStartSystemVersion(String startSystemVersion) {
        return coreConfiguratorDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setEndSystemVersion(String endSystemVersion) {
        return coreConfiguratorDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunityStandaloneBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreConfiguratorDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunityStandaloneBuilder setMetadata(Map<String, Object> metadata) {
        return coreConfiguratorDelegate.setMetadata(metadata);
    }

    @Override
    public CommunityStandaloneBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreConfiguratorDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreConfiguratorDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunityStandaloneBuilder setDefaultAuthor(String publicMigrationAuthor) {
        return coreConfiguratorDelegate.setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public CommunityStandaloneBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreConfiguratorDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunityStandaloneBuilder addTemplateModule(TemplateModule templateModule) {
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
    //  COMMUNITY
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return communityConfiguratorDelegate.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return communityConfiguratorDelegate.getDriver();
    }

    @Override
    public CommunityConfiguration getCommunityProperties() {
        return communityConfiguratorDelegate.getCommunityProperties();
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  STANDALONE
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public CommunityStandaloneBuilder addDependency(Object instance) {
        return standaloneConfiguratorDelegate.addDependency(instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(name, instance);
    }

    @Override
    public CommunityStandaloneBuilder addDependency(Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(type, instance);
    }

    @Override
    public DependencyContext getDependencyContext() {
        return standaloneConfiguratorDelegate.getDependencyContext();
    }

    @Override
    public CommunityStandaloneBuilder addDependency(String name, Class<?> type, Object instance) {
        return standaloneConfiguratorDelegate.addDependency(name, type, instance);
    }

    @Override
    public CommunityStandaloneBuilder setPipelineStartedListener(Consumer<IPipelineStartedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setPipelineCompletedListener(Consumer<IPipelineCompletedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineCompletedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setPipelineIgnoredListener(Consumer<IPipelineIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineIgnoredListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setPipelineFailedListener(Consumer<IPipelineFailedEvent> listener) {
        return standaloneConfiguratorDelegate.setPipelineFailedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setStageStartedListener(Consumer<IStageStartedEvent> listener) {
        return standaloneConfiguratorDelegate.setStageStartedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setStageCompletedListener(Consumer<IStageCompletedEvent> listener) {
        return standaloneConfiguratorDelegate.setStageCompletedListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setStageIgnoredListener(Consumer<IStageIgnoredEvent> listener) {
        return standaloneConfiguratorDelegate.setStageIgnoredListener(listener);
    }

    @Override
    public CommunityStandaloneBuilder setStageFailedListener(Consumer<IStageFailedEvent> listener) {
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
