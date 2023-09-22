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

include("core:flamingock-springboot-v2-core")
project(":core:flamingock-springboot-v2-core").projectDir = file("core/flamingock-springboot-v2-core")
project(":core:flamingock-springboot-v2-core").name = "flamingock-springboot-v2-core"

include("core:flamingock-springboot-v3-core")
project(":core:flamingock-springboot-v3-core").projectDir = file("core/flamingock-springboot-v3-core")
project(":core:flamingock-springboot-v3-core").name = "flamingock-springboot-v3-core"

//////////////////////////////////////
// COMMUNITY
//////////////////////////////////////
include("community:base-community")
project(":community:base-community").name = "base-community"
project(":community:base-community").projectDir = file("community/base-community")

include("community:standalone-runner")
project(":community:standalone-runner").name = "standalone-runner"
project(":community:standalone-runner").projectDir = file("community/standalone-runner")

include("community:springboot-v2-runner")
project(":community:springboot-v2-runner").name = "springboot-v2-runner"
project(":community:springboot-v2-runner").projectDir = file("community/springboot-v2-runner")

include("community:springboot-v3-runner")
project(":community:springboot-v3-runner").name = "springboot-v3-runner"
project(":community:springboot-v3-runner").projectDir = file("community/springboot-v3-runner")

include("community:mongodb:mongodb-facade")
project(":community:mongodb:mongodb-facade").name = "mongodb-facade"
project(":community:mongodb:mongodb-facade").projectDir = file("community/mongodb/mongodb-facade")

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

include("community:couchbase:couchbase-driver")
project(":community:couchbase:couchbase-driver").name = "couchbase-driver"
project(":community:couchbase:couchbase-driver").projectDir = file("community/couchbase/couchbase-driver")

include("community:couchbase:couchbase-springboot-v2-driver")
project(":community:couchbase:couchbase-springboot-v2-driver").name = "couchbase-springboot-v2-driver"
project(":community:couchbase:couchbase-springboot-v2-driver").projectDir = file("community/couchbase/couchbase-springboot-v2-driver")

//////////////////////////////////////
// EXAMPLES
//////////////////////////////////////

include("examples:community:standalone-mongodb-sync")
project(":examples:community:standalone-mongodb-sync").name = "standalone-mongodb-sync"
project(":examples:community:standalone-mongodb-sync").projectDir = file("examples/community/standalone-mongodb-sync")

include("examples:community:springboot-mongodb-sync")
project(":examples:community:springboot-mongodb-sync").name = "springboot-mongodb-sync"
project(":examples:community:springboot-mongodb-sync").projectDir = file("examples/community/springboot-mongodb-sync")

include("examples:community:springboot-mongodb-springdata")
project(":examples:community:springboot-mongodb-springdata").name = "springboot-mongodb-springdata"
project(":examples:community:springboot-mongodb-springdata").projectDir = file("examples/community/springboot-mongodb-springdata")

include("examples:community:springboot-v3-mongodb-springdata-v4")
project(":examples:community:springboot-v3-mongodb-springdata-v4").name = "springboot-v3-mongodb-springdata-v4"
project(":examples:community:springboot-v3-mongodb-springdata-v4").projectDir = file("examples/community/springboot-v3-mongodb-springdata-v4")

include("examples:community:mongodb-template")
project(":examples:community:mongodb-template").name = "mongodb-template"
project(":examples:community:mongodb-template").projectDir = file("examples/community/mongodb-template")

include("examples:community:couchbase:standalone-couchbase")
project(":examples:community:couchbase:standalone-couchbase").name = "standalone-couchbase"
project(":examples:community:couchbase:standalone-couchbase").projectDir = file("examples/community/couchbase/standalone-couchbase")

include("examples:community:couchbase:springboot-v2-couchbase")
project(":examples:community:couchbase:springboot-v2-couchbase").name = "springboot-v2-couchbase"
project(":examples:community:couchbase:springboot-v2-couchbase").projectDir = file("examples/community/couchbase/springboot-v2-couchbase")
