package io.flamingock.core.core.lock;

import io.flamingock.core.api.exception.CoreException;

public class LockException extends CoreException {
  public LockException(Throwable throwable) {
    super(throwable);
  }

  public LockException(String s) {
    super(s);
  }

  public LockException() {
    super();
  }
}
