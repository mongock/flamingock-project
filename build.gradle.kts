plugins {
    `kotlin-dsl`
    id("maven-publish")
    id("signing")
    id("java")
}

allprojects {
    group = "io.flamingock"
    version = "1.0.0-beta"
}


subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    if (project.hasProperty("publish") && project.property("publish") == "true") {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
    }

    afterEvaluate {
        if (project.hasProperty("publish") && project.property("publish") == "true") {
            publishing {
                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])
                        artifactId = project.name
                        pom {
                            name.set(project.name)
                            description.set(project.description)
                            url.set("https://github.com/mongock/flamingock-project")
                        }
                    }
                }

                repositories {
                    maven {
                        name = "OSSRH"
                        url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        //./gradlew publish -Prelease
//                        if (project.hasProperty("release")) {
//                            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
//                        } else {
//                            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
//                        }
                        credentials {
                            username = System.getenv("MAVEN_USERNAME")
                            password = System.getenv("MAVEN_CENTRAL_TOKEN")
                        }
                    }
                }
            }

            signing {
                useInMemoryPgpKeys(
                    System.getenv("GPG_SIGNING_PRIVATE_KEY"),
                    System.getenv("GPG_PASSPHRASE")
                )
                sign(publishing.publications["mavenJava"])
            }
        }
    }

    repositories {
        mavenCentral()
        mavenLocal()
    }




    val implementation by configurations
    val testImplementation by configurations
    val testRuntimeOnly by configurations

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.slf4j", "slf4j-api", "2.0.6")

        testImplementation("org.slf4j:slf4j-simple:2.0.6")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
        testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.2")

        testImplementation("org.mockito:mockito-core:4.11.0")
        testImplementation("org.mockito:mockito-junit-jupiter:4.11.0")
        testImplementation("org.mockito:mockito-inline:4.11.0")

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


    tasks.getByName<Test>("test") {
        useJUnitPlatform()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(8))
        }
    }
}



