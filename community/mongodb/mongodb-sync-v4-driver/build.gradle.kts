
dependencies {
//    INTERNAL
    implementation(project(":flamingock-core"))
    implementation(project(":base-community"))
    implementation(project(":mongodb-facade"))

//    GENERAL
    implementation("org.slf4j", "slf4j-api", "2.0.6")
    implementation("org.mongodb:mongo-java-driver:3.12.8")

//    TEST
    testImplementation(project(":standalone-runner"))
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
    testImplementation("org.testcontainers:mongodb:1.18.3")

}
