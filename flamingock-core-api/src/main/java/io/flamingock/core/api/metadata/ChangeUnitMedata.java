package io.flamingock.core.api.metadata;

public class ChangeUnitMedata {

    private String className;

    public ChangeUnitMedata() {
    }

    public ChangeUnitMedata(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChangeUnitMedata that = (ChangeUnitMedata) o;

        return className.equals(that.className);
    }

    @Override
    public int hashCode() {
        return className.hashCode();
    }

    @Override
    public String toString() {
        return "ChangeUnitMedata{" + "className='" + className + '\'' +
                '}';
    }
}
