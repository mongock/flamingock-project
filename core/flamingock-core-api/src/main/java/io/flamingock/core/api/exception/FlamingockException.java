/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.flamingock.core.api.exception;

import io.flamingock.core.api.validation.ValidationError;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Exception thrown when a Flamingock operation fails.
 */
public class FlamingockException extends RuntimeException {

  private final List<ValidationError> validationErrors;

  public FlamingockException() {
    super();
    this.validationErrors = Collections.emptyList();
  }

  public FlamingockException(Throwable cause) {
    super(cause);
    this.validationErrors = Collections.emptyList();
  }

  public FlamingockException(String message) {
    super(message);
    this.validationErrors = Collections.emptyList();
  }

  public FlamingockException(String formattedMessage, Object... args) {
    super(String.format(formattedMessage, args));
    this.validationErrors = Collections.emptyList();
  }

  public FlamingockException(Throwable cause, String formattedMessage, Object... args) {
    this(String.format(formattedMessage, args), cause);
  }

  public FlamingockException(Throwable cause, String message) {
    super(message, cause);
    this.validationErrors = Collections.emptyList();
  }

  public FlamingockException(List<ValidationError> validationErrors) {
    super(buildErrorMessage(validationErrors));
    this.validationErrors = validationErrors;
  }

  public FlamingockException(Throwable cause, List<ValidationError> validationErrors) {
    super(buildErrorMessage(validationErrors), cause);
    this.validationErrors = validationErrors;
  }

  public List<ValidationError> getValidationErrors() {
    return validationErrors;
  }

  private static String buildErrorMessage(List<ValidationError> validationErrors) {
    if (validationErrors == null || validationErrors.isEmpty()) {
      return "Validation failed";
    }

    return "Validation failed with the following errors:\n" +
           validationErrors.stream()
               .map(ValidationError::getFormattedMessage)
               .collect(Collectors.joining("\n"));
  }
}
