package io.mongock.runner.standalone;

public final class MongockStandalone {

    private MongockStandalone() {
    }

    public static MongockStandaloneRunnerBuilder builder() {
        return new MongockStandaloneRunnerBuilder();
    }


}
