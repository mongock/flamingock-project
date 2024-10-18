package io.flamingock.oss.driver.mongodb.v3.internal.legacy;

import io.flamingock.core.api.LocalSystemModule;
import io.flamingock.core.runtime.dependency.Dependency;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MongoDBLocalLegacyImporter implements LocalSystemModule {
    public static final List<Class<?>> TASK_CLASSES = Collections.singletonList(
            MongockLocalLegacyImporterChangeUnit.class
    );
    
    @Override
    public void initialise() {

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
        return Collections.emptyList();
    }

    @Override
    public boolean isBeforeUserStages() {
        return true;
    }
}
