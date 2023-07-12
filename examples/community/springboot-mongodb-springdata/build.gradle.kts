plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}

val mongodbVersion = "4.3.3"

dependencies {

    implementation(project(":community:springboot-runner"))
    implementation(project(":community:mongodb:mongodb-springdata-v3-driver"))
    
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.data:spring-data-mongodb:3.2.0")
    implementation("org.mongodb:mongodb-driver-sync:$mongodbVersion")
    implementation("org.mongodb:mongodb-driver-core:$mongodbVersion")
    implementation("org.mongodb:bson:$mongodbVersion")

    testImplementation("org.testcontainers:mongodb:1.18.3")

    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")


}

