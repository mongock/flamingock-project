package io.flamingock.core.api.exception;

/**
 *
 */
public class CoreException extends RuntimeException {

  public CoreException() {
    super();
  }

  public CoreException(Throwable cause) {
    super(cause);
  }

  public CoreException(String message) {
    super(message);
  }

  public CoreException(String formattedMessage, Object... args) {
    super(String.format(formattedMessage, args));
  }

  public CoreException(Throwable cause, String formattedMessage, Object... args) {
    this(String.format(formattedMessage, args), cause);
  }

  public CoreException(Throwable cause, String message) {
    super(message, cause);
  }
}
