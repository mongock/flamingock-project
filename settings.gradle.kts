rootProject.name = "flamingock-project"



//////////////////////////////////////
// CORE
//////////////////////////////////////
include("core:flamingock-template")
project(":core:flamingock-template").projectDir = file("core/flamingock-template")
project(":core:flamingock-template").name = "flamingock-template"


include("core:flamingock-core")
project(":core:flamingock-core").projectDir = file("core/flamingock-core")
project(":core:flamingock-core").name = "flamingock-core"

include("core:flamingock-springboot-v2-runner")
project(":core:flamingock-springboot-v2-runner").projectDir = file("core/flamingock-springboot-v2-runner")
project(":core:flamingock-springboot-v2-runner").name = "flamingock-springboot-v2-runner"

include("core:flamingock-springboot-v3-runner")
project(":core:flamingock-springboot-v3-runner").projectDir = file("core/flamingock-springboot-v3-runner")
project(":core:flamingock-springboot-v3-runner").name = "flamingock-springboot-v3-runner"

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

include("examples:community:mongodb:standalone-mongodb-sync")
project(":examples:community:mongodb:standalone-mongodb-sync").name = "standalone-mongodb-sync"
project(":examples:community:mongodb:standalone-mongodb-sync").projectDir = file("examples/community/mongodb/standalone-mongodb-sync")

include("examples:community:mongodb:springboot-mongodb-sync")
project(":examples:community:mongodb:springboot-mongodb-sync").name = "springboot-mongodb-sync"
project(":examples:community:mongodb:springboot-mongodb-sync").projectDir = file("examples/community/mongodb/springboot-mongodb-sync")

include("examples:community:mongodb:springboot-mongodb-springdata")
project(":examples:community:mongodb:springboot-mongodb-springdata").name = "springboot-mongodb-springdata"
project(":examples:community:mongodb:springboot-mongodb-springdata").projectDir = file("examples/community/mongodb/springboot-mongodb-springdata")

include("examples:community:mongodb:springboot-v3-mongodb-springdata-v4")
project(":examples:community:mongodb:springboot-v3-mongodb-springdata-v4").name = "springboot-v3-mongodb-springdata-v4"
project(":examples:community:mongodb:springboot-v3-mongodb-springdata-v4").projectDir = file("examples/community/mongodb/springboot-v3-mongodb-springdata-v4")

include("examples:community:mongodb:mongodb-template")
project(":examples:community:mongodb:mongodb-template").name = "mongodb-template"
project(":examples:community:mongodb:mongodb-template").projectDir = file("examples/community/mongodb/mongodb-template")

include("examples:community:couchbase:standalone-couchbase")
project(":examples:community:couchbase:standalone-couchbase").name = "standalone-couchbase"
project(":examples:community:couchbase:standalone-couchbase").projectDir = file("examples/community/couchbase/standalone-couchbase")

include("examples:community:couchbase:springboot-v2-couchbase")
project(":examples:community:couchbase:springboot-v2-couchbase").name = "springboot-v2-couchbase"
project(":examples:community:couchbase:springboot-v2-couchbase").projectDir = file("examples/community/couchbase/springboot-v2-couchbase")
