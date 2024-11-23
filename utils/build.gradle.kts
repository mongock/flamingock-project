
//    project {
//        authors.set(listOf("dieppa"))
//        license.set("Apache-2.0")
//        links {
//            homepage.set("https://github.com/mongock/flamingock-project")
//        }
//        inceptionYear.set("2024")
//    }
//
//    release {
//        github {
//            repoOwner.set("dieppa")
//            overwrite.set(true)
//        }
//    }
//
//
//    distributions {
//        create(name) {
//            artifact {
//                path.set(file("build/libs/${name}-${version}.jar"))
//            }
//        }
//    }
//
//    deploy {
//        maven {
//            repository {
//                name.set("MavenCentral")
//                url.set("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
//                username.set(System.getenv("OSSRH_USERNAME")) // Add OSSRH_USERNAME to env variables
//                password.set(System.getenv("OSSRH_PASSWORD")) // Add OSSRH_PASSWORD to env variables
//            }
//        }
//    }
//}


tasks.named("jreleaserFullRelease") {
    doFirst {
        val outputDir = file("${layout.buildDirectory}/jreleaser")
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
    }
}
val jacksonVersion = "2.16.0"
dependencies {

    implementation("org.reflections:reflections:0.10.1")
//    api("org.objenesis:objenesis:3.2")
    api("org.yaml:snakeyaml:2.2")
//
    api("org.apache.httpcomponents:httpclient:4.5.14")
    
    api("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    

//
//    testImplementation(project(":commons:test-utils"))
}