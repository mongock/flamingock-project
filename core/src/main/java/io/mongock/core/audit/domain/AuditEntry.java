package io.mongock.core.audit.domain;

import java.time.LocalDateTime;

public class AuditEntry {

    private final String executionId;

    private final String changeId;

    private final String author;
    private final LocalDateTime timestamp;//todo change name

    private final AuditEntryStatus state;

//  private final ChangeType type;

    private final String changeLogClass;//todo change name

    private final String changeSetMethod;//todo change name

    private final Object metadata;

    private final long executionMillis;

    private final String executionHostname;

    private final Throwable errorTrace;

    public static AuditEntry instance(String executionId,
                                      String changeId,
                                      String author,
                                      LocalDateTime timestamp,
                                      AuditEntryStatus state,
                                      String changeLogClass,
                                      String changeSetMethod,
                                      long executionMillis,
                                      String executionHostname,
                                      Object metadata) {
        return new AuditEntry(executionId,
                changeId,
                author,
                timestamp,
                state,
                changeLogClass,
                changeSetMethod,
                executionMillis,
                executionHostname,
                metadata,
                null);
    }

  public static AuditEntry withError(String executionId,
                                     String changeId,
                                     String author,
                                     LocalDateTime timestamp,
                                     AuditEntryStatus state,
                                     String changeLogClass,
                                     String changeSetMethod,
                                     long executionMillis,
                                     String executionHostname,
                                     Object metadata,
                                     Throwable errorTrace) {
    return new AuditEntry(executionId,
            changeId,
            author,
            timestamp,
            state,
            changeLogClass,
            changeSetMethod,
            executionMillis,
            executionHostname,
            metadata,
            errorTrace);
  }

    protected AuditEntry(String executionId,
                      String changeId,
                      String author,
                      LocalDateTime timestamp,
                      AuditEntryStatus state,
                      String changeLogClass,
                      String changeSetMethod,
                      long executionMillis,
                      String executionHostname,
                      Object metadata,
                         Throwable errorTrace) {
        this.executionId = executionId;
        this.changeId = changeId;
        this.author = author;
        this.timestamp = timestamp;
        this.state = state;
        this.changeLogClass = changeLogClass;
        this.changeSetMethod = changeSetMethod;
        this.metadata = metadata;
        this.executionMillis = executionMillis;
        this.executionHostname = executionHostname;
        this.errorTrace = errorTrace;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getChangeId() {
        return changeId;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public AuditEntryStatus getState() {
        return state;
    }

    public String getChangeLogClass() {
        return changeLogClass;
    }

    public String getChangeSetMethod() {
        return changeSetMethod;
    }

    public Object getMetadata() {
        return metadata;
    }

    public long getExecutionMillis() {
        return executionMillis;
    }

    public String getExecutionHostname() {
        return executionHostname;
    }

    public Throwable getErrorTrace() {
        return errorTrace;
    }


    public enum Field {
        KEY_EXECUTION_ID("executionId"),
        KEY_CHANGE_ID("changeId"),
        KEY_AUTHOR("author"),
        KEY_TIMESTAMP("timestamp"),
        KEY_STATE("state"),
        KEY_TYPE("type"),
        KEY_CHANGELOG_CLASS("changeLogClass"),
        KEY_CHANGESET_METHOD("changeSetMethod"),
        KEY_METADATA("metadata"),
        KEY_EXECUTION_MILLIS("executionMillis"),
        KEY_EXECUTION_HOSTNAME("executionHostname"),
        KEY_ERROR_TRACE("errorTrace"),
        KEY_SYSTEM_CHANGE("systemChange");

        private final String value;

        Field(String value) {
            this.value = value;
        }

        public String value() {
            return value;
        }

    }
}
