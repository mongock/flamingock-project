package io.flamingock.core.engine.local;

import io.flamingock.commons.utils.RunnerId;
import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.configurator.core.CoreConfigurable;

public abstract class AbstractLocalEngine implements LocalEngine {

    private final CoreConfigurable coreConfiguration;

    protected AbstractLocalEngine(CoreConfigurable coreConfiguration) {
        this.coreConfiguration = coreConfiguration;
    }

    public void initialize(RunnerId runnerId) {
        doInitialize(runnerId);
        validate();
    }

    abstract protected void doInitialize(RunnerId runnerId);

    private void validate() {
        Boolean transactionEnabled = coreConfiguration.getTransactionEnabled();
        if (!getTransactionWrapper().isPresent() && transactionEnabled != null && transactionEnabled) {
            throw new FlamingockException("[transactionEnabled = true] and driver is not transactional");
        }
    }
}
