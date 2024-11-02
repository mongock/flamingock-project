package io.flamingock.core.api;

import java.util.Collection;

public class FlamingockConfiguration {

    public static final String FILE_PATH = "META-INF/flamingock-configuration.txt";


    private boolean suppressProxies;

    private Collection<String> classes;

    public FlamingockConfiguration() {
    }

    public FlamingockConfiguration(boolean suppressProxies,
                                   Collection<String> classes) {
        this.suppressProxies = suppressProxies;
        this.classes = classes;
    }

    public boolean isSuppressProxies() {
        return suppressProxies;
    }

    public Collection<String> getClasses() {
        return classes;
    }

    public void setSuppressProxies(boolean suppressProxies) {
        this.suppressProxies = suppressProxies;
    }

    public void setClasses(Collection<String> classes) {
        this.classes = classes;
    }
}
