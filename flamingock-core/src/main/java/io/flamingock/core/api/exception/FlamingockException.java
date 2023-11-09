package io.flamingock.core.api.exception;

/**
 *
 */
public class FlamingockException extends RuntimeException {

  public FlamingockException() {
    super();
  }

  public FlamingockException(Throwable cause) {
    super(cause);
  }

  public FlamingockException(String message) {
    super(message);
  }

  public FlamingockException(String formattedMessage, Object... args) {
    super(String.format(formattedMessage, args));
  }

  public FlamingockException(Throwable cause, String formattedMessage, Object... args) {
    this(String.format(formattedMessage, args), cause);
  }

  public FlamingockException(Throwable cause, String message) {
    super(message, cause);
  }
}
