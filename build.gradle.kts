import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("org.springframework.boot") version "3.1.1"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.8.0"
    kotlin("plugin.spring") version "1.8.0"
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.springframework.boot:spring-boot-starter-web:3.1.1")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.1")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    runtimeOnly ("com.h2database:h2")

    implementation ("org.jetbrains.kotlin:kotlin-reflect")
    implementation ("com.fasterxml.jackson.module:jackson-module-kotlin")

    //https://github.com/Vonage/vonage-jwt-jdk
    implementation("com.vonage:jwt:1.0.2")
    //https://github.com/vonage/vonage-java-sdk
    implementation("com.vonage:client:7.6.0")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Jar>("jar") {
    isEnabled = false
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}


