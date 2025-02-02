val jacksonVersion = "2.16.0"

dependencies {
    implementation(project(":utils"))
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}