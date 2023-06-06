package io.flamingock.oss.driver.common.mongodb;

import io.flamingock.core.core.audit.domain.AuditEntryStatus;
import io.flamingock.core.core.lock.LockStatus;
import io.flamingock.core.core.util.TimeUtil;
import io.flamingock.oss.internal.persistence.LockEntry;
import io.flamingock.oss.internal.persistence.LockEntryField;

import java.util.function.Supplier;

import static io.flamingock.oss.internal.persistence.AuditEntryField.KEY_STATE;
import static io.flamingock.oss.internal.persistence.LockEntryField.*;

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
