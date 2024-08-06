val jacksonVersion = "2.16.0"
dependencies {
    api(project(":utils"))
    api(project(":flamingock-core-api"))

    api("org.apache.httpcomponents:httpclient:4.5.14")
    api("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    api("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")

    testImplementation(project(":utils-test"))
}
