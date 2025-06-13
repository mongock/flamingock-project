import org.gradle.kotlin.dsl.invoke
import org.jetbrains.kotlin.gradle.utils.extendsFrom

dependencies {
    implementation(project(":utils:mongodb-util"))
    implementation(project(":importers:mongodb-importer-sync-v4"))

    implementation(project(":core:flamingock-core"))
    api(project(":community:flamingock-ce-mongodb-sync"))

    /**
     * MongoDB driver version constraint [4.8.0, 5.5.0].
     *
     * This specific version range is required to maintain compatibility between:
     * 1. Internal Flamingock dependencies (mongodb-importer-sync-v4 and flamingock-ce-mongodb-sync) which support a wider range [3.7.0, 6.0.0)
     * 2. Spring Data MongoDB [4.0.0, 5.0.0) which requires MongoDB driver versions in this narrower range
     *
     * This ensures the library works correctly with both Spring Data MongoDB and internal Flamingock components.
     * See compatibility matrix: ./mongodb_springdata_versions.md
     */
    compileOnly("org.mongodb:mongodb-driver-sync:[4.8.0, 5.5.0]")

    compileOnly("org.springframework.data:spring-data-mongodb:[4.0.0, 5.0.0)")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:[3.0.0, 4.0.0)")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:[3.0.0,4.0.0)")

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

configurations {
    testImplementation.extendsFrom(compileOnly)
}