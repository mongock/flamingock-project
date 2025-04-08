val jacksonVersion = "2.16.0"
dependencies {
    api(project(":flamingock-core-api"))
    api(project(":utils"))
    api("org.yaml:snakeyaml:2.2")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
}

description = "Annotation processor for Flamingock. Generates code and metadata from Flamingock annotations such as " +
        "@Change and @ChangeUnit, as well as from templated changes, enabling compile-time processing."

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}
