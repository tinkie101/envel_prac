version = "0.0.1-SNAPSHOT"

plugins {
    id("org.springframework.boot") apply false
    id("io.spring.dependency-management") apply false
    id("org.flywaydb.flyway") apply false

    kotlin("jvm") apply false
    kotlin("plugin.spring") apply false
    kotlin("plugin.jpa") apply false
}

subprojects {
    repositories {
        mavenCentral()
    }
}