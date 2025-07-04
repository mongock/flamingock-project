package io.flamingock.core.processor;

import io.flamingock.api.annotations.Flamingock;
import io.flamingock.api.annotations.Stage;
import io.flamingock.api.StageType;
import io.flamingock.api.annotations.SystemStage;
import io.flamingock.core.processor.util.LoggerPreProcessor;
import io.flamingock.internal.common.core.preview.AbstractPreviewTask;
import io.flamingock.internal.common.core.preview.PreviewPipeline;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class PipelinePreProcessorTest {

    @TempDir
    Path tempDir;
    
    private FlamingockAnnotationProcessor processor;
    private File pipelineFile;

    @BeforeEach
    void setUp() throws Exception {
        processor = spy(new FlamingockAnnotationProcessor());
        pipelineFile = tempDir.resolve("pipeline.yaml").toFile();
        
        // Mock dependencies
        LoggerPreProcessor mockLogger = mock(LoggerPreProcessor.class);
        doNothing().when(processor).init(any());
        setPrivateField("pipelineFile", pipelineFile);
        setPrivateField("sourceRoots", Collections.singletonList("src/main/java"));
        setPrivateField("resourcesRoot", "src/main/resources");
        setPrivateField("logger", mockLogger);
    }

    /**
     * Tests the pipeline processor behavior when no configuration source is provided.
     * 
     * <p><b>Scenario:</b> Pipeline processor is invoked without any configuration source</p>
     * <p><b>Given:</b> No pipeline.yaml file exists and no @Pipeline annotation is provided</p>
     * <p><b>When:</b> The processor attempts to build a pipeline</p>
     * <p><b>Then:</b> A RuntimeException should be thrown indicating no configuration found</p>
     */
    @Test
    @DisplayName("Given no pipeline file and no @Pipeline annotation, when processing pipeline, then should throw RuntimeException")
    void SHOULD_throwException_WHEN_noFileAndNoPipelineAnnotation() throws Exception {
        // Given - no file exists, no annotation
        Map<String, List<AbstractPreviewTask>> emptyMap = new HashMap<>();
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> 
            invokeGetPipelineFromProcessChanges(emptyMap, null));
        
        // Verify the cause is RuntimeException with expected message
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("@Flamingock annotation is mandatory"));
    }

    /**
     * Tests the pipeline processor using file-based configuration with @Flamingock annotation.
     * 
     * <p><b>Scenario:</b> Pipeline processor processes file-based configuration with available change units</p>
     * <p><b>Given:</b> A pipeline.yaml file exists and @Flamingock annotation is provided with empty pipelineFile</p>
     * <p><b>When:</b> The processor builds the pipeline with @Flamingock annotation</p>
     * <p><b>Then:</b> A valid PreviewPipeline should be created from the file configuration</p>
     */
    @Test
    @DisplayName("Given pipeline file exists and @Flamingock annotation provided, when pipelineFile is empty, then should use default file configuration")
    void SHOULD_useFileOnly_WHEN_fileProvidedAndFlamingockAnnotationWithoutPipelineFile() throws Exception {
        // Given
        createPipelineFile();
        Map<String, List<AbstractPreviewTask>> changeUnitsMap = createMockChangeUnitsMap();
        Flamingock mockAnnotation = createMockPipelineAnnotationWithSourcesPackage();
        
        // When
        PreviewPipeline result = invokeGetPipelineFromProcessChanges(changeUnitsMap, mockAnnotation);
        
        // Then
        assertNotNull(result);
    }


    /**
     * Tests the pipeline processor using only annotation-based configuration.
     * 
     * <p><b>Scenario:</b> Pipeline processor processes annotation-based configuration exclusively</p>
     * <p><b>Given:</b> No pipeline.yaml file exists but @Pipeline annotation is provided with change units</p>
     * <p><b>When:</b> The processor builds the pipeline from annotation configuration</p>
     * <p><b>Then:</b> A valid PreviewPipeline should be created from the annotation configuration</p>
     */
    @Test
    @DisplayName("Given no pipeline file but @Pipeline annotation provided, when processing pipeline, then should use annotation configuration")
    void SHOULD_usePipelineAnnotation_WHEN_noFileAndPipelineAnnotationAndChangeUnitsProvided() throws Exception {
        // Given - no file, annotation provided
        Flamingock pipelineAnnotation = createMockPipelineAnnotationWithSourcesPackage();
        Map<String, List<AbstractPreviewTask>> changeUnitsMap = createMockChangeUnitsMap();
        
        // When
        PreviewPipeline result = invokeGetPipelineFromProcessChanges(changeUnitsMap, pipelineAnnotation);
        
        // Then
        assertNotNull(result);
    }

    /**
     * Tests the pipeline processor priority when both configuration sources are available.
     * 
     * <p><b>Scenario:</b> Pipeline processor handles conflicting configuration sources with annotation priority</p>
     * <p><b>Given:</b> Both pipeline.yaml file and @Pipeline annotation exist with change units available</p>
     * <p><b>When:</b> The processor builds the pipeline with both configuration sources</p>
     * <p><b>Then:</b> The @Pipeline annotation should take priority and a warning should be logged</p>
     */
    @Test
    @DisplayName("Given both pipeline file and @Pipeline annotation exist, when processing pipeline, then should prioritize annotation and warn")
    void SHOULD_usePipelineAnnotationAndWarn_WHEN_bothFileAndPipelineAnnotationAndChangeUnitsProvided() throws Exception {
        // Given
        createPipelineFile();
        Flamingock pipelineAnnotation = createMockPipelineAnnotationWithSourcesPackage();
        Map<String, List<AbstractPreviewTask>> changeUnitsMap = createMockChangeUnitsMap();
        
        // When
        PreviewPipeline result = invokeGetPipelineFromProcessChanges(changeUnitsMap, pipelineAnnotation);
        
        // Then
        assertNotNull(result);
    }

    /**
     * Tests that annotation with both pipelineFile and stages throws exception.
     */
    @Test
    @DisplayName("Given @Flamingock with both pipelineFile and stages, when processing pipeline, then should throw RuntimeException")
    void SHOULD_throwException_WHEN_annotationHasBothFileAndStages() throws Exception {
        // Given
        Map<String, List<AbstractPreviewTask>> emptyMap = new HashMap<>();
        Flamingock invalidAnnotation = createMockAnnotationWithBothFileAndStages();
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> 
            invokeGetPipelineFromProcessChanges(emptyMap, invalidAnnotation));
        
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("cannot have both pipelineFile and stages"));
    }

    /**
     * Tests that annotation with neither pipelineFile nor stages throws exception.
     */
    @Test
    @DisplayName("Given @Flamingock with neither pipelineFile nor stages, when processing pipeline, then should throw RuntimeException")
    void SHOULD_throwException_WHEN_annotationHasNeitherFileNorStages() throws Exception {
        // Given
        Map<String, List<AbstractPreviewTask>> emptyMap = new HashMap<>();
        Flamingock emptyAnnotation = createMockAnnotationWithNeitherFileNorStages();
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> 
            invokeGetPipelineFromProcessChanges(emptyMap, emptyAnnotation));
        
        assertInstanceOf(RuntimeException.class, exception.getCause());
        assertTrue(exception.getCause().getMessage().contains("must specify either pipelineFile OR stages"));
    }

    /**
     * Tests that annotation with only pipelineFile works correctly.
     */
    @Test
    @DisplayName("Given @Flamingock with only pipelineFile, when processing pipeline, then should use file configuration")
    void SHOULD_useFileConfiguration_WHEN_annotationHasOnlyPipelineFile() throws Exception {
        // Given
        createPipelineFile();
        Map<String, List<AbstractPreviewTask>> changeUnitsMap = createMockChangeUnitsMap();
        Flamingock fileOnlyAnnotation = createMockAnnotationWithOnlyPipelineFile();
        
        // When
        PreviewPipeline result = invokeGetPipelineFromProcessChanges(changeUnitsMap, fileOnlyAnnotation);
        
        // Then
        assertNotNull(result);
    }

    private void createPipelineFile() throws IOException {
        String yamlContent = "pipeline:\n" +
                "  systemStage:\n" +
                "    sourcesPackage: com.example.system\n" +
                "  stages:\n" +
                "    - name: test-stage\n" +
                "      sourcesPackage: com.example.changes\n";
        java.nio.file.Files.write(pipelineFile.toPath(), yamlContent.getBytes());
    }
    
    private void createPipelineFileWithoutSourcesPackage() throws IOException {
        String yamlContent = "pipeline:\n" +
                "  stages:\n" +
                "    - name: test-stage\n" +
                "      resourcesDir: templates\n";
        java.nio.file.Files.write(pipelineFile.toPath(), yamlContent.getBytes());
    }

    private Flamingock createMockPipelineAnnotationWithSourcesPackage() {
        return new Flamingock() {
            @Override
            public SystemStage systemStage() {
                return new SystemStage() {
                    @Override
                    public String sourcesPackage() { return "com.example.system"; }
                    @Override
                    public String resourcesDir() { return ""; }
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return SystemStage.class;
                    }
                };
            }

            @Override
            public Stage[] stages() {
                return new Stage[]{
                    new Stage() {
                        @Override
                        public String name() { return "test-stage"; }
                        @Override
                        public String description() { return ""; }
                        @Override
                        public StageType type() { return StageType.DEFAULT; }
                        @Override
                        public String sourcesPackage() { return "com.example.changes"; }
                        @Override
                        public String resourcesDir() { return ""; }
                        @Override
                        public Class<? extends java.lang.annotation.Annotation> annotationType() {
                            return Stage.class;
                        }
                    }
                };
            }

            @Override
            public String pipelineFile() { return ""; }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Flamingock.class;
            }
        };
    }

    private Flamingock createMockAnnotationWithBothFileAndStages() {
        return new Flamingock() {
            @Override
            public SystemStage systemStage() {
                return new SystemStage() {
                    @Override
                    public String sourcesPackage() { return "com.example.system"; }
                    @Override
                    public String resourcesDir() { return ""; }
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return SystemStage.class;
                    }
                };
            }

            @Override
            public Stage[] stages() {
                return new Stage[]{
                    new Stage() {
                        @Override
                        public String name() { return "test-stage"; }
                        @Override
                        public String description() { return ""; }
                        @Override
                        public StageType type() { return StageType.DEFAULT; }
                        @Override
                        public String sourcesPackage() { return "com.example.changes"; }
                        @Override
                        public String resourcesDir() { return ""; }
                        @Override
                        public Class<? extends java.lang.annotation.Annotation> annotationType() {
                            return Stage.class;
                        }
                    }
                };
            }

            @Override
            public String pipelineFile() { return "test-pipeline.yaml"; }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Flamingock.class;
            }
        };
    }

    private Flamingock createMockAnnotationWithNeitherFileNorStages() {
        return new Flamingock() {
            @Override
            public SystemStage systemStage() {
                return new SystemStage() {
                    @Override
                    public String sourcesPackage() { return ""; }
                    @Override
                    public String resourcesDir() { return ""; }
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return SystemStage.class;
                    }
                };
            }

            @Override
            public Stage[] stages() {
                return new Stage[0]; // Empty array
            }

            @Override
            public String pipelineFile() { return ""; }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Flamingock.class;
            }
        };
    }

    private Flamingock createMockAnnotationWithOnlyPipelineFile() {
        return new Flamingock() {
            @Override
            public SystemStage systemStage() {
                return new SystemStage() {
                    @Override
                    public String sourcesPackage() { return ""; }
                    @Override
                    public String resourcesDir() { return ""; }
                    @Override
                    public Class<? extends java.lang.annotation.Annotation> annotationType() {
                        return SystemStage.class;
                    }
                };
            }

            @Override
            public Stage[] stages() {
                return new Stage[0]; // Empty array
            }

            @Override
            public String pipelineFile() { return pipelineFile.getAbsolutePath(); }

            @Override
            public Class<? extends java.lang.annotation.Annotation> annotationType() {
                return Flamingock.class;
            }
        };
    }

    private Map<String, List<AbstractPreviewTask>> createMockChangeUnitsMap() {
        Map<String, List<AbstractPreviewTask>> map = new HashMap<>();
        AbstractPreviewTask mockTask = mock(AbstractPreviewTask.class);
        map.put("com.example.changes", Collections.singletonList(mockTask));
        map.put("com.example.system", Collections.singletonList(mockTask));
        return map;
    }

    private PreviewPipeline invokeGetPipelineFromProcessChanges(Map<String, List<AbstractPreviewTask>> changeUnits, Flamingock annotation) throws Exception {
        Method method = FlamingockAnnotationProcessor.class.getDeclaredMethod(
            "getPipelineFromProcessChanges", Map.class, Flamingock.class);
        method.setAccessible(true);
        return (PreviewPipeline) method.invoke(processor, changeUnits, annotation);
    }

    private void setPrivateField(String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = FlamingockAnnotationProcessor.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(processor, value);
    }
}