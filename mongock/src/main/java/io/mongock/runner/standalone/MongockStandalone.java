package io.mongock.runner.standalone;

public final class MongockStandalone {

    private MongockStandalone() {
    }

    public static MongockStandaloneBuilder builder() {
        return new MongockStandaloneBuilder();
    }


}
