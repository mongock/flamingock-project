
dependencies {
    api(project(":community:mongodb:mongodb-facade"))
    api(project(":community:mongodb:mongodb-sync-v4-driver"))

    implementation("org.springframework.data:spring-data-mongodb:3.2.12")
    implementation("org.mongodb:mongodb-driver-sync:4.3.3")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")

    testImplementation(project(":core:flamingock-core"))
    testImplementation("org.testcontainers:mongodb:1.18.3")
    testImplementation("org.testcontainers:junit-jupiter:1.18.3")
}
