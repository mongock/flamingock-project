package io.flamingock.oss.driver.mongodb.v3.internal.legacy;


import io.flamingock.core.api.annotations.ChangeUnit;
import io.flamingock.core.api.annotations.Execution;
import io.flamingock.core.api.annotations.SystemChange;

@SystemChange
@ChangeUnit(id = "mongock-local-legacy-importer-mongodb-3", order = "1")
public class MongockLocalLegacyImporterChangeUnit {

    @Execution
    public void execution(MongockLegacyImporterConfiguration configuration) {
        System.out.println("THIS SHOULD THE LOCAL MIGRATION with configuration " + configuration.toString());
    }
}
