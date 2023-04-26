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
    implementation(project(":core"))
    implementation(project(":mongock"))

//    GENERAL
    implementation("org.slf4j", "slf4j-api", "2.0.6")

    implementation("org.mongodb:mongo-java-driver:3.12.8")

//    TEST
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "1.8"
    targetCompatibility = "1.8"
}