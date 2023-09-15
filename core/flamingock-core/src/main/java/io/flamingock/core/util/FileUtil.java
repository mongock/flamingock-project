package io.flamingock.core.util;

import io.flamingock.core.pipeline.Stage;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class FileUtil {

    private FileUtil() {
    }

    public static List<File> loadFilesFromDirectory(String directory) {
        try {
            return Arrays.asList(Objects.requireNonNull(new File(Stage.class.getClassLoader().getResource(directory).toURI())
                    .listFiles()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getFromYamlFile(File file, Class<T> type) {
        try {
            return new Yaml(new Constructor(type, new LoaderOptions())).load(Files.newInputStream(file.toPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getFromMap(Class<T> type, Map<?, ?> source) {
        Yaml yamlWriter = new Yaml();
        StringWriter writer = new StringWriter();
        yamlWriter.dump(source, writer);
        return new Yaml(new Constructor(type, new LoaderOptions()))
                .load(writer.toString());
    }
}
