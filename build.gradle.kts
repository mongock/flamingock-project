import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {

    `kotlin-dsl`
    id("java")
//    kotlin("jvm") version "1.6.20"
}



subprojects {
    apply {
        plugin("org.jetbrains.kotlin.jvm")
    }


    group = "io.flamingock"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    val implementation by configurations
    val testImplementation by configurations
    val testRuntimeOnly by configurations

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    }

//    tasks.withType<KotlinCompile> {
//        kotlinOptions.jvmTarget = "1.8"
//    }


    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"
    }
}