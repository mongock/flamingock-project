dependencies {
    implementation(project(":flamingock-core-api"))
    implementation(project(":flamingock-core-template"))
    implementation("org.mongodb:mongodb-driver-sync:4.3.3")
}

description = "${project.name}'s description"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}