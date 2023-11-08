dependencies {
    api(project(":core:flamingock-core"))

    compileOnly("org.springframework:spring-context:5.+")
    compileOnly("org.springframework.boot:spring-boot:2.7.12")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")

    testImplementation("org.springframework:spring-context:5.+")


}
