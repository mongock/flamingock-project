dependencies {
    api(project(":local-drivers:driver-common"))

    implementation("software.amazon.awssdk:dynamodb-enhanced:2.25.28")

    testImplementation("org.testcontainers:dynalite:1.20.1")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

}
