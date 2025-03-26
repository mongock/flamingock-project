package io.flamingock.oss.driver.mongodb.v3.internal.mongock;

import com.mongodb.client.MongoCollection;
import io.flamingock.core.engine.audit.importer.changeunit.MongockImporterChangeUnit;
import io.flamingock.core.pipeline.PreviewStage;
import io.flamingock.core.system.LocalSystemModule;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.runtime.dependency.Dependency;
import io.flamingock.core.task.preview.CodePreviewChangeUnit;
import io.flamingock.core.task.preview.MethodPreview;
import io.flamingock.core.task.preview.builder.PreviewTaskBuilder;
import org.bson.Document;

import java.util.Collections;
import java.util.List;

public class MongockImporterModule implements LocalSystemModule {

    private static final List<CodePreviewChangeUnit> MONGOCK_CHANGE_UNITS = Collections.singletonList(
            PreviewTaskBuilder.getCodeBuilder()
                    .setId("mongock-importer")
                    .setOrder("1")
                    .setSourceClassPath(MongockImporterChangeUnit .class.getName())
                    .setExecutionMethod(new MethodPreview("execution", Collections.singletonList(
                            InternalMongockImporterConfiguration.class.getName())))
                    .setRunAlways(false)
                    .setTransactional(true)
                    .setNewChangeUnit(true)
                    .setSystem(true)
                    .build()
    );

    private List<Dependency> dependencies;
    private final MongoCollection<Document> sourceCollection;
    private final AuditWriter auditWriter;

    public MongockImporterModule(MongoCollection<Document> sourceCollection, AuditWriter auditWriter) {
        this.sourceCollection = sourceCollection;
        this.auditWriter = auditWriter;
    }

    @Override
    public void initialise() {
        InternalMongockImporterConfiguration configuration = new InternalMongockImporterConfiguration(
                sourceCollection, auditWriter
        );
        dependencies = Collections.singletonList(
                new Dependency(InternalMongockImporterConfiguration.class, configuration)
        );
    }

    @Override
    public PreviewStage getStage() {
        return PreviewStage.builder()
                .setName("mongodb-local-legacy-importer")
                .setDescription("MongoDB importer from Mongock")
                .setChangeUnitClasses(MONGOCK_CHANGE_UNITS)
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
