package io.flamingock.internal.common.core.preview;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public enum StageType {
    DEFAULT,
    MONGOCK_LEGACY("mongock-legacy"),
    IMPORTER("importer");

    private final Set<String> aliases;

    StageType(String... aliases) {
        this.aliases = new HashSet<>(Arrays.asList(aliases));
    }

    public static StageType from(String name) {
        if(name == null || name.isEmpty()) {
            return DEFAULT;
        }
        for (StageType stageType : StageType.values()) {
            if (stageType.aliases.contains(name)) {
                return stageType;
            }
        }
        throw new IllegalArgumentException("No such stage type: " + name);
    }
}
