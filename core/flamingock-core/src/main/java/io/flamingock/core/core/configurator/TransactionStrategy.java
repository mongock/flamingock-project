package io.flamingock.core.core.configurator;


public enum TransactionStrategy {

  EXECUTION, CHANGE_UNIT;

  public boolean isTransaction() {
    return this == EXECUTION ||  this == CHANGE_UNIT;
  }

}