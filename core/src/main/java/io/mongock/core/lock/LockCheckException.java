package io.mongock.core.lock;

import io.mongock.api.exception.CoreException;

public class LockCheckException extends CoreException {
  public LockCheckException(String s) {
    super(s);
  }

  public LockCheckException() {
    super();
  }
}
