rootProject.name = "flamingock-project"



//////////////////////////////////////
// CORE
//////////////////////////////////////
include("flamingock-core")
project(":flamingock-core").projectDir = file("core/flamingock-core")
project(":flamingock-core").name = "flamingock-core"

include("flamingock-spring-core")
project(":flamingock-spring-core").projectDir = file("core/flamingock-spring-core")
project(":flamingock-spring-core").name = "flamingock-spring-core"

//////////////////////////////////////
// CORE
//////////////////////////////////////
include("flamingock-cloud")
project(":flamingock-cloud").projectDir = file("cloud/flamingock-cloud")

//////////////////////////////////////
// COMMUNITY
//////////////////////////////////////
include("flamingock-community")
project(":flamingock-community").projectDir = file("community/flamingock-community")
project(":flamingock-community").name = "flamingock-community"

include("mongodb-sync-v4-driver")
project(":mongodb-sync-v4-driver").projectDir = file("community/mongodb/mongodb-sync-v4-driver")

include("standalone-runner-community")
project(":standalone-runner-community").name = "standalone-runner-community"
project(":standalone-runner-community").projectDir = file("community/standalone-runner-community")



include("mongodb-facade")
project(":mongodb-facade").name = "mongodb-facade"
project(":mongodb-facade").projectDir = file("community/mongodb/mongodb-facade")
