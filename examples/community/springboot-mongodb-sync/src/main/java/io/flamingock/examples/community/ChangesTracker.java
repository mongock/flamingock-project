package io.flamingock.examples.community;

import java.util.ArrayList;
import java.util.List;

public final class ChangesTracker {

    private static final List<String> changes = new ArrayList<>();

    private ChangesTracker() {
    }

    public static void clear() {
        changes.clear();
    }

    public static void add(String change) {
        changes.add(change);
    }

    public static int size() {
        return changes.size();
    }

    public static String get(int i) {
        return changes.get(i);
    }
}
