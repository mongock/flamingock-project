package io.flamingock.core.configurator.standalone;

import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.configurator.CoreConfiguratorDelegate;
import io.flamingock.core.configurator.cloud.CloudConfiguration;
import io.flamingock.core.configurator.cloud.CloudConfigurator;
import io.flamingock.core.configurator.cloud.CloudConfiguratorDelegate;
import io.flamingock.core.runner.Runner;
import io.flamingock.core.runtime.dependency.DependencyInjectableContext;

public class CloudStandaloneBuilder
        extends AbstractStandaloneBuilder<CloudStandaloneBuilder>
        implements CloudConfigurator<CloudStandaloneBuilder> {

    private final CoreConfiguratorDelegate<CloudStandaloneBuilder> coreConfiguratorDelegate;

    private final StandaloneConfiguratorDelegate<CloudStandaloneBuilder> standaloneConfiguratorDelegate;

    private final CloudConfigurator<CloudStandaloneBuilder> cloudConfiguratorDelegate;


    CloudStandaloneBuilder(CoreConfiguration coreConfiguration,
                           CloudConfiguration cloudConfiguration,
                           DependencyInjectableContext dependencyInjectableContext) {
        this.coreConfiguratorDelegate = new CoreConfiguratorDelegate<>(coreConfiguration, () -> this);
        this.standaloneConfiguratorDelegate = new StandaloneConfiguratorDelegate<>(dependencyInjectableContext, () -> this);
        this.cloudConfiguratorDelegate = new CloudConfiguratorDelegate<>(cloudConfiguration, () -> this);

    }



    ///////////////////////////////////////////////////////////////////////////////////
    //  BUILD
    ///////////////////////////////////////////////////////////////////////////////////

    @Override
    protected CoreConfiguratorDelegate<CloudStandaloneBuilder> coreConfiguratorDelegate() {
        return coreConfiguratorDelegate;
    }

    @Override
    protected StandaloneConfiguratorDelegate<CloudStandaloneBuilder> standaloneConfiguratorDelegate() {
        return standaloneConfiguratorDelegate;
    }

    @Override
    public Runner build() {
//        ConnectionEngine connectionEngine = getAndInitilizeConnectionEngine();
//        registerTemplates();
//        return RunnerCreator.create(
//                buildPipeline(),
//                connectionEngine.getAuditor(),
//                connectionEngine.getAuditor(),
//                connectionEngine.getTransactionWrapper().orElse(null),
//                connectionEngine.getLockProvider(),
//                coreConfiguratorDelegate.getCoreProperties(),
//                buildEventPublisher(),
//                getDependencyContext(),
//                getCoreProperties().isThrowExceptionIfCannotObtainLock()
//        );
        return null;
    }


    @Override
    public CloudStandaloneBuilder setApiKey(String apiKey) {
        return cloudConfiguratorDelegate.setApiKey(apiKey);
    }

    @Override
    public CloudStandaloneBuilder setToken(String token) {
        return cloudConfiguratorDelegate.setToken(token);
    }

    @Override
    public String getApiKey() {
        return cloudConfiguratorDelegate.getApiKey();
    }

    @Override
    public String getToken() {
        return cloudConfiguratorDelegate.getToken();
    }
}
