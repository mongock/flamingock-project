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

    implementation("org.reflections:reflections:0.10.1")
//    api("org.objenesis:objenesis:3.2")
    api("org.yaml:snakeyaml:2.2")
//
    api("org.apache.httpcomponents:httpclient:4.5.14")
    
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    

//
//    testImplementation(project(":commons:test-utils"))
}