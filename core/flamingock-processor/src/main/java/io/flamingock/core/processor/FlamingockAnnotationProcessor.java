package io.flamingock.core.processor;

import io.flamingock.api.annotations.ChangeUnit;
import io.flamingock.api.annotations.Flamingock;
import io.flamingock.api.annotations.Stage;
import io.flamingock.api.annotations.SystemStage;
import io.flamingock.internal.common.core.preview.PreviewPipeline;
import io.flamingock.internal.common.core.preview.PreviewStage;
import io.flamingock.core.processor.util.AnnotationFinder;
import io.flamingock.core.processor.util.LoggerPreProcessor;
import io.flamingock.core.processor.util.Serializer;
import io.flamingock.internal.common.core.preview.AbstractPreviewTask;
import io.flamingock.api.StageType;
import io.flamingock.internal.common.core.preview.SystemPreviewStage;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Annotation processor for Flamingock that generates metadata files containing information 
 * about templated and annotated changes. The processor requires a mandatory {@link Flamingock} 
 * annotation to configure the pipeline.
 * <p>
 * <h2>@Flamingock Annotation Configuration</h2>
 * The processor supports two mutually exclusive configuration modes:
 * <ul>
 *     <li><b>File-based configuration:</b> Uses {@code pipelineFile} to reference a YAML pipeline definition</li>
 *     <li><b>Annotation-based configuration:</b> Uses {@code stages} array to define the pipeline inline</li>
 * </ul>
 * 
 * <h3>Pipeline File Resolution</h3>
 * When using {@code pipelineFile}, the processor provides resource resolution
 * supporting multiple file locations:
 * 
 * <h4>Examples:</h4>
 * <pre>{@code
 * // Absolute file path - highest priority
 * @Flamingock(pipelineFile = "/path/to/external/pipeline.yaml")
 * // Uses direct file system path
 * 
 * // Relative file path - second priority (relative to working directory)
 * @Flamingock(pipelineFile = "config/flamingock-pipeline.yaml")
 * // Resolves relative to current working directory, NOT as classpath resource
 * 
 * // Classpath resource - fallback if file doesn't exist relative to working directory
 * @Flamingock(pipelineFile = "flamingock/pipeline.yaml")
 * // If "flamingock/pipeline.yaml" doesn't exist in working directory,
 * // then tries: src/main/resources/flamingock/pipeline.yaml
 * // then tries: src/test/resources/flamingock/pipeline.yaml
 * 
 * // Resource with explicit "resources/" prefix (automatically stripped)
 * @Flamingock(pipelineFile = "resources/flamingock/pipeline.yaml")
 * // First tries: "resources/flamingock/pipeline.yaml" relative to working directory
 * // If not found, strips "resources/" prefix and tries classpath resolution:
 * // src/main/resources/flamingock/pipeline.yaml or src/test/resources/flamingock/pipeline.yaml
 * }</pre>
 *
 * <h4>Resolution Order (stops at first match):</h4>
 * <ol>
 *     <li><b>Direct file path:</b> {@code [pipelineFile]} (absolute or relative to working directory)</li>
 *     <li><b>Main resources:</b> {@code src/main/resources/[pipelineFile]}</li>
 *     <li><b>Test resources:</b> {@code src/test/resources/[pipelineFile]}</li>
 *     <li><b>Main resources (stripped):</b> If path starts with "resources/", strips prefix and tries {@code src/main/resources/[remaining-path]}</li>
 *     <li><b>Test resources (stripped):</b> If path starts with "resources/", strips prefix and tries {@code src/test/resources/[remaining-path]}</li>
 * </ol>
 * <p>
 * <b>Important:</b> Working directory files always take precedence over classpath resources.
 * If both {@code ./config/pipeline.yaml} and {@code src/main/resources/config/pipeline.yaml} exist,
 * the working directory file is used.
 *
 * <h3>Annotation-based Configuration</h3>
 * <pre>{@code
 * @Flamingock(stages = {
 *     @Stage(name = "init", type = StageType.BEFORE, sourcesPackage = "com.example.init"),
 *     @Stage(name = "migration", type = StageType.DEFAULT, sourcesPackage = "com.example.migrations")
 * })
 * }</pre>
 *
 * <h2>Processing Phases</h2>
 * <ul>
 *     <li><b>Initialization:</b> Sets up resource paths and validates @Flamingock annotation</li>
 *     <li><b>Processing:</b> Generates {@code META-INF/flamingock/metadata-full.json} with complete pipeline metadata</li>
 * </ul>
 *
 * <h2>Validation Rules</h2>
 * <ul>
 *     <li>@Flamingock annotation is mandatory</li>
 *     <li>Must specify either {@code pipelineFile} OR {@code stages} (mutually exclusive)</li>
 *     <li>SystemStage can only be used with annotation-based configuration (not with pipelineFile)</li>
 * </ul>
 *
 * <h2>Supported Annotations</h2>
 * <ul>
 *     <li>{@link Flamingock} - Mandatory pipeline configuration</li>
 *     <li>{@link ChangeUnit} - Represents a change unit defined within the code</li>
 *     <li>io.mongock.api.annotations.ChangeUnit - Legacy change unit support</li>
 * </ul>
 *
 * @author Antonio
 * @version 2.0
 * @since Flamingock v1.x
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class FlamingockAnnotationProcessor extends AbstractProcessor {

    private static final String RESOURCES_PATH_ARG = "resources";

    private static final String SOURCES_PATH_ARG = "sources";

    private static final String DEFAULT_RESOURCES_PATH = "src/main/resources";

    private static final List<String> DEFAULT_SOURCE_DIRS = Arrays.asList(
            "src/main/java", "src/main/kotlin", "src/main/scala", "src/main/groovy"
    );


    private static final boolean hasProcessed = false;

    private String resourcesRoot = null;
    private List<String> sourceRoots = null;
    private LoggerPreProcessor logger;

    public FlamingockAnnotationProcessor(){}

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new LoggerPreProcessor(processingEnv);
        logger.info("Starting Flamingock annotation processor initialization.");
        resourcesRoot = getResourcesRoot();
        sourceRoots = getSourcesPathList();


        logger.info("Initialization completed. Pipeline will be processed with @Flamingock annotation.");
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<>(Arrays.asList("sources", "resources"));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                Flamingock.class.getName(),
                ChangeUnit.class.getName()
        ));
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (roundEnv.processingOver()) {
            logger.info("Final processing round detected - skipping execution.");
            return false;
        }
        if (hasProcessed) {
            logger.info("Changes already processed - skipping execution.");
            return true;
        }

        AnnotationFinder annotationFinder = new AnnotationFinder(roundEnv, logger);
        PreviewPipeline pipeline = getPipelineFromProcessChanges(
                annotationFinder.getCodedChangeUnitsMapByPackage(),
                annotationFinder.getPipelineAnnotation()
        );
        Serializer serializer = new Serializer(processingEnv, logger);
        serializer.serializeFullPipeline(pipeline);
        logger.info("Finished processing annotated classes and generating metadata.");
        return true;
    }

    private PreviewPipeline getPipelineFromProcessChanges(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, Flamingock pipelineAnnotation) {
        if (codedChangeUnitsByPackage == null) {
            codedChangeUnitsByPackage = new HashMap<>();
        }
        
        if (pipelineAnnotation == null) {
            throw new RuntimeException("@Flamingock annotation is mandatory. Please annotate a class with @Flamingock to configure the pipeline.");
        }
        
        boolean hasFileInAnnotation = !pipelineAnnotation.pipelineFile().isEmpty();
        boolean hasStagesInAnnotation = pipelineAnnotation.stages().length > 0;
        boolean hasSystemStage = !pipelineAnnotation.systemStage().sourcesPackage().isEmpty();
        
        // Validate mutually exclusive modes
        if (hasFileInAnnotation && hasStagesInAnnotation) {
            throw new RuntimeException("@Flamingock annotation cannot have both pipelineFile and stages configured. Choose one: either specify pipelineFile OR stages.");
        }
        
        if (!hasFileInAnnotation && !hasStagesInAnnotation) {
            throw new RuntimeException("@Flamingock annotation must specify either pipelineFile OR stages configuration.");
        }
        
        // SystemStage only allowed with stages, not with pipelineFile
        if (hasSystemStage && !hasStagesInAnnotation) {
            throw new RuntimeException("SystemStage can only be configured when stages are provided, not with pipelineFile.");
        }
        
        if (hasFileInAnnotation) {
            logger.info("Reading flamingock pipeline from file specified in @Flamingock annotation: '" + pipelineAnnotation.pipelineFile() + "'");
            File specifiedPipelineFile = resolvePipelineFile(pipelineAnnotation.pipelineFile());
            return buildPipelineFromSpecifiedFile(specifiedPipelineFile, codedChangeUnitsByPackage);
        } else {
            logger.info("Reading flamingock pipeline from @Flamingock annotation stages configuration");
            return buildPipelineFromAnnotation(pipelineAnnotation, codedChangeUnitsByPackage);
        }
    }
    
    private PreviewPipeline buildPipelineFromAnnotation(Flamingock pipelineAnnotation, Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage) {
        List<PreviewStage> stages = new ArrayList<>();
        
        for (Stage stageAnnotation : pipelineAnnotation.stages()) {
            PreviewStage stage = mapAnnotationToStage(codedChangeUnitsByPackage, stageAnnotation);
            stages.add(stage);
        }
        
        Optional<SystemPreviewStage> systemStage = getSystemStageFromAnnotation(codedChangeUnitsByPackage, pipelineAnnotation.systemStage());
        
        return systemStage
                .map(previewStage -> new PreviewPipeline(previewStage, stages))
                .orElseGet(() -> new PreviewPipeline(stages));
    }
    
    private PreviewStage mapAnnotationToStage(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, Stage stageAnnotation) {
        String sourcesPackage = stageAnnotation.sourcesPackage().isEmpty() ? null : stageAnnotation.sourcesPackage();
        Collection<AbstractPreviewTask> changeUnitClasses = sourcesPackage != null ? codedChangeUnitsByPackage.get(sourcesPackage) : null;
        
        return PreviewStage.defaultBuilder(stageAnnotation.type())
                .setName(stageAnnotation.name())
                .setDescription(stageAnnotation.description().isEmpty() ? null : stageAnnotation.description())
                .setSourcesRoots(sourceRoots)
                .setSourcesPackage(sourcesPackage)
                .setResourcesRoot(resourcesRoot)
                .setResourcesDir(stageAnnotation.resourcesDir().isEmpty() ? null : stageAnnotation.resourcesDir())
                .setChanges(changeUnitClasses)
                .build();
    }
    
    private Optional<SystemPreviewStage> getSystemStageFromAnnotation(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, SystemStage systemStageAnnotation) {
        String sourcesPackage = systemStageAnnotation.sourcesPackage();
        
        // If no sourcesPackage specified for systemStage, don't create it
        if (sourcesPackage == null || sourcesPackage.trim().isEmpty()) {
            return Optional.empty();
        }
        
        Collection<AbstractPreviewTask> changeUnitClasses = codedChangeUnitsByPackage.get(sourcesPackage);
        
        SystemPreviewStage stage = PreviewStage.systemBuilder()
                .setName("flamingock-system-stage")
                .setDescription("Dedicated stage for system-level changes")
                .setSourcesRoots(sourceRoots)
                .setSourcesPackage(sourcesPackage)
                .setResourcesRoot(resourcesRoot)
                .setChanges(changeUnitClasses)
                .build();
        return Optional.of(stage);
    }

    /**
     * Resolves a pipeline file path from the @Flamingock annotation, supporting both absolute file paths
     * and classpath resources. This method provides resource resolution for the Flamingock library.
     *
     * @param pipelineFilePath the file path specified in the @Flamingock annotation
     * @return a File object representing the resolved pipeline file
     * @throws RuntimeException if the file cannot be found in any of the supported locations
     */
    private File resolvePipelineFile(String pipelineFilePath) {
        List<File> searchedFiles = new ArrayList<>();
        
        // Try direct file path first (absolute or relative to current working directory)
        File result = tryResolveFile(new File(pipelineFilePath), "direct file path", searchedFiles);
        if (result != null) return result;
        
        // Try as classpath resource in main resources
        result = tryResolveFile(new File(resourcesRoot + "/" + pipelineFilePath), "main resources", searchedFiles);
        if (result != null) return result;
        
        // Try as classpath resource in test resources (for annotation processing during tests)
        String testResourcesRoot = resourcesRoot.replace("src/main/resources", "src/test/resources");
        result = tryResolveFile(new File(testResourcesRoot + "/" + pipelineFilePath), "test resources", searchedFiles);
        if (result != null) return result;
        
        // Try with "resources/" prefix stripped (handle cases like "resources/flamingock/pipeline.yaml")
        if (pipelineFilePath.startsWith("resources/")) {
            String pathWithoutResourcesPrefix = pipelineFilePath.substring("resources/".length());
            
            // Try in main resources without "resources/" prefix
            result = tryResolveFile(new File(resourcesRoot + "/" + pathWithoutResourcesPrefix), "main resources (stripped resources/ prefix)", searchedFiles);
            if (result != null) return result;
            
            // Try in test resources without "resources/" prefix
            result = tryResolveFile(new File(testResourcesRoot + "/" + pathWithoutResourcesPrefix), "test resources (stripped resources/ prefix)", searchedFiles);
            if (result != null) return result;
        }
        
        // If all resolution attempts failed, provide helpful error message
        StringBuilder searchedLocations = new StringBuilder("Searched locations:");
        for (int i = 0; i < searchedFiles.size(); i++) {
            searchedLocations.append(String.format("\n  %d. %s", i + 1, searchedFiles.get(i).getAbsolutePath()));
        }
        
        throw new RuntimeException(
            "Pipeline file specified in @Flamingock annotation does not exist: " + pipelineFilePath + "\n" +
            searchedLocations
        );
    }
    

    private File tryResolveFile(File file, String description, List<File> searchedFiles) {
        searchedFiles.add(file);
        if (file.exists()) {
            logger.info("Pipeline file resolved as " + description + ": " + file.getAbsolutePath());
            return file;
        }
        return null;
    }

    private PreviewPipeline buildPipelineFromSpecifiedFile(File file, Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage) {

        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);

            Map<String, Object> pipelineMap = (Map<String, Object>) config.get("pipeline");


            List<Map<String, String>> stageList = (List<Map<String, String>>) pipelineMap.get("stages");


            List<PreviewStage> stages = new ArrayList<>();

            for (Map<String, String> stageMap : stageList) {
                PreviewStage stage = mapToStage(codedChangeUnitsByPackage, stageMap);
                stages.add(stage);
            }

            Optional<SystemPreviewStage> systemStage = getSystemStage(codedChangeUnitsByPackage, (Map<String, String> )pipelineMap.get("systemStage"));

            return systemStage
                    .map(previewStage -> new PreviewPipeline(previewStage, stages))
                    .orElseGet(() -> new PreviewPipeline(stages));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private Optional<SystemPreviewStage> getSystemStage(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, Map<String, String> systemStage) {
        if(systemStage == null) {
            return Optional.empty();
        }
        String sourcesPackage = systemStage.get("sourcesPackage");
        Collection<AbstractPreviewTask> changeUnitClasses = codedChangeUnitsByPackage.get(sourcesPackage);
        SystemPreviewStage stage = PreviewStage.systemBuilder()
                .setName("flamingock-system-stage")
                .setDescription("Dedicated stage for system-level changes")
                .setSourcesRoots(sourceRoots)
                .setSourcesPackage(sourcesPackage)
                .setResourcesRoot(resourcesRoot)
                .setResourcesDir(systemStage.get("resourcesDir"))
                .setChanges(changeUnitClasses)
                .build();
        return Optional.of(stage);
    }

    private PreviewStage mapToStage(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, Map<String, String> stageMap) {

        String sourcesPackage = stageMap.get("sourcesPackage");
        Collection<AbstractPreviewTask> changeUnitClasses = codedChangeUnitsByPackage.get(sourcesPackage);
        return PreviewStage.defaultBuilder(StageType.from(stageMap.get("type")))
                .setName(stageMap.get("name"))
                .setDescription(stageMap.get("description"))
                .setSourcesRoots(sourceRoots)
                .setSourcesPackage(sourcesPackage)
                .setResourcesRoot(resourcesRoot)
                .setResourcesDir(stageMap.get("resourcesDir"))
                .setChanges(changeUnitClasses)
                .build();
    }

    @NotNull
    private List<String> getSourcesPathList() {
        if (processingEnv.getOptions().containsKey(SOURCES_PATH_ARG)) {
            String sourcesPath = processingEnv.getOptions().get(SOURCES_PATH_ARG);
            logger.info("'" + SOURCES_PATH_ARG + "' parameter passed: '" + sourcesPath + "'");
            return Collections.singletonList(sourcesPath);
        } else {
            logger.warn("'" + SOURCES_PATH_ARG + "' parameter NOT passed. Searching in: '" + DEFAULT_SOURCE_DIRS + "'");
            return DEFAULT_SOURCE_DIRS;
        }
    }

    @NotNull
    private String getResourcesRoot() {
        final String resourcesDir;
        if (processingEnv.getOptions().containsKey(RESOURCES_PATH_ARG)) {
            resourcesDir = processingEnv.getOptions().get(RESOURCES_PATH_ARG);
            logger.info("'" + RESOURCES_PATH_ARG + "' parameter passed: '" + resourcesDir + "'");
        } else {
            resourcesDir = DEFAULT_RESOURCES_PATH;
            logger.warn("'" + RESOURCES_PATH_ARG + "' parameter NOT passed. Using default '" + resourcesDir + "'");
        }
        return resourcesDir;
    }



}
