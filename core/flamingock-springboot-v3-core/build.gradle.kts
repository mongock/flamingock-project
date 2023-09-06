dependencies {
    api(project(":core:flamingock-core"))

    compileOnly("org.springframework:spring-context:6.+")
    compileOnly("org.springframework.boot:spring-boot:3.1.3")

    testImplementation("org.springframework:spring-context:6.+")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}
