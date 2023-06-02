
dependencies {
//    INTERNAL
    implementation(project(":core"))
    implementation(project(":mongock"))
    implementation(project(":mongodb-facade"))

//    GENERAL
    implementation("org.slf4j", "slf4j-api", "2.0.6")

    implementation("org.mongodb:mongo-java-driver:3.12.8")

//    TEST
    testImplementation("org.slf4j:slf4j-simple:2.0.6")
}
