dependencies {
//    INTERNAL
    api(project(":flamingock-spring-core"))
    api(project(":base-community"))

    compileOnly("org.springframework.boot:spring-boot:2.7.12")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.7.12")



}