package io.flamingock.core.api;


import io.flamingock.commons.utils.JsonObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;

public class FlamingockMetadata {

    public static final String FILE_PATH = "META-INF/flamingock-metadata.txt";

    private static FlamingockMetadata instance;

    private boolean suppressedProxies;

    private Collection<String> classes;

    public FlamingockMetadata() {
    }

    public FlamingockMetadata(boolean suppressedProxies, Collection<String> classes) {
        this.suppressedProxies = suppressedProxies;
        this.classes = classes;
    }

    public static synchronized FlamingockMetadata getInstance() {
        if (instance == null) {
            instance = loadFromFile();
        }
        return instance;
    }

    private static FlamingockMetadata loadFromFile() {
        ClassLoader classLoader = FlamingockMetadata.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("META-INF/flamingock-metadata.txt")) {
            if (inputStream != null) {
                StringBuilder sb = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                }

                return JsonObjectMapper.DEFAULT_INSTANCE.readValue(sb.toString(), FlamingockMetadata.class);


            } else {
                throw new RuntimeException("annotated-classes.txt not found in META-INF");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    public boolean isSuppressedProxies() {
        return suppressedProxies;
    }

    public void setSuppressedProxies(boolean suppressedProxies) {
        this.suppressedProxies = suppressedProxies;
    }

    public Collection<String> getClasses() {
        return classes;
    }

    public void setClasses(Collection<String> classes) {
        this.classes = classes;
    }


    @Override
    public String toString() {
        return "FlamingockMetadata{" + "suppressedProxies=" + suppressedProxies + ", classes=" + classes + '}';
    }
}
