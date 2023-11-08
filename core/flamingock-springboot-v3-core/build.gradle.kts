dependencies {
    api(project(":core:flamingock-core"))
    compileOnly("org.springframework.boot:spring-boot:3.1.3")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.1.3")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")

    compileOnly("org.springframework:spring-context:6.+")

    testImplementation("org.springframework:spring-context:6.+")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
