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

//    REFLECTION
    implementation("org.reflections:reflections:0.10.1")
    implementation("org.objenesis:objenesis:3.2")

}
