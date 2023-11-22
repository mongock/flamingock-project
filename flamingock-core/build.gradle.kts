val jacksonVersion = "2.16.0"
dependencies {
    api(project(":flamingock-template"))
    api("javax.inject:javax.inject:1")
    api("org.reflections:reflections:0.10.1")
    api("org.objenesis:objenesis:3.2")
    api("org.yaml:snakeyaml:2.2")

    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")


}
