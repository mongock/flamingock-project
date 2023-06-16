package io.flamingock.community.internal.driver;


import io.flamingock.core.core.audit.AuditReader;
import io.flamingock.core.core.audit.single.SingleAuditProcessStatus;
import io.flamingock.core.core.configurator.CoreProperties;
import io.flamingock.core.core.lock.AbstractLockAcquirer;
import io.flamingock.core.core.lock.Lock;
import io.flamingock.core.core.lock.LockOptions;
import io.flamingock.core.core.process.single.SingleExecutableProcess;
import io.flamingock.core.core.util.TimeService;
import io.flamingock.community.internal.CommunityProperties;
import io.flamingock.community.internal.persistence.LockRepository;

public class MongockLockAcquirer extends AbstractLockAcquirer<SingleAuditProcessStatus, SingleExecutableProcess> {

    private final LockRepository lockRepository;

    private final CoreProperties configuration;


    /**
     * @param lockRepository lockRepository to persist the lock
     * @param auditReader
     * @param coreProperties
     */
    public MongockLockAcquirer(LockRepository lockRepository,
                               AuditReader<SingleAuditProcessStatus> auditReader,
                               CoreProperties coreProperties) {
        super(auditReader);
        this.lockRepository = lockRepository;
        this.configuration = coreProperties;
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
