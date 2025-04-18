package io.flamingock.core.engine.audit.importer;

import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.engine.audit.importer.changeunit.FlamingockLocalImporterChangeUnit;
import io.flamingock.core.engine.audit.importer.changeunit.MongockImporterChangeUnit;
import io.flamingock.core.pipeline.PipelineDescriptor;
import io.flamingock.core.preview.PreviewStage;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.system.LocalSystemModule;
import io.flamingock.core.preview.CodePreviewChangeUnit;
import io.flamingock.core.preview.PreviewMethod;
import io.flamingock.core.preview.builder.PreviewTaskBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ImporterModule implements LocalSystemModule {

    public static final String FROM_MONGOCK_NAME = "from-mongock-importer";
    public static final String FROM_FLAMINGOCK_LITE_NAME = "from-flamingock-local-importer";



    private final List<CodePreviewChangeUnit> fromMongockChangeUnits = Collections.singletonList(
            PreviewTaskBuilder.getCodeBuilder()
                    .setId(MongockImporterChangeUnit.IMPORTER_FROM_MONGOCK)
                    .setOrder("1")
                    .setSourceClassPath(MongockImporterChangeUnit.class.getName())
                    .setExecutionMethod(new PreviewMethod("execution", Arrays.asList(
                            ImporterReader.class.getName(),
                            AuditWriter.class.getName(),
                            PipelineDescriptor.class.getName())))
                    .setRunAlways(false)
                    .setTransactional(true)
                    .setNewChangeUnit(true)
                    .setSystem(true)
                    .build()
    );


    private final List<CodePreviewChangeUnit> fromFlamingockChangeUnits = Collections.singletonList(
            PreviewTaskBuilder.getCodeBuilder()
                    .setId(FlamingockLocalImporterChangeUnit.IMPORTER_FROM_FLAMINGOCK_LOCAL)
                    .setOrder("2")
                    .setSourceClassPath(MongockImporterChangeUnit.class.getName())
                    .setExecutionMethod(new PreviewMethod("execution", Arrays.asList(
                            ImporterReader.class.getName(),
                            AuditWriter.class.getName(),
                            PipelineDescriptor.class.getName())))
                    .setRunAlways(false)
                    .setTransactional(true)
                    .setNewChangeUnit(true)
                    .setSystem(true)
                    .build()
    );
    private static final String FROM_MONGOCK_DESC = "Importer from Mongock";
    private static final String FROM_FLAMINGOCK_LITE_DESC = "Importer from Flamingock lite";

    private ImporterReader importReader;
    private boolean fromMongock;
    private List<Dependency> dependencies;

    public ImporterModule() {
    }

    public ImporterModule(ImporterReader importerReader) {
        this.fromMongock = importerReader.isFromMongock();
        this.importReader = importerReader;
    }

    @Override
    public void initialise() {
        dependencies = Collections.singletonList(
                new Dependency(ImporterReader.class, importReader)
        );
    }

    @Override
    public PreviewStage getStage() {
        return PreviewStage.builder()
                .setName(fromMongock ? FROM_MONGOCK_NAME : FROM_FLAMINGOCK_LITE_NAME)
                .setDescription(fromMongock ? FROM_MONGOCK_DESC : FROM_FLAMINGOCK_LITE_DESC)
                .setChanges(fromMongock ? fromMongockChangeUnits : fromFlamingockChangeUnits)
                .build();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean isBeforeUserStages() {
        return true;
    }
}
