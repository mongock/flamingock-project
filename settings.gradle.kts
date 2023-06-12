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
// COMMUNITY
//////////////////////////////////////
include("base-community")
project(":base-community").projectDir = file("community/base-community")
project(":base-community").name = "base-community"

include("mongodb-sync-v4-driver")
project(":mongodb-sync-v4-driver").projectDir = file("community/mongodb/mongodb-sync-v4-driver")

include("standalone-runner")
project(":standalone-runner").name = "standalone-runner"
project(":standalone-runner").projectDir = file("community/standalone-runner")



include("mongodb-facade")
project(":mongodb-facade").name = "mongodb-facade"
project(":mongodb-facade").projectDir = file("community/mongodb/mongodb-facade")
