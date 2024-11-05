package io.flamingock.core.api;



import java.util.Collection;

public class FlamingockMetadata {

    public static final String FILE_PATH = "META-INF/flamingock-metadata.txt";

    private boolean suppressedProxies;

    private Collection<String> classes;

    public FlamingockMetadata() {
    }

    public FlamingockMetadata(boolean suppressedProxies,
                              Collection<String> classes) {
        this.suppressedProxies = suppressedProxies;
        this.classes = classes;
    }

    public boolean isSuppressedProxies() {
        return suppressedProxies;
    }

    public Collection<String> getClasses() {
        return classes;
    }

    public void setSuppressedProxies(boolean suppressedProxies) {
        this.suppressedProxies = suppressedProxies;
    }

    public void setClasses(Collection<String> classes) {
        this.classes = classes;
    }


    @Override
    public String toString() {
        return "FlamingockMetadata{" + "suppressedProxies=" + suppressedProxies +
                ", classes=" + classes +
                '}';
    }
}
