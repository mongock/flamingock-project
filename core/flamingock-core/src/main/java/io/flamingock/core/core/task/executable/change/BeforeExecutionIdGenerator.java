package io.flamingock.core.core.task.executable.change;

public final class BeforeExecutionIdGenerator {
    private BeforeExecutionIdGenerator(){}

    public static String getId(String baseId) {
        return String.format("%s_%s", baseId, "before");
    }
}
