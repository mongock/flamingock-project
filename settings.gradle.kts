rootProject.name = "flamingock-project"
include("flamingock-core")
include("flamingock-cloud")

include("flamingock-oss")
project(":flamingock-oss").projectDir = file("oss/flamingock-oss")

include("mongodb-sync-v4-driver")
project(":mongodb-sync-v4-driver").projectDir = file("oss/mongodb/mongodb-sync-v4-driver")

include("mongodb-facade")
project(":mongodb-facade").projectDir = file("oss/mongodb/mongodb-facade")
