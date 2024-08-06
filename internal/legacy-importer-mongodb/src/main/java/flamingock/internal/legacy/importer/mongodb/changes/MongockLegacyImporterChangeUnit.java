package flamingock.internal.legacy.importer.mongodb.changes;


import flamingock.core.api.annotations.ChangeUnit;
import flamingock.core.api.annotations.Execution;

@ChangeUnit(id = "mongock-legacy-importer-mongodb", order = "1")
public class MongockLegacyImporterChangeUnit {

    @Execution
    public void execution() {

    }
}
