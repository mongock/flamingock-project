package io.flamingock.core.processor.util;

import io.flamingock.internal.util.JsonObjectMapper;
import io.flamingock.internal.common.core.metadata.Constants;
import io.flamingock.internal.common.core.preview.PreviewPipeline;
import io.flamingock.internal.common.core.metadata.FlamingockMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public final class Deserializer {

    private static final Logger logger = LoggerFactory.getLogger(Deserializer.class);

    private static final ClassLoader CLASS_LOADER = PreviewPipeline.class.getClassLoader();


    private Deserializer() {
    }

    /**
     * Reads the preview pipeline from file. It first tries to load the full pipeline,
     * and if not found, falls back to the templated pipeline.
     *
     * @return PreviewPipeline object if found
     * @throws RuntimeException if neither file is found
     */
    public static PreviewPipeline readPreviewPipelineFromFile() {
        return readFileIfExists(Constants.FULL_PIPELINE_FILE_PATH)
                .orElseThrow(() -> new RuntimeException("Flamingock metadata file not found"));
    }

    /**
     * Attempts to read a file and deserialize it into a PreviewPipeline.
     *
     * @param filePath Path to the file inside resources
     * @return An Optional containing the deserialized PreviewPipeline if successful, otherwise empty
     */
    private static Optional<PreviewPipeline> readFileIfExists(String filePath) {
        try (InputStream inputStream = CLASS_LOADER.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                logger.debug("Flamingock pipeline file not found at the specified path: '{}'", filePath);
                return Optional.empty();
            }

            FlamingockMetadata metadata = JsonObjectMapper.DEFAULT_INSTANCE.readValue(inputStream, FlamingockMetadata.class);
            logger.debug("Successfully deserialized Flamingock metadata from file: '{}'", filePath);
            return Optional.of(metadata.getPipeline());
        } catch (IOException e) {
            throw new RuntimeException("Error reading Flamingock metadata file at: " + filePath, e);
        }
    }

}
