package io.flamingock.core.util;

import java.util.ArrayList;
import java.util.List;

public final class CollectionUtil {
    private CollectionUtil() {
    }

    public static <T> List<T> assignOrEmpty(List<T> list) {
        return list != null ? list : new ArrayList<>();
    }

}
