dependencies {
    implementation(project(":flamingock-core"))
    implementation(project(":local-drivers:mongodb:mongodb-facade"))

    implementation("org.mongodb:mongodb-driver-sync:4.3.3")


    testImplementation(project(":utils-test"))
}

description = "${project.name}'s description"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}