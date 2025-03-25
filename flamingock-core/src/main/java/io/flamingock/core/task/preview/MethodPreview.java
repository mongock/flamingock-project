package io.flamingock.core.task.preview;

import java.util.List;

public class MethodPreview {
    private String name;
    private List<String> parameterTypes;

    public MethodPreview() {
    }

    public MethodPreview(String name, List<String> parameterTypes) {
        this.name = name;
        this.parameterTypes = parameterTypes;
    }

    public String getName() {
        return name;
    }

    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setParameterTypes(List<String> parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    @Override
    public String toString() {
        return name + "(" + String.join(", ", parameterTypes) + ")";
    }
}
