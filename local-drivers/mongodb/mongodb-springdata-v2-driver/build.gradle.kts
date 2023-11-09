
dependencies {
    api(project(":local-drivers:mongodb:mongodb-facade"))
    api(project(":local-drivers:mongodb:mongodb-v3-driver"))

    implementation("org.springframework.data:spring-data-mongodb:2.2.13.RELEASE")
    implementation("org.mongodb:mongodb-driver-sync:3.11.3")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")

    testImplementation(project(":core:flamingock-core"))
    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}
