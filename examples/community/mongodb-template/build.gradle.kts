plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}
dependencies {
    implementation(project(":core:flamingock-template"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}
