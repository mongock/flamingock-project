plugins {
    id("maven-publish")
    id("signing")
    id("java")
}


apply(plugin = "maven-publish")
apply(plugin = "signing")

extra["publish"] = "true"

val jacksonVersion = "2.16.0"
dependencies {

    implementation("org.reflections:reflections:0.10.1")
//    api("org.objenesis:objenesis:3.2")
    api("org.yaml:snakeyaml:2.2")
//
    api("org.apache.httpcomponents:httpclient:4.5.14")
    
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")

}