package io.mongock.core.transaction;

public interface TransactionWrapper {

    boolean wrapInTransaction(Runnable operation);

}
