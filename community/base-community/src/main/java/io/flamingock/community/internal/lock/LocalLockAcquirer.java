package io.flamingock.community.internal.lock;


import io.flamingock.core.audit.AuditReader;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.lock.AbstractLockAcquirer;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.lock.LockOptions;
import io.flamingock.core.util.TimeService;

public class LocalLockAcquirer extends AbstractLockAcquirer {

    private final LockRepository lockRepository;

    private final CoreConfiguration configuration;


    /**
     * @param lockRepository    lockRepository to persist the lock
     * @param auditReader
     * @param coreConfiguration
     */
    public LocalLockAcquirer(LockRepository lockRepository,
                             AuditReader auditReader,
                             CoreConfiguration coreConfiguration) {
        super(auditReader);
        this.lockRepository = lockRepository;
        this.configuration = coreConfiguration;
    }


    @Override
    protected Lock acquireLock(LockOptions lockOptions) {
        return LocalLock.getLock(
                configuration.getLockAcquiredForMillis(),
                configuration.getLockQuitTryingAfterMillis(),
                configuration.getLockTryFrequencyMillis(),
                lockOptions.getOwner(),
                lockRepository,
                new TimeService()
        );
    }

}
