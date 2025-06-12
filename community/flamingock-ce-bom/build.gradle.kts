plugins {
    `java-platform`
    `maven-publish`
}

repositories {
    mavenLocal()
    maven {
        name = "GithubPackages"
        url = uri("https://maven.pkg.github.com/mongock/flamingock-project")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITHUB_USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITHUB_TOKEN")
        }
    }
    mavenCentral()
}

group = "io.flamingock"
version = "0.0.34-beta"

dependencies {
    constraints {
        // Core modules
        api("io.flamingock:flamingock-core:${project.version}")
        api("io.flamingock:flamingock-core-api:${project.version}")
        api("io.flamingock:flamingock-commons:${project.version}")

        // Database modules
        api("io.flamingock:flamingock-ce-mongodb-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-sync-v4:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v2:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v4:${project.version}")
        api("io.flamingock:flamingock-ce-couchbase:${project.version}")
        api("io.flamingock:flamingock-ce-dynamodb:${project.version}")
        api("io.flamingock:flamingock-ce-commons:${project.version}")

        // Templates
        api("io.flamingock:flamingock-sql-template:${project.version}")
        api("io.flamingock:flamingock-mongodb-change-template:${project.version}")
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["javaPlatform"])

            pom {
                name.set("Flamingock Community Edition BOM")
                description.set("Bill of Materials for Flamingock Community Edition modules")
                url.set("https://oss.flamingock.io")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                developers {
                    developer {
                        id.set("flamingock")
                        name.set("Flamingock Team")
                        email.set("team@flamingock.io")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/mongock/flamingock-project.git")
                    developerConnection.set("scm:git:ssh://github.com:mongock/flamingock-project.git")
                    url.set("https://github.com/mongock/flamingock-project")
                }
            }
        }
    }
}

// List of expected modules for verification
val expectedModules = listOf(
    // Core modules
    "flamingock-core",
    "flamingock-core-api",
    "flamingock-commons",

    // Database modules
    "flamingock-ce-mongodb-v3",
    "flamingock-ce-mongodb-sync-v4",
    "flamingock-ce-mongodb-springdata-v2",
    "flamingock-ce-mongodb-springdata-v3",
    "flamingock-ce-mongodb-springdata-v4",
    "flamingock-ce-couchbase",
    "flamingock-ce-dynamodb",
    "flamingock-ce-commons",

    // Templates
    "flamingock-sql-template",
    "flamingock-mongodb-change-template"
)

tasks.register("verifyBomContents") {
    group = "verification"
    description = "Verifies that all required modules are included in the BOM"

    // For java-platform projects, the task name is different
    dependsOn("generatePomFileForMavenPublication")

    doLast {
        val pomFile = layout.buildDirectory.file("publications/maven/pom-default.xml").get().asFile

        if (!pomFile.exists()) {
            logger.warn("BOM POM file not found at ${pomFile.absolutePath}")
            logger.warn("Run 'generatePomFileForMavenPublication' task first")
            return@doLast
        }

        val pomContent = pomFile.readText()
        var missingModules = 0

        println("Verifying BOM contents:")
        expectedModules.forEach { module ->
            val isPresent = pomContent.contains("<artifactId>$module</artifactId>")
            println("  $module: ${if (isPresent) "✓" else "✗"}")
            if (!isPresent) missingModules++
        }

        if (missingModules > 0) {
            throw GradleException("$missingModules module(s) are missing from the BOM!")
        } else {
            println("✓ All ${expectedModules.size} modules are present in the BOM")
        }

        // Verify it's published as a platform
        if (pomContent.contains("<packaging>pom</packaging>")) {
            println("✓ BOM correctly published with POM packaging")
        } else {
            logger.warn("⚠️  BOM may not have correct POM packaging")
        }
    }
}

tasks.register("publishAndVerify") {
    group = "publishing"
    description = "Publishes the BOM to Maven Local and verifies its contents"

    dependsOn("publishToMavenLocal")
    finalizedBy("verifyBomContents")
}

// Optional: Debug task to see available tasks
tasks.register("listPublishingTasks") {
    group = "help"
    description = "Lists all publishing-related tasks"

    doLast {
        println("Available publishing tasks:")
        tasks.matching { it.name.contains("publish", ignoreCase = true) || it.name.contains("pom", ignoreCase = true) }
            .forEach { println("  - ${it.name}") }
    }
}