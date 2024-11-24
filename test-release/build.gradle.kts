plugins {
    id("java")
}

tasks {
    // Create sources JAR
    register<Jar>("sourcesJar") {
        from(sourceSets["main"].allSource)
        archiveClassifier.set("sources")
    }

    // Create Javadoc JAR
    register<Jar>("javadocJar") {
        from(javadoc)
        archiveClassifier.set("javadoc")
    }
}

description = "Internal util library"

tasks.named("jreleaserFullRelease") {
    doFirst {
        val outputDir = file("${layout.buildDirectory}/jreleaser")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
}

val jacksonVersion = "2.16.0"
dependencies {


}