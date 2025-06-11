
dependencies {
    constraints {
        // Database modules
        api("io.flamingock:flamingock-ce-mongodb-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-sync-v4:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v2:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v3:${project.version}")
        api("io.flamingock:flamingock-ce-mongodb-springdata-v4:${project.version}")
        api("io.flamingock:flamingock-ce-couchbase:${project.version}")
        api("io.flamingock:flamingock-ce-dynamodb:${project.version}")

        // Templates
        api("io.flamingock:flamingock-sql-template:${project.version}")
        api("io.flamingock:flamingock-mongodb-change-template:${project.version}")
    }
}

val expectedModules = listOf(
    "flamingock-ce-mongodb-v3",
    "flamingock-ce-mongodb-sync-v4",
    "flamingock-ce-mongodb-springdata-v2",
    "flamingock-ce-mongodb-springdata-v3",
    "flamingock-ce-mongodb-springdata-v4",
    "flamingock-ce-couchbase",
    "flamingock-ce-dynamodb",
    "flamingock-sql-template",
    "flamingock-mongodb-change-template"
)

tasks.register("verifyBomContents") {
    group = "verification"
    description = "Verifies that all required modules are included in the BOM"

    doLast {
        val bomFile = layout.buildDirectory.file("publications/maven/pom-default.xml").get().asFile

        if (!bomFile.exists()) {
            logger.lifecycle("""
                BOM file not found at ${bomFile.absolutePath}.
                This is normal if you haven't published the BOM yet.
                
                To verify the BOM content, you need to:
                1. Run './gradlew :community:flamingock-ce-bom:publishToMavenLocal' first
                2. Then run this verification task
            """.trimIndent())
            return@doLast
        }

        println("Verifying BOM contents in: ${bomFile.absolutePath}")
        val bomContent = bomFile.readText()

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
            println("All ${expectedModules.size} modules are present in the BOM.")
        }
    }
}

tasks.register("publishAndVerify") {
    group = "publishing"
    description = "Publishes the BOM to Maven Local and verifies its contents"

    dependsOn("publishToMavenLocal")
    finalizedBy("verifyBomContents")
}