
dependencies {
    api(project(":drivers:mongodb:mongodb-facade"))


    implementation("org.mongodb:mongodb-driver-sync:4.3.3")

    testImplementation(project(":core:flamingock-core"))
    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

}
