rootProject.name = "flamingock-project"



//////////////////////////////////////
// CORE
//////////////////////////////////////
include("flamingock-core")
include("flamingock-processor")
include("flamingock-springboot-v2-runner")
include("flamingock-springboot-v3-runner")
include("flamingock-graalvm")
include("flamingock-core-api")


//////////////////////////////////////
// CLOUD
//////////////////////////////////////
include("flamingock-cloud-edition")

include("cloud-transactioners:sql-cloud-transactioner")
project(":cloud-transactioners:sql-cloud-transactioner").projectDir = file("cloud-transactioners/sql-cloud-transactioner")
project(":cloud-transactioners:sql-cloud-transactioner").name = "sql-cloud-transactioner"

include("cloud-transactioners:mongodb-sync-v4-cloud-transactioner")
project(":cloud-transactioners:mongodb-sync-v4-cloud-transactioner").projectDir = file("cloud-transactioners/mongodb-sync-v4-cloud-transactioner")
project(":cloud-transactioners:mongodb-sync-v4-cloud-transactioner").name = "mongodb-sync-v4-cloud-transactioner"

include("cloud-transactioners:dynamodb-cloud-transactioner")
project(":cloud-transactioners:dynamodb-cloud-transactioner").projectDir = file("cloud-transactioners/dynamodb-cloud-transactioner")
project(":cloud-transactioners:dynamodb-cloud-transactioner").name = "dynamodb-cloud-transactioner"

//////////////////////////////////////
// COMMUNITY
//////////////////////////////////////

include("community:flamingock-ce-commons")
project(":community:flamingock-ce-commons").name = "flamingock-ce-commons"
project(":community:flamingock-ce-commons").projectDir = file("community/flamingock-ce-commons")

include("community:flamingock-ce-bom")
project(":community:flamingock-ce-bom").name = "flamingock-ce-bom"
project(":community:flamingock-ce-bom").projectDir = file("community/flamingock-ce-bom")

include("community:mongodb-sync-v4-driver")
project(":community:mongodb-sync-v4-driver").name = "mongodb-sync-v4-driver"
project(":community:mongodb-sync-v4-driver").projectDir = file("community/mongodb-sync-v4-driver")

include("community:mongodb-v3-driver")
project(":community:mongodb-v3-driver").name = "mongodb-v3-driver"
project(":community:mongodb-v3-driver").projectDir = file("community/mongodb-v3-driver")

include("community:mongodb-springdata-v3-driver")
project(":community:mongodb-springdata-v3-driver").name = "mongodb-springdata-v3-driver"
project(":community:mongodb-springdata-v3-driver").projectDir = file("community/mongodb-springdata-v3-driver")

include("community:mongodb-springdata-v2-driver")
project(":community:mongodb-springdata-v2-driver").name = "mongodb-springdata-v2-driver"
project(":community:mongodb-springdata-v2-driver").projectDir = file("community/mongodb-springdata-v2-driver")

include("community:mongodb-springdata-v4-driver")
project(":community:mongodb-springdata-v4-driver").name = "mongodb-springdata-v4-driver"
project(":community:mongodb-springdata-v4-driver").projectDir = file("community/mongodb-springdata-v4-driver")

include("community:flamingock-ce-couchbase")
project(":community:flamingock-ce-couchbase").name = "flamingock-ce-couchbase"
project(":community:flamingock-ce-couchbase").projectDir = file("community/flamingock-ce-couchbase")

include("community:flamingock-ce-dynamodb")
project(":community:flamingock-ce-dynamodb").name = "flamingock-ce-dynamodb"
project(":community:flamingock-ce-dynamodb").projectDir = file("community/flamingock-ce-dynamodb")

//////////////////////////////////////
// TEMPLATES
//////////////////////////////////////

//SQL
include("templates:sql-template")
project(":templates:sql-template").name = "sql-template"
project(":templates:sql-template").projectDir = file("templates/sql-template")


//MONGODB
include("templates:mongodb-change-template")
project(":templates:mongodb-change-template").name = "mongodb-change-template"
project(":templates:mongodb-change-template").projectDir = file("templates/mongodb-change-template")


//////////////////////////////////////
// INTERNAL
//////////////////////////////////////
include("importers:mongodb-importer-sync-v4")
project(":importers:mongodb-importer-sync-v4").name = "mongodb-importer-sync-v4"
project(":importers:mongodb-importer-sync-v4").projectDir = file("importers/mongodb-importer-sync-v4")


include("importers:mongodb-importer-v3")
project(":importers:mongodb-importer-v3").name = "mongodb-importer-v3"
project(":importers:mongodb-importer-v3").projectDir = file("importers/mongodb-importer-v3")

//////////////////////////////////////
// UTILS
//////////////////////////////////////
include("utils-test")
include("utils")

include("commons:mongodb-facade")
project(":commons:mongodb-facade").name = "mongodb-facade"
project(":commons:mongodb-facade").projectDir = file("commons/mongodb-facade")


include("commons:dynamodb-utils")
project(":commons:dynamodb-utils").name = "dynamodb-utils"
project(":commons:dynamodb-utils").projectDir = file("commons/dynamodb-utils")