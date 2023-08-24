package io.flamingock.community.internal.driver;


import io.flamingock.community.internal.persistence.LockRepository;
import io.flamingock.core.audit.single.SingleAuditReader;
import io.flamingock.core.audit.single.SingleAuditStageStatus;
import io.flamingock.core.configurator.CoreConfiguration;
import io.flamingock.core.lock.AbstractLockAcquirer;
import io.flamingock.core.lock.Lock;
import io.flamingock.core.lock.LockOptions;
import io.flamingock.core.stage.ExecutableStage;
import io.flamingock.core.util.TimeService;

public class SingleLockAcquirer extends AbstractLockAcquirer<SingleAuditStageStatus, ExecutableStage> {

    private final LockRepository lockRepository;

    private final CoreConfiguration configuration;


    /**
     * @param lockRepository    lockRepository to persist the lock
     * @param auditReader
     * @param coreConfiguration
     */
    public SingleLockAcquirer(LockRepository lockRepository,
                              SingleAuditReader auditReader,
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
