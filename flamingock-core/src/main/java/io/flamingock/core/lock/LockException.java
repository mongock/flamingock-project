package io.flamingock.core.lock;

import io.flamingock.core.api.exception.FlamingockException;

public class LockException extends FlamingockException {
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
