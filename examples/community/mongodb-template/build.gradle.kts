plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}
dependencies {
    implementation(project(":community:springboot-v2-runner"))
    implementation(project(":core:flamingock-template"))
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")

    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}
