
dependencies {
    api(project(":community:mongodb:mongodb-facade"))


    implementation("org.mongodb:mongodb-driver-sync:4.3.3")

    testImplementation(project(":community:standalone-runner"))
    testImplementation("org.testcontainers:mongodb:1.18.3")

}
