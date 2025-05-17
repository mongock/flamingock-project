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

include("flamingock-ce-bom")
include("flamingock-ce-commons")

include("commons:mongodb-facade")
project(":commons:mongodb-facade").name = "mongodb-facade"
project(":commons:mongodb-facade").projectDir = file("commons/mongodb-facade")

include("community:mongodb:mongodb-sync-v4-driver")
project(":community:mongodb:mongodb-sync-v4-driver").name = "mongodb-sync-v4-driver"
project(":community:mongodb:mongodb-sync-v4-driver").projectDir = file("community/mongodb/mongodb-sync-v4-driver")

include("community:mongodb:mongodb-v3-driver")
project(":community:mongodb:mongodb-v3-driver").name = "mongodb-v3-driver"
project(":community:mongodb:mongodb-v3-driver").projectDir = file("community/mongodb/mongodb-v3-driver")

include("community:mongodb:mongodb-springdata-v3-driver")
project(":community:mongodb:mongodb-springdata-v3-driver").name = "mongodb-springdata-v3-driver"
project(":community:mongodb:mongodb-springdata-v3-driver").projectDir = file("community/mongodb/mongodb-springdata-v3-driver")

include("community:mongodb:mongodb-springdata-v2-driver")
project(":community:mongodb:mongodb-springdata-v2-driver").name = "mongodb-springdata-v2-driver"
project(":community:mongodb:mongodb-springdata-v2-driver").projectDir = file("community/mongodb/mongodb-springdata-v2-driver")

include("community:mongodb:mongodb-springdata-v4-driver")
project(":community:mongodb:mongodb-springdata-v4-driver").name = "mongodb-springdata-v4-driver"
project(":community:mongodb:mongodb-springdata-v4-driver").projectDir = file("community/mongodb/mongodb-springdata-v4-driver")

include("community:couchbase-driver")
project(":community:couchbase-driver").name = "couchbase-driver"
project(":community:couchbase-driver").projectDir = file("community/couchbase-driver")

include("commons:dynamodb-utils")
project(":commons:dynamodb-utils").name = "dynamodb-utils"
project(":commons:dynamodb-utils").projectDir = file("commons/dynamodb-utils")

include("community:dynamodb-driver")
project(":community:dynamodb-driver").name = "dynamodb-driver"
project(":community:dynamodb-driver").projectDir = file("community/dynamodb-driver")

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
// UTILS
//////////////////////////////////////
include("utils-test")
include("utils")



//////////////////////////////////////
// INTERNAL
//////////////////////////////////////
include("importers:mongodb-importer-sync-v4")
project(":importers:mongodb-importer-sync-v4").name = "mongodb-importer-sync-v4"
project(":importers:mongodb-importer-sync-v4").projectDir = file("importers/mongodb-importer-sync-v4")


include("importers:mongodb-importer-v3")
project(":importers:mongodb-importer-v3").name = "mongodb-importer-v3"
project(":importers:mongodb-importer-v3").projectDir = file("importers/mongodb-importer-v3")

