plugins {
    id("java")
}

group = "io.mongock"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
//    INTERNAL

//    GENERAL
    implementation("org.slf4j:slf4j-api:2.0.6")
    implementation("javax.inject:javax.inject:1")

//    TEST
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("org.mockito:mockito-core:5.2.0")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    testLogging {
        events(
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.STANDARD_OUT,
        )
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}