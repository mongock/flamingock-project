
dependencies {
    api(project(":community:base-community"))
    api(project(":community:couchbase:couchbase-driver"))

    implementation("org.springframework.data:spring-data-couchbase:4.4.8")
    implementation("com.couchbase.client:java-client:3.4.4")

    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")
}
