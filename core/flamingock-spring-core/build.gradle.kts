repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":flamingock-core"))

    compileOnly("org.springframework:spring-context:5.+")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
