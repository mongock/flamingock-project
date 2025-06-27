package io.flamingock.core.processor;

import io.flamingock.api.annotations.ChangeUnit;
import io.flamingock.api.annotations.Pipeline;
import io.flamingock.api.annotations.Stage;
import io.flamingock.api.annotations.SystemStage;
import io.flamingock.internal.common.core.preview.PreviewPipeline;
import io.flamingock.internal.common.core.preview.PreviewStage;
import io.flamingock.core.processor.util.AnnotationFinder;
import io.flamingock.core.processor.util.LoggerPreProcessor;
import io.flamingock.core.processor.util.Serializer;
import io.flamingock.internal.common.core.preview.AbstractPreviewTask;
import io.flamingock.api.annotations.StageType;
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
 * Annotation processor for Flamingock that generates metadata files
 * containing information about templated and annotated changes.
 * <p>
 * The processor operates in two phases:
 * <ul>
 *     <li><b>Initialization (`init()` phase):</b> Processes templated changes
 *         (which are stored in files) and serializes them into
 *         <code>META-INF/flamingock/metadata-templated.json</code>.</li>
 *     <li><b>Processing (`process()` phase):</b> Processes all changes,
 *         including templated changes and those derived from annotated classes.
 *         The final structure is serialized into
 *         <code>META-INF/flamingock/metadata-full.json</code>.</li>
 * </ul>
 * <p>
 *
 * <h2>Runtime Behavior</h2>
 * At runtime, the Flamingock library follows this lookup order for metadata:
 * <ol>
 *     <li>If <code>META-INF/flamingock/metadata-full.json</code> exists,
 *         it is used exclusively.</li>
 *     <li>If <code>META-INF/flamingock/metadata-full.json</code> does not exist,
 *         the system falls back to <code>META-INF/flamingock/metadata-templated.json</code>.</li>
 *     <li><b>If neither file exists, the Flamingock library throws an exception.</b></li>
 * </ol>
 * <p>
 *
 * <h2>Usage</h2>
 * This annotation processor is automatically triggered during compilation.
 * It processes annotated classes and predefined metadata templates without requiring
 * additional configuration.
 *
 * <h2>Supported Annotations</h2>
 * The processor detects and processes classes annotated with:
 * <ul>
 *     <li>{@link  ChangeUnit} - Represents a change unit defined within the code.</li>
 *     <li>io.mongock.api.annotations.ChangeUnit - Represents a legacy change unit defined within the code.</li>
 * </ul>
 *
 * <h2>Generated Metadata Files</h2>
 * <ul>
 *     <li><b><code>templated-pipeline.json</code></b> - Contains only templated changes.</li>
 *     <li><b><code>full-pipeline.json</code></b> - Contains all processed changes
 *         (templated + annotated classes).</li>
 * </ul>
 *
 * <h2>Compilation Behavior</h2>
 * <ul>
 *     <li>The processor executes at compile-time.</li>
 *     <li>The templated metadata is always processed in the `init()` phase.</li>
 *     <li>If annotated elements exist, the processor enriches the metadata in `process()`.</li>
 * </ul>
 *
 * @author Antonio
 * @version 1.0
 * @since Flamingock v1.x
 */
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class PipelinePreProcessor extends AbstractProcessor {

    private static final String RESOURCES_PATH_ARG = "resources";
    private static final String SOURCES_PATH_ARG = "sources";

    private static final String DEFAULT_RESOURCES_PATH = "src/main/resources";
    private static final List<String> DEFAULT_SOURCE_DIRS = Arrays.asList(
            "src/main/java", "src/main/kotlin", "src/main/scala", "src/main/groovy"
    );

    private static final String FLAMINGOCK_PIPELINE_FILE = "pipeline.yaml";
    private static final String FLAMINGOCK_RESOURCE_DIR = "flamingock";

    private static final boolean hasProcessed = false;

    private File pipelineFile;
    private String resourcesRoot = null;
    private String flamingockDir = null;
    private List<String> sourceRoots = null;
    private LoggerPreProcessor logger;
    private Serializer serializer;
    private AnnotationFinder annotationFinder;

    public PipelinePreProcessor(){}

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        logger = new LoggerPreProcessor(processingEnv);
        logger.info("Starting Flamingock annotation processor initialization.");
        resourcesRoot = getResourcesRoot();
        flamingockDir = getFlamingockDir(resourcesRoot);
        sourceRoots = getSourcesPathList();
        serializer = new Serializer(processingEnv, logger);
        pipelineFile = getFlamingockPipelineFile();
        
        if (pipelineFile.exists()) {
            PreviewPipeline pipeline = getPipelineFromTemplatedChanges();
            serializer.serializeTemplatedPipeline(pipeline);
            logger.info("Initialization completed. Processed templated-based changes.");
        } else {
            // Create empty templated pipeline - will be populated in process phase with annotation config
            serializer.serializeTemplatedPipeline(new PreviewPipeline(Collections.emptyList()));
            logger.info("No pipeline.yaml file found. Templated pipeline will be processed with @Pipeline annotation.");
        }
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<>(Arrays.asList("sources", "resources"));
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                ChangeUnit.class.getName(),
                Pipeline.class.getName()
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

        AnnotationFinder finder = new AnnotationFinder(roundEnv, logger);
        PreviewPipeline pipeline = getPipelineFromProcessChanges(
                finder.getCodedChangeUnitsMapByPackage(),
                finder.getPipelineAnnotation()
        );
        serializer.serializeFullPipeline(pipeline);
        logger.info("Finished processing annotated classes and generating metadata.");
        return true;
    }

    private PreviewPipeline getPipelineFromTemplatedChanges() {
        return getPipelineFromProcessChanges(null, null);
    }

    @SuppressWarnings("unchecked")
    private PreviewPipeline getPipelineFromProcessChanges(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage, Pipeline pipelineAnnotation) {
        if (codedChangeUnitsByPackage == null) {
            codedChangeUnitsByPackage = new HashMap<>();
        }
        
        boolean hasFile = pipelineFile != null && pipelineFile.exists();
        boolean hasAnnotation = pipelineAnnotation != null;
        
        if (!hasFile && !hasAnnotation) {
            throw new RuntimeException("No pipeline configuration found. Provide either pipeline.yaml file or @Pipeline annotation.");
        }
        
        if (hasFile && hasAnnotation) {
            logger.warn("Both pipeline.yaml file and @Pipeline annotation found. Using @Pipeline annotation.");
        }
        
        if (hasAnnotation) {
            logger.info("Reading flamingock pipeline from @Pipeline annotation");
            return buildPipelineFromAnnotation(pipelineAnnotation, codedChangeUnitsByPackage);
        } else {
            logger.info("Reading flamingock pipeline from file: '" + pipelineFile.getPath() + "'");
            return buildPipelineFromFile(codedChangeUnitsByPackage);
        }
    }
    
    private PreviewPipeline buildPipelineFromAnnotation(Pipeline pipelineAnnotation, Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage) {
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

    private PreviewPipeline buildPipelineFromFile(Map<String, List<AbstractPreviewTask>> codedChangeUnitsByPackage) {

        try (InputStream inputStream = Files.newInputStream(pipelineFile.toPath())) {
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

    @NotNull
    private String getFlamingockDir(String resourcesDir) {

        if (resourcesDir.endsWith("/")) {
            return resourcesDir + FLAMINGOCK_RESOURCE_DIR;
        } else {
            return resourcesDir + "/" + FLAMINGOCK_RESOURCE_DIR;
        }
    }

    @NotNull
    private File getFlamingockPipelineFile() {
        return new File(flamingockDir, FLAMINGOCK_PIPELINE_FILE);
    }

}
