package io.flamingock.core.engine.cloud.lock;

import io.flamingock.core.engine.cloud.lock.client.LockServiceClient;
import io.flamingock.core.engine.lock.LockAcquisition;
import io.flamingock.core.engine.lock.LockKey;
import io.flamingock.core.engine.lock.LockService;
import io.flamingock.core.engine.lock.LockServiceException;
import io.flamingock.core.runner.RunnerId;
import io.flamingock.core.util.ConnectionException;

public class CloudLockService implements LockService {


    private final LockServiceClient client;

    public CloudLockService(LockServiceClient client) {
        this.client = client;
    }

    @Override
    public LockAcquisition extendLock(LockKey key, RunnerId owner, long leaseMillis) throws LockServiceException {
        try {
            LockResponse lockExtension = client.extendLock(key, owner, new LockExtensionRequest(leaseMillis));
            return new LockAcquisition(RunnerId.fromString(lockExtension.getOwner()), lockExtension.getLockAcquiredForMillis());

        } catch (ConnectionException ex) {
            throw new LockServiceException(
                    ex.getRequestString(),
                    ex.getBodyString(),
                    String.format("Error extending lock[%s] for runner[%s]: %s", key.toString(), owner.toString(), ex.getMessage())
            );
        } catch (Throwable ex) {
            throw new LockServiceException(
                    "n/a",
                    "n/a",
                    String.format("Unexpected error extending lock[%s] for runner[%s]: %s", key.toString(), owner.toString(), ex.getMessage())
            );
        }
    }

    @Override
    public LockAcquisition getLock(LockKey lockKey) {
        try {
            LockResponse response = client.getLock(lockKey);
            return new LockAcquisition(RunnerId.fromString(response.getOwner()), response.getLockAcquiredForMillis());

        } catch (ConnectionException ex) {
            throw new LockServiceException(
                    ex.getRequestString(),
                    ex.getBodyString(),
                    String.format("Error retrieving lock[%s]: %s", lockKey.toString(), ex.getMessage())
            );
        } catch (Throwable ex) {
            throw new LockServiceException(
                    "n/a",
                    "n/a",
                    String.format("Unexpected error retrieving lock[%s]: %s", lockKey.toString(), ex.getMessage())
            );
        }
    }

    @Override
    public void releaseLock(LockKey lockKey, RunnerId owner) {
        try {
            client.releaseLock(lockKey, owner);

        } catch (ConnectionException ex) {
            throw new LockServiceException(
                    ex.getRequestString(),
                    ex.getBodyString(),
                    String.format("Error releasing lock[%s]: %s", lockKey.toString(), ex.getMessage())
            );
        } catch (Throwable ex) {
            throw new LockServiceException(
                    "n/a",
                    "n/a",
                    String.format("Unexpected error releasing lock[%s]: %s", lockKey.toString(), ex.getMessage())
            );
        }

    }

}
