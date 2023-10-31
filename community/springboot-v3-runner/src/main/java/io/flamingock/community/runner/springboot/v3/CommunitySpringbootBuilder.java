package io.flamingock.community.runner.springboot.v3;

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
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.core.springboot.v3.configurator.SpringRunnerType;
import io.flamingock.core.springboot.v3.configurator.SpringbootConfiguration;
import io.flamingock.core.springboot.v3.configurator.SpringbootConfigurator;
import io.flamingock.core.springboot.v3.configurator.SpringbootConfiguratorDelegate;
import io.flamingock.core.springboot.v3.event.SpringPipelineFailedEvent;
import io.flamingock.core.springboot.v3.event.SpringPipelineIgnoredEvent;
import io.flamingock.core.springboot.v3.event.SpringPipelineStartedEvent;
import io.flamingock.core.springboot.v3.event.SpringPipelineCompletedEvent;
import io.flamingock.core.springboot.v3.SpringDependencyContext;
import io.flamingock.core.springboot.v3.SpringProfileFilter;
import io.flamingock.core.springboot.v3.SpringRunnerBuilder;
import io.flamingock.core.springboot.v3.SpringUtil;
import io.flamingock.core.pipeline.Stage;
import io.flamingock.core.springboot.v3.event.SpringStageCompletedEvent;
import io.flamingock.core.springboot.v3.event.SpringStageFailedEvent;
import io.flamingock.core.springboot.v3.event.SpringStageIgnoredEvent;
import io.flamingock.core.springboot.v3.event.SpringStageStartedEvent;
import io.flamingock.template.TemplateModule;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class CommunitySpringbootBuilder
        implements
        CoreConfigurator<CommunitySpringbootBuilder>,
        LocalConfigurator<CommunitySpringbootBuilder>,
        SpringbootConfigurator<CommunitySpringbootBuilder>,
        SpringRunnerBuilder {

    private static final Logger logger = LoggerFactory.getLogger(CommunitySpringbootBuilder.class);

    private final CoreConfiguratorDelegate<CommunitySpringbootBuilder> coreConfiguratorDelegate;

    private final LocalConfiguratorDelegate<CommunitySpringbootBuilder> communityConfiguratorDelegate;

    private final SpringbootConfiguratorDelegate<CommunitySpringbootBuilder> springbootConfiguratorDelegate;


    CommunitySpringbootBuilder(CoreConfiguration coreConfiguration,
                               CommunityConfiguration communityConfiguration,
                               SpringbootConfiguration springbootConfiguration) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this);
        this.communityConfiguratorDelegate = new LocalConfiguratorDelegate<>(communityConfiguration, () -> this);
        this.springbootConfiguratorDelegate = new SpringbootConfiguratorDelegate<>(springbootConfiguration, () -> this);
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public Runner build() {
        ConnectionEngine connectionEngine = getAndInitilizeConnectionEngine();

        String[] activeProfiles = SpringUtil.getActiveProfiles(getSpringContext());
        logger.info("Creating runner with spring profiles[{}]", Arrays.toString(activeProfiles));

        return RunnerCreator.create(
                buildPipeline(activeProfiles),
                connectionEngine.getAuditor(),
                connectionEngine.getAuditor(),
                connectionEngine.getTransactionWrapper().orElse(null),
                connectionEngine.getLockProvider(),
                coreConfiguratorDelegate.getCoreProperties(),
                createEventPublisher(),
                new SpringDependencyContext(getSpringContext()),
                getCoreProperties().isThrowExceptionIfCannotObtainLock()
        );
    }

    @NotNull
    private EventPublisher createEventPublisher() {

        return new EventPublisher()
                //pipeline
                .addListener(IPipelineStartedEvent.class, e-> getEventPublisher().publishEvent(new SpringPipelineStartedEvent(this, e)))
                .addListener(IPipelineCompletedEvent.class, e-> getEventPublisher().publishEvent(new SpringPipelineCompletedEvent(this, e)))
                .addListener(IPipelineIgnoredEvent.class, e-> getEventPublisher().publishEvent(new SpringPipelineIgnoredEvent(this, e)))
                .addListener(IPipelineFailedEvent.class, e-> getEventPublisher().publishEvent(new SpringPipelineFailedEvent(this, e)))
                //stage
                .addListener(IStageStartedEvent.class, e-> getEventPublisher().publishEvent(new SpringStageStartedEvent(this, e)))
                .addListener(IStageCompletedEvent.class, e-> getEventPublisher().publishEvent(new SpringStageCompletedEvent(this, e)))
                .addListener(IStageIgnoredEvent.class, e-> getEventPublisher().publishEvent(new SpringStageIgnoredEvent(this, e)))
                .addListener(IStageFailedEvent.class, e-> getEventPublisher().publishEvent(new SpringStageFailedEvent(this, e)));
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
    private Pipeline buildPipeline(String[] activeProfiles) {
        return Pipeline.builder()
                .setFilters(Collections.singletonList(new SpringProfileFilter(activeProfiles)))
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
    public CommunitySpringbootBuilder addStage(Stage stage) {
        return coreConfiguratorDelegate.addStage(stage);
    }

    @Override
    public CommunitySpringbootBuilder setLockAcquiredForMillis(long lockAcquiredForMillis) {
        return coreConfiguratorDelegate.setLockAcquiredForMillis(lockAcquiredForMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockQuitTryingAfterMillis(Long lockQuitTryingAfterMillis) {
        return coreConfiguratorDelegate.setLockQuitTryingAfterMillis(lockQuitTryingAfterMillis);
    }

    @Override
    public CommunitySpringbootBuilder setLockTryFrequencyMillis(long lockTryFrequencyMillis) {
        return coreConfiguratorDelegate.setLockTryFrequencyMillis(lockTryFrequencyMillis);
    }

    @Override
    public CommunitySpringbootBuilder setThrowExceptionIfCannotObtainLock(boolean throwExceptionIfCannotObtainLock) {
        return coreConfiguratorDelegate.setThrowExceptionIfCannotObtainLock(throwExceptionIfCannotObtainLock);
    }

    @Override
    public CommunitySpringbootBuilder setTrackIgnored(boolean trackIgnored) {
        return coreConfiguratorDelegate.setTrackIgnored(trackIgnored);
    }

    @Override
    public CommunitySpringbootBuilder setEnabled(boolean enabled) {
        return coreConfiguratorDelegate.setEnabled(enabled);
    }

    @Override
    public CommunitySpringbootBuilder setStartSystemVersion(String startSystemVersion) {
        return coreConfiguratorDelegate.setStartSystemVersion(startSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setEndSystemVersion(String endSystemVersion) {
        return coreConfiguratorDelegate.setEndSystemVersion(endSystemVersion);
    }

    @Override
    public CommunitySpringbootBuilder setServiceIdentifier(String serviceIdentifier) {
        return coreConfiguratorDelegate.setServiceIdentifier(serviceIdentifier);
    }

    @Override
    public CommunitySpringbootBuilder setMetadata(Map<String, Object> metadata) {
        return coreConfiguratorDelegate.setMetadata(metadata);
    }

    @Override
    public CommunitySpringbootBuilder setLegacyMigration(LegacyMigration legacyMigration) {
        return coreConfiguratorDelegate.setLegacyMigration(legacyMigration);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionEnabled(Boolean transactionEnabled) {
        return coreConfiguratorDelegate.setTransactionEnabled(transactionEnabled);
    }

    @Override
    public CommunitySpringbootBuilder setDefaultAuthor(String publicMigrationAuthor) {
        return coreConfiguratorDelegate.setDefaultAuthor(publicMigrationAuthor);
    }

    @Override
    public CommunitySpringbootBuilder setTransactionStrategy(TransactionStrategy transactionStrategy) {
        return coreConfiguratorDelegate.setTransactionStrategy(transactionStrategy);
    }

    @Override
    public CommunitySpringbootBuilder addTemplateModule(TemplateModule templateModule) {
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
    public CommunitySpringbootBuilder setDriver(ConnectionDriver<?> connectionDriver) {
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
    //  SPRINGBOOT
    ///////////////////////////////////////////////////////////////////////////////////
    @Override
    public CommunitySpringbootBuilder setSpringContext(ApplicationContext springContext) {
        return springbootConfiguratorDelegate.setSpringContext(springContext);
    }

    @Override
    public ApplicationContext getSpringContext() {
        return springbootConfiguratorDelegate.getSpringContext();
    }

    @Override
    public CommunitySpringbootBuilder setEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return springbootConfiguratorDelegate.setEventPublisher(applicationEventPublisher);
    }

    @Override
    public ApplicationEventPublisher getEventPublisher() {
        return springbootConfiguratorDelegate.getEventPublisher();
    }

    @Override
    public CommunitySpringbootBuilder setRunnerType(SpringRunnerType runnerType) {
        return springbootConfiguratorDelegate.setRunnerType(runnerType);
    }

    @Override
    public SpringRunnerType getRunnerType() {
        return springbootConfiguratorDelegate.getRunnerType();
    }
}
