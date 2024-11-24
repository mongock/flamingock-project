import org.jreleaser.model.Active

plugins {
    `kotlin-dsl`
    `maven-publish`
    id("java-library")
    id("org.jreleaser") version "1.15.0"

}

allprojects {
    group = "io.flamingock"
    version = "0.0.3"

    apply(plugin = "org.jetbrains.kotlin.jvm")
}


subprojects {


    apply(plugin = "java-library")


    if(shouldBeReleased(project)) {




        apply(plugin = "maven-publish")
        apply(plugin = "org.jreleaser")
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    groupId = project.group.toString()
                    artifactId = project.name

                    from(components["java"])

                    pom {
                        name.set(project.name)
                        description.set("Description should be here")
                        url.set("https://github.com/mongock/flamingock-project")
                        inceptionYear.set("2024")

                        licenses {
                            license {
                                name.set("Apache-2.0")
                                url.set("https://spdx.org/licenses/Apache-2.0.html")
                            }
                        }
                        developers {
                            developer {
                                id.set("dieppa")
                                name.set("Antonio Perez Dieppa")
                            }
                        }
                        scm {
                            connection.set("scm:git:https://github.com:mongock/flamingock-project.git")
                            developerConnection.set("scm:git:ssh://github.com:mongock/flamingock-project.git")
                            url.set("https://github.com/mongock/flamingock-project")
                        }
                    }
                }
            }

            repositories {
                maven {
                    url = layout.buildDirectory.dir("staging-deploy").get().asFile.toURI()
                }
            }
        }

        jreleaser {
            signing {
                active.set(Active.ALWAYS)
                armored = true
            }

            gitRootSearch.set(true)

            deploy {
                maven {
                    mavenCentral {

                        create("sonatype") {
                            active.set(Active.ALWAYS)
                            url.set("https://central.sonatype.com/api/v1/publisher")
                            stagingRepository("build/staging-deploy")
                            sourceJar.set(true)
                            javadocJar.set(true)
                            verifyPom.set(false)
                        }


                    }
                }
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

fun shouldBeReleased(project: Project) : Boolean {
    return project.name == "utils"
}
