/*
 * Copyright 2023 Flamingock (https://oss.flamingock.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
