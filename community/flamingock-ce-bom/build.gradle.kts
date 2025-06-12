
// Since we can't apply java-platform due to java/java-library already being applied,
// we'll modify the existing configuration directly

// Don't add any source code to this project
sourceSets {
    main {
        java.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
    test {
        java.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
}

// Make sure no dependencies are added to runtime and mark as platform
configurations.runtimeClasspath {
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, "platform"))
    }
}

// Also mark the API configuration as a platform
configurations.apiElements {
    attributes {
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category::class.java, "platform"))
    }
}

// Define all platform dependencies
dependencies {
    constraints {
        api("io.flamingock:flamingock-core:${project.version}")
        api("io.flamingock:flamingock-core-api:${project.version}")

        // Database modules
        api("io.flamingock:flamingock-ce-mongodb-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-sync-v4:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v2:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v4:${project.version}")
        api("io.flamingock:flamingock-ce-couchbase:${project.version}")
        api("io.flamingock:flamingock-ce-dynamodb:${project.version}")
        api("io.flamingock:flamingock-ce-commons:${project.version}")  // Added this line

        // Templates
        api("io.flamingock:flamingock-sql-template:${project.version}")
        api("io.flamingock:flamingock-mongodb-change-template:${project.version}")
    }
}

// Make sure the Maven POM is configured properly
afterEvaluate {
    publishing {
        publications {
            // Get the existing maven publication
            val mavenPublication = publications.findByName("maven") as? MavenPublication

            if (mavenPublication != null) {
                // Mark this as a platform in Gradle metadata
                mavenPublication.versionMapping {
                    allVariants {
                        fromResolutionResult()
                    }
                }

                // Modify the existing publication
                mavenPublication.pom.withXml {
                    val root = asNode()

                    // First, remove any existing packaging element to add it in the correct position
                    val existingPackagingNodes = root.get("packaging") as? groovy.util.NodeList
                    if (existingPackagingNodes != null && !existingPackagingNodes.isEmpty()) {
                        root.remove(existingPackagingNodes[0] as groovy.util.Node)
                    }

                    // Add packaging right after groupId, artifactId, and version
                    root.appendNode("packaging", "pom")

                    // Remove any existing dependencies
                    val regularDependenciesNodes = root.get("dependencies") as? groovy.util.NodeList
                    if (regularDependenciesNodes != null && !regularDependenciesNodes.isEmpty()) {
                        root.remove(regularDependenciesNodes[0] as groovy.util.Node)
                    }

                    // Add dependencyManagement section with our module versions
                    val dependencyManagementNodes = root.get("dependencyManagement") as? groovy.util.NodeList
                    val dependencyManagement = if (dependencyManagementNodes != null && !dependencyManagementNodes.isEmpty()) {
                        dependencyManagementNodes[0] as groovy.util.Node
                    } else {
                        root.appendNode("dependencyManagement")
                    }

                    val dependenciesNodes = dependencyManagement.get("dependencies") as? groovy.util.NodeList
                    val dependencies = if (dependenciesNodes != null && !dependenciesNodes.isEmpty()) {
                        val deps = dependenciesNodes[0] as groovy.util.Node
                        deps.setValue(groovy.util.NodeList()) // Clear existing dependencies
                        deps
                    } else {
                        dependencyManagement.appendNode("dependencies")
                    }

                    // Add each module as a managed dependency
                    addManagedDependency(dependencies, "flamingock-core")
                    addManagedDependency(dependencies, "flamingock-core-api")
                    addManagedDependency(dependencies, "flamingock-commons")

                    // Database modules
                    addManagedDependency(dependencies, "flamingock-ce-mongodb-v3")
                    addManagedDependency(dependencies, "flamingock-ce-mongodb-sync-v4")
                    addManagedDependency(dependencies, "flamingock-ce-mongodb-springdata-v2")
                    addManagedDependency(dependencies, "flamingock-ce-mongodb-springdata-v3")
                    addManagedDependency(dependencies, "flamingock-ce-mongodb-springdata-v4")
                    addManagedDependency(dependencies, "flamingock-ce-couchbase")
                    addManagedDependency(dependencies, "flamingock-ce-dynamodb")

                    // Templates
                    addManagedDependency(dependencies, "flamingock-sql-template")
                    addManagedDependency(dependencies, "flamingock-mongodb-change-template")
                }
            } else {
                logger.warn("Could not find 'maven' publication to modify. BOM may not be correctly generated.")
            }
        }
    }
}

// Mark all configuration variants as platforms in the module metadata
tasks.withType<GenerateModuleMetadata> {
    doLast {
        val metadata = outputFile.asFile.get()
        if (metadata.exists()) {
            val content = metadata.readText()
            val updatedContent = content.replace(
                "\"org.gradle.category\": \"library\"",
                "\"org.gradle.category\": \"platform\""
            )
            metadata.writeText(updatedContent)
        }
    }
}

// Helper function to add a managed dependency to the POM
fun addManagedDependency(dependencies: groovy.util.Node, artifactId: String) {
    val dependency = dependencies.appendNode("dependency")
    dependency.appendNode("groupId", "io.flamingock")
    dependency.appendNode("artifactId", artifactId)
    dependency.appendNode("version", project.version)
}

// Make sure the jar task produces a minimal jar
tasks.withType<Jar> {
    exclude("**/*.class")
}

// List of modules to verify in the BOM
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

    // Templates
    "flamingock-sql-template",
    "flamingock-mongodb-change-template"
)

// Updated verification task
tasks.register("verifyBomContents") {
    group = "verification"
    description = "Verifies that all required modules are included in the BOM"

    doLast {
        val pomFile = layout.buildDirectory.file("publications/maven/pom-default.xml").get().asFile
        val moduleFile = layout.buildDirectory.file("publications/maven/module.json").get().asFile

        if (!pomFile.exists()) {
            logger.lifecycle("""
                BOM file not found at ${pomFile.absolutePath}.
                This is normal if you haven't published the BOM yet.
                
                To verify the BOM content, you need to:
                1. Run './gradlew :community:flamingock-ce-bom:publishToMavenLocal' first
                2. Then run this verification task
            """.trimIndent())
            return@doLast
        }

        // Verify the contents of the POM file
        println("Verifying BOM contents in POM: ${pomFile.absolutePath}")
        val bomContent = pomFile.readText()

        var missingModules = 0
        expectedModules.forEach { module ->
            val isPresent = bomContent.contains("<artifactId>$module</artifactId>")
            println("$module: ${if (isPresent) "✓" else "✗"}")
            if (!isPresent) {
                missingModules++
            }
        }

        if (missingModules > 0) {
            throw GradleException("$missingModules module(s) are missing from the BOM!")
        } else {
            println("All ${expectedModules.size} modules are present in the POM.")
        }

        if (moduleFile.exists()) {
            // Also verify the Gradle module metadata
            println("Verifying Gradle module metadata: ${moduleFile.absolutePath}")
            val moduleContent = moduleFile.readText()

            // Check that it's published as a platform
            if (!moduleContent.contains("\"org.gradle.category\": \"platform\"")) {
                println("⚠️ WARNING: The module is not published as a platform in Gradle metadata!")
                println("This may cause issues when consuming the BOM in Gradle projects.")
            } else {
                println("✓ Module is correctly published as a platform in Gradle metadata.")
            }
        }
    }
}

// Create a task that combines publishing and verification
tasks.register("publishAndVerify") {
    group = "publishing"
    description = "Publishes the BOM to Maven Local and verifies its contents"

    dependsOn("publishToMavenLocal")
    finalizedBy("verifyBomContents")
}