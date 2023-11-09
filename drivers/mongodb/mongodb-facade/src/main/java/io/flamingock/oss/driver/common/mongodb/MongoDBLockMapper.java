package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.community.internal.lock.LockEntry;
import io.flamingock.core.lock.LockStatus;
import io.flamingock.core.util.TimeUtil;

import java.util.function.Supplier;

import static io.flamingock.community.internal.lock.LockEntryField.EXPIRES_AT_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.KEY_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.OWNER_FIELD;
import static io.flamingock.community.internal.lock.LockEntryField.STATUS_FIELD;

public class MongoDBLockMapper<DOCUMENT_WRAPPER extends DocumentWrapper> {

    private final Supplier<DOCUMENT_WRAPPER> documentckSupplier;

    public MongoDBLockMapper(Supplier<DOCUMENT_WRAPPER> documentCreator) {
        this.documentckSupplier = documentCreator;
    }

    public DOCUMENT_WRAPPER toDocument(LockEntry lockEntry) {
        DOCUMENT_WRAPPER document = documentckSupplier.get();
        document.append(KEY_FIELD, lockEntry.getKey());
        document.append(OWNER_FIELD, lockEntry.getOwner());
        document.append(STATUS_FIELD, lockEntry.getStatus().name());
        document.append(EXPIRES_AT_FIELD, TimeUtil.toDate(lockEntry.getExpiresAt()));
        return document;
    }

    public LockEntry fromDocument(DocumentWrapper entry) {
        return new LockEntry(
                entry.getString(KEY_FIELD),
                entry.containsKey(STATUS_FIELD) ? LockStatus.valueOf(entry.getString(STATUS_FIELD)) : null,
                entry.getString(OWNER_FIELD),
                TimeUtil.toLocalDateTime(entry.get(STATUS_FIELD)));

    }
}
