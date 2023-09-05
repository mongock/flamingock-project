rootProject.name = "flamingock-project"



//////////////////////////////////////
// CORE
//////////////////////////////////////
include("core:flamingock-core")
project(":core:flamingock-core").projectDir = file("core/flamingock-core")
project(":core:flamingock-core").name = "flamingock-core"

include("core:flamingock-spring-core")
project(":core:flamingock-spring-core").projectDir = file("core/flamingock-spring-core")
project(":core:flamingock-spring-core").name = "flamingock-spring-core"

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

include("community:springboot-runner")
project(":community:springboot-runner").name = "springboot-runner"
project(":community:springboot-runner").projectDir = file("community/springboot-runner")

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