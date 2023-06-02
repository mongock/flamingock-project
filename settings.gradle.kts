rootProject.name = "flamingock-project"
include("flamingock-core")
include("flamingock-oss")
include("flamingock-cloud")

include("mongodb-sync-v4-driver")
project(":mongodb-sync-v4-driver").projectDir = file("driver/mongodb/mongodb-sync-v4-driver")

include("mongodb-facade")
project(":mongodb-facade").projectDir = file("driver/mongodb/mongodb-facade")
