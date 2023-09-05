dependencies {
//    INTERNAL
    api(project(":core:flamingock-spring-core"))
    api(project(":community:base-community"))

    compileOnly("org.springframework.boot:spring-boot:3.1.3")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:3.1.3")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:3.1.3")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}