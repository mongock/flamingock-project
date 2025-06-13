//dependencies {
//    api(project(":core:flamingock-core"))
//    compileOnly("org.springframework.boot:spring-boot:3.1.3")
//    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.1.3")
//    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")
//
//    compileOnly("org.springframework:spring-context:6.+")
//
//    testImplementation("org.springframework:spring-context:6.+")
//
//}
//
//description = "${project.name}'s description"
//
//java {
//    toolchain {
//        languageVersion.set(JavaLanguageVersion.of(17))
//    }
//}

val versions = mapOf(
    "spring" to "[6.0.0,7.0.0)",
    "springBoot" to "[3.0.0,4.0.0)"
)

dependencies {
    api(project(":core:flamingock-core"))
    compileOnly("org.springframework:spring-context:${versions["spring"]}")
    compileOnly("org.springframework.boot:spring-boot:${versions["springBoot"]}")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:${versions["springBoot"]}")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:${versions["springBoot"]}")

    testImplementation("org.springframework:spring-context:5.+")
}

description = "Spring Boot v2.x integration module for Flamingock, providing seamless configuration and autoconfiguration capabilities for Spring-based applications"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

configurations.testImplementation {
    extendsFrom(configurations.compileOnly.get())
}