package io.flamingock.core.configurator.standalone;

import io.flamingock.core.configurator.CommunityConfiguration;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.CoreConfiguratorDelegate;
import io.flamingock.core.configurator.local.LocalConfigurator;
import io.flamingock.core.configurator.local.LocalConfiguratorDelegate;
import io.flamingock.core.driver.ConnectionDriver;
import io.flamingock.core.driver.ConnectionEngine;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runner.RunnerCreator;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;
import org.jetbrains.annotations.NotNull;

public class LocalStandaloneBuilder
        extends AbstractStandaloneBuilder<LocalStandaloneBuilder>
        implements LocalConfigurator<LocalStandaloneBuilder> {

    private final CoreConfiguratorDelegate<LocalStandaloneBuilder> coreConfiguratorDelegate;

    private final StandaloneConfiguratorDelegate<LocalStandaloneBuilder> standaloneConfiguratorDelegate;

    private final LocalConfiguratorDelegate<LocalStandaloneBuilder> localConfiguratorDelegate;


    LocalStandaloneBuilder(CoreConfiguration coreConfiguration,
                           CommunityConfiguration communityConfiguration,
                           DependencyInjectableContext dependencyInjectableContext) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this);
        this.standaloneConfiguratorDelegate = new StandaloneConfiguratorDelegate<>(dependencyInjectableContext, () -> this);
        this.localConfiguratorDelegate = new LocalConfiguratorDelegate<>(communityConfiguration, () -> this);

    }



    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected CoreConfiguratorDelegate<LocalStandaloneBuilder> coreConfiguratorDelegate() {
        return coreConfiguratorDelegate;
    }

    @Override
    protected StandaloneConfiguratorDelegate<LocalStandaloneBuilder> standaloneConfiguratorDelegate() {
        return standaloneConfiguratorDelegate;
    }

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

    @NotNull
    private ConnectionEngine getAndInitilizeConnectionEngine() {
        ConnectionEngine connectionEngine = localConfiguratorDelegate
                .getDriver()
                .getConnectionEngine(coreConfiguratorDelegate.getCoreProperties(), localConfiguratorDelegate.getCommunityProperties());
        connectionEngine.initialize();
        return connectionEngine;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //  LOCAL
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    public LocalStandaloneBuilder setDriver(ConnectionDriver<?> connectionDriver) {
        return localConfiguratorDelegate.setDriver(connectionDriver);
    }

    @Override
    public ConnectionDriver<?> getDriver() {
        return localConfiguratorDelegate.getDriver();
    }

    @Override
    public CommunityConfiguration getCommunityProperties() {
        return localConfiguratorDelegate.getCommunityProperties();
    }


}
