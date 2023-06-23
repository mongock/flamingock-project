package io.flamingock.community.internal.driver;


import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.configurator.CoreConfiguration;
import io.flamingock.core.core.lock.AbstractLockAcquirer;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockOptions;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.util.TimeService;
import io.flamingock.community.internal.persistence.LockRepository;

public class MongockLockAcquirer extends AbstractLockAcquirer<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final LockRepository lockRepository;

    private final CoreConfiguration configuration;


    /**
     * @param lockRepository lockRepository to persist the lock
     * @param auditReader
     * @param coreConfiguration
     */
    public MongockLockAcquirer(LockRepository lockRepository,
                               AuditReader<SingleAuditProcessStatus> auditReader,
                               CoreConfiguration coreConfiguration) {
        super(auditReader);
        this.lockRepository = lockRepository;
        this.configuration = coreConfiguration;
    }


    @Override
    protected Lock acquireLock(LockOptions lockOptions) {
        return MongockLock.getLock(
                configuration.getLockAcquiredForMillis(),
                configuration.getLockQuitTryingAfterMillis(),
                configuration.getLockTryFrequencyMillis(),
                lockOptions.getOwner(),
                lockRepository,
                new TimeService()
        );
    }

}
