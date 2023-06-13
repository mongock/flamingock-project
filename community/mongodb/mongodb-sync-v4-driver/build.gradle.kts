
dependencies {
    api(project(":mongodb-facade"))

    implementation("org.mongodb:mongo-java-driver:3.12.8")

    testImplementation(project(":standalone-runner"))
    testImplementation("org.testcontainers:mongodb:1.18.3")

}
