package io.flamingock.community.internal.driver;


import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.lock.AbstractLockAcquirer;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockOptions;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.util.TimeService;
import io.flamingock.community.internal.MongockConfiguration;
import io.flamingock.community.internal.persistence.LockRepository;

public class MongockLockAcquirer extends AbstractLockAcquirer<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final LockRepository lockRepository;

    private final MongockConfiguration configuration;


    /**
     * @param lockRepository lockRepository to persist the lock
     * @param auditReader
     * @param configuration
     */
    public MongockLockAcquirer(LockRepository lockRepository,
                               AuditReader<SingleAuditProcessStatus> auditReader,
                               MongockConfiguration configuration) {
        super(auditReader);
        this.lockRepository = lockRepository;
        this.configuration = configuration;
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
