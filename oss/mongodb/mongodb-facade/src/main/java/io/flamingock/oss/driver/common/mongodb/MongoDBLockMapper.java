package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.oss.internal.persistence.LockEntry;
import io.flamingock.oss.internal.persistence.LockEntryField;

import java.util.function.Supplier;

public class MongoDBLockMapper<DOCUMENT_WRAPPER extends DocumentWrapper> {

    private final Supplier<DOCUMENT_WRAPPER> documentckSupplier;

    public MongoDBLockMapper(Supplier<DOCUMENT_WRAPPER> documentCreator) {
        this.documentckSupplier = documentCreator;
    }

    public DOCUMENT_WRAPPER toDocument(LockEntry lockEntry) {
        DOCUMENT_WRAPPER document = documentckSupplier.get();
        document.append(LockEntryField.KEY_FIELD, lockEntry.getKey());
        document.append(LockEntryField.OWNER_FIELD, lockEntry.getOwner());
        document.append(LockEntryField.STATUS_FIELD, lockEntry.getStatus());
        document.append(LockEntryField.EXPIRES_AT_FIELD, TimeUtil.toDate(lockEntry.getExpiresAt()));
        return document;
    }

    public LockEntry fromDocument(DocumentWrapper entry) {
        return new LockEntry(
                entry.getString(LockEntryField.KEY_FIELD),
                entry.getString(LockEntryField.OWNER_FIELD),
                entry.getString(LockEntryField.STATUS_FIELD),
                TimeUtil.toLocalDateTime(entry.get(LockEntryField.STATUS_FIELD)));

    }
}
