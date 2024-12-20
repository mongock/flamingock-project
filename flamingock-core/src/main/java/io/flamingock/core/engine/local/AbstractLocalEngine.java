package io.flamingock.core.engine.local;

import io.flamingock.core.api.exception.FlamingockException;
import io.flamingock.core.configurator.core.CoreConfigurable;

public abstract class AbstractLocalEngine implements LocalEngine {

    public void validate(CoreConfigurable coreConfiguration) {
        Boolean transactionEnabled = coreConfiguration.getTransactionEnabled();
        if (getTransactionWrapper().isPresent() && transactionEnabled != null && transactionEnabled) {
            throw new FlamingockException("[transactionEnabled = true] and driver is not transactional");
        }
    }
}
