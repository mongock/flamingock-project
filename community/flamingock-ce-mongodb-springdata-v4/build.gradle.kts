dependencies {
    implementation(project(":utils:mongodb-util"))
    implementation(project(":importers:flamingock-mongodb-importer-sync-v4"))

    implementation(project(":core:flamingock-core"))
    api(project(":community:flamingock-ce-mongodb-sync-v4"))

    implementation("org.springframework.data:spring-data-mongodb:4.1.3")
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.1.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")

    testImplementation(project(":core:flamingock-core"))
    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

//    Mongock
    testImplementation("io.mongock:mongock-standalone:5.5.0")
    testImplementation("io.mongock:mongodb-sync-v4-driver:5.5.0")


}

description = "${project.name}'s description"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}