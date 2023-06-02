package io.flamingock.oss.core.lock;

import io.flamingock.oss.api.exception.CoreException;

public class LockCheckException extends CoreException {
  public LockCheckException(String s) {
    super(s);
  }

  public LockCheckException() {
    super();
  }
}
