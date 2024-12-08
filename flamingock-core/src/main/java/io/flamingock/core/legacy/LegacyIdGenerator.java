package io.flamingock.core.legacy;

public class LegacyIdGenerator {

    private LegacyIdGenerator() {
    }

    public static String getNewId(String legacyId, String author) {
        return String.format("%s_%s", author, legacyId);
    }
}
