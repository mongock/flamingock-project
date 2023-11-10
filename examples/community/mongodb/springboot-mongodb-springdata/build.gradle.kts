plugins {
    java
    id("org.springframework.boot") version "2.7.12"
    id("io.spring.dependency-management") version "1.1.0"
}

dependencies {

    implementation("com.mysql:mysql-connector-j:8.2.0")
    implementation("io.flamingock:flamingock-template:0.0.2-SNAPSHOT")

    implementation(project(":flamingock-springboot-v2-runner"))
    implementation(project(":local-drivers:mongodb:mongodb-springdata-v3-driver"))

    implementation(project(":examples:community:mongodb:mysql-template"))
    
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation("org.testcontainers:mongodb:1.18.3")

    testImplementation("org.testcontainers:junit-jupiter:1.18.3")

    testImplementation("org.springframework.boot:spring-boot-starter-test")


}

