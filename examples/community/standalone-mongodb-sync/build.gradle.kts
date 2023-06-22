
val mongodbVersion = "4.3.3"
dependencies {
    implementation(project(":community:standalone-runner"))
    implementation(project(":community:mongodb:mongodb-sync-v4-driver"))
    implementation("org.mongodb:mongodb-driver-sync:$mongodbVersion")
    implementation("org.mongodb:mongodb-driver-core:$mongodbVersion")
    implementation("org.mongodb:bson:$mongodbVersion")

    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}
