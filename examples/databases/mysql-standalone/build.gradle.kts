plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}


val mongodbVersion = "4.3.3"
dependencies {
    implementation(project(":flamingock-core"))
    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation(project(":templates:sql-template"))

    //TODO remove once cloud is available
    implementation(project(":local-drivers:mongodb:mongodb-sync-v4-driver"))
    implementation("org.mongodb:mongodb-driver-sync:$mongodbVersion")
    implementation("org.mongodb:mongodb-driver-core:$mongodbVersion")
    implementation("org.mongodb:bson:$mongodbVersion")
    implementation("org.slf4j:slf4j-simple:2.0.6")





}

