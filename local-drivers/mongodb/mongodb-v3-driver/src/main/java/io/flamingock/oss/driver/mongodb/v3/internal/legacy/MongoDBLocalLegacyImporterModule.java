package io.flamingock.oss.driver.mongodb.v3.internal.legacy;

import com.mongodb.client.MongoDatabase;
import io.flamingock.core.api.LocalSystemModule;
import io.flamingock.core.engine.audit.AuditWriter;
import io.flamingock.core.runtime.dependency.Dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MongoDBLocalLegacyImporterModule implements LocalSystemModule {
    public static final List<Class<?>> TASK_CLASSES = Collections.singletonList(
            MongockLocalLegacyImporterChangeUnit.class
    );
    private List<Dependency> dependencies;
    private final MongoDatabase mongoDatabase;
    private final AuditWriter auditWriter;

    public MongoDBLocalLegacyImporterModule(MongoDatabase mongoDatabase, AuditWriter auditWriter) {
        this.mongoDatabase = mongoDatabase;
        this.auditWriter = auditWriter;
    }

    @Override
    public void initialise() {
        MongockLegacyImporterConfiguration configuration = new MongockLegacyImporterConfiguration(
                mongoDatabase, auditWriter
        );
        dependencies = Collections.singletonList(
                new Dependency(MongockLegacyImporterConfiguration.class, configuration)
        );
    }

    @Override
    public String getName() {
        return "mongodb-local-legacy-importer";
    }

    @Override
    public Collection<Class<?>> getTaskClasses() {
        return TASK_CLASSES;
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
