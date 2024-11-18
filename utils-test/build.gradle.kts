plugins {
    //`maven-publish`
}

//publishing {
//    publications {
//        create<MavenPublication>("mavenJava") {
//            from(components["java"])
//            groupId = project.group.toString()
//            artifactId = project.name
//            version = project.version.toString()
//        }
//    }
//    repositories {
//        mavenLocal()
//    }
//}

val jacksonVersion = "2.16.0"
dependencies {

    api(project(":utils"))
    api(project(":flamingock-core-cloud-api"))

    api("javax.inject:javax.inject:1")
    api("org.reflections:reflections:0.10.1")
    api("org.objenesis:objenesis:3.2")
    api("org.yaml:snakeyaml:2.2")

    api("org.apache.httpcomponents:httpclient:4.5.14")
    
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    

    api("com.github.tomakehurst:wiremock-jre8:2.35.2")

}
