rootProject.name = "flamingock-project"



//////////////////////////////////////
// CORE
//////////////////////////////////////
include("flamingock-template")
project(":flamingock-template").projectDir = file("flamingock-template")
project(":flamingock-template").name = "flamingock-template"


include("flamingock-core")
project(":flamingock-core").projectDir = file("flamingock-core")
project(":flamingock-core").name = "flamingock-core"

include("flamingock-springboot-v2-runner")
project(":flamingock-springboot-v2-runner").projectDir = file("flamingock-springboot-v2-runner")
project(":flamingock-springboot-v2-runner").name = "flamingock-springboot-v2-runner"

include("flamingock-springboot-v3-runner")
project(":flamingock-springboot-v3-runner").projectDir = file("flamingock-springboot-v3-runner")
project(":flamingock-springboot-v3-runner").name = "flamingock-springboot-v3-runner"

//////////////////////////////////////
// COMMUNITY
//////////////////////////////////////
include("local-drivers:driver-common")
project(":local-drivers:driver-common").name = "driver-common"
project(":local-drivers:driver-common").projectDir = file("local-drivers/driver-common")


include("local-drivers:mongodb:mongodb-facade")
project(":local-drivers:mongodb:mongodb-facade").name = "mongodb-facade"
project(":local-drivers:mongodb:mongodb-facade").projectDir = file("local-drivers/mongodb/mongodb-facade")

include("local-drivers:mongodb:mongodb-sync-v4-driver")
project(":local-drivers:mongodb:mongodb-sync-v4-driver").name = "mongodb-sync-v4-driver"
project(":local-drivers:mongodb:mongodb-sync-v4-driver").projectDir = file("local-drivers/mongodb/mongodb-sync-v4-driver")

include("local-drivers:mongodb:mongodb-v3-driver")
project(":local-drivers:mongodb:mongodb-v3-driver").name = "mongodb-v3-driver"
project(":local-drivers:mongodb:mongodb-v3-driver").projectDir = file("local-drivers/mongodb/mongodb-v3-driver")

include("local-drivers:mongodb:mongodb-springdata-v3-driver")
project(":local-drivers:mongodb:mongodb-springdata-v3-driver").name = "mongodb-springdata-v3-driver"
project(":local-drivers:mongodb:mongodb-springdata-v3-driver").projectDir = file("local-drivers/mongodb/mongodb-springdata-v3-driver")

include("local-drivers:mongodb:mongodb-springdata-v2-driver")
project(":local-drivers:mongodb:mongodb-springdata-v2-driver").name = "mongodb-springdata-v2-driver"
project(":local-drivers:mongodb:mongodb-springdata-v2-driver").projectDir = file("local-drivers/mongodb/mongodb-springdata-v2-driver")

include("local-drivers:mongodb:mongodb-springdata-v4-driver")
project(":local-drivers:mongodb:mongodb-springdata-v4-driver").name = "mongodb-springdata-v4-driver"
project(":local-drivers:mongodb:mongodb-springdata-v4-driver").projectDir = file("local-drivers/mongodb/mongodb-springdata-v4-driver")

include("local-drivers:couchbase:couchbase-driver")
project(":local-drivers:couchbase:couchbase-driver").name = "couchbase-driver"
project(":local-drivers:couchbase:couchbase-driver").projectDir = file("local-drivers/couchbase/couchbase-driver")

include("local-drivers:couchbase:couchbase-springboot-v2-driver")
project(":local-drivers:couchbase:couchbase-springboot-v2-driver").name = "couchbase-springboot-v2-driver"
project(":local-drivers:couchbase:couchbase-springboot-v2-driver").projectDir = file("local-drivers/couchbase/couchbase-springboot-v2-driver")

//////////////////////////////////////
// EXAMPLES
//////////////////////////////////////

include("examples:databases:standalone-mongodb-sync")
project(":examples:databases:standalone-mongodb-sync").name = "standalone-mongodb-sync"
project(":examples:databases:standalone-mongodb-sync").projectDir = file("examples/databases/standalone-mongodb-sync")

include("examples:databases:mongodb-springboot-sync")
project(":examples:databases:mongodb-springboot-sync").name = "mongodb-springboot-sync"
project(":examples:databases:mongodb-springboot-sync").projectDir = file("examples/databases/mongodb-springboot-sync")

include("examples:databases:mongodb-springboot-springdata")
project(":examples:databases:mongodb-springboot-springdata").name = "mongodb-springboot-springdata"
project(":examples:databases:mongodb-springboot-springdata").projectDir = file("examples/databases/mongodb-springboot-springdata")

include("examples:databases:springboot-v3-mongodb-springdata-v4")
project(":examples:databases:springboot-v3-mongodb-springdata-v4").name = "springboot-v3-mongodb-springdata-v4"
project(":examples:databases:springboot-v3-mongodb-springdata-v4").projectDir = file("examples/databases/springboot-v3-mongodb-springdata-v4")

include("examples:databases:mysql-template")
project(":examples:databases:mysql-template").name = "mysql-template"
project(":examples:databases:mysql-template").projectDir = file("examples/databases/mysql-template")

include("examples:databases:standalone-couchbase")
project(":examples:databases:standalone-couchbase").name = "standalone-couchbase"
project(":examples:databases:standalone-couchbase").projectDir = file("examples/databases/standalone-couchbase")

include("examples:databases:springboot-v2-couchbase")
project(":examples:databases:springboot-v2-couchbase").name = "springboot-v2-couchbase"
project(":examples:databases:springboot-v2-couchbase").projectDir = file("examples/databases/springboot-v2-couchbase")
