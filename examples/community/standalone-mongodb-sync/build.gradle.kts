
dependencies {
    implementation(project(":community:standalone-runner"))
    implementation(project(":community:mongodb:mongodb-sync-v4-driver"))
    implementation("org.mongodb:mongodb-driver-sync:4.3.3")

    testImplementation("org.testcontainers:mongodb:1.18.3")
}
