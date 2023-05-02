package io.mongock.runner.standalone;

public final class MongockStandalone {

    private MongockStandalone() {
    }
    public static RunnerStandaloneBuilder builder() {
        return new RunnerStandaloneBuilderImpl(new ExecutorBuilderDefault(), new MongockConfiguration());
    }


}
