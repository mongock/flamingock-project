dependencies {
//    INTERNAL
    api(project(":flamingock-spring-core"))
    api(project(":community:base-community"))

    compileOnly("org.springframework.boot:spring-boot:2.7.12")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:2.7.12")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")



}