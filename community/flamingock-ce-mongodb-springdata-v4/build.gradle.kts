import org.gradle.kotlin.dsl.invoke

dependencies {
    implementation(project(":utils:mongodb-util"))
    implementation(project(":importers:mongodb-importer-sync-v4"))
    implementation(project(":core:flamingock-core"))
    api(project(":community:flamingock-ce-mongodb-sync"))

    compileOnly("org.mongodb:mongodb-driver-sync:[4.8.0, 5.5.0]")//this filters the broader range in flamingock-ce-mongodb-sync
    compileOnly("org.springframework.data:spring-data-mongodb:[4.0.0, 5.0.0)")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:[3.0.0, 4.0.0)")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:[3.0.0,4.0.0)")

    /*********************************************************
     * TEST
     *********************************************************/
    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

    testImplementation("io.mongock:mongock-standalone:5.5.0")
    testImplementation("io.mongock:mongodb-sync-v4-driver:5.5.0")


}

description = "MongoDB Spring Data v4.x integration module for Flamingock Community Edition, providing seamless integration with Spring Data MongoDB 4.x applications. Compatible with MongoDB Driver Sync 4.8.0+ and Spring Boot 3.x. Requires JDK 17 or above."

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configurations.testImplementation {
    extendsFrom(configurations.compileOnly.get())
}