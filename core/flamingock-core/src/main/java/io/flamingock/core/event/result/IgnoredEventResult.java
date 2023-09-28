package io.flamingock.core.event.result;

public class IgnoredEventResult extends EventSuccessResult {

  public IgnoredEventResult() {
    super(true);
  }

}
