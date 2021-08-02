version = "0.0.1-SNAPSHOT"

plugins {
    id("org.sonarqube") version "2.8"
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

sonarqube {
    properties {
        property("sonar.projectKey", "tinkie101_envel_prac")
        property("sonar.organization", "tinkie101")
        property("sonar.host.url", "https://sonarcloud.io")

        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.dynamicAnalysis", "reuseReports")
        property("sonar.language", "kotlin")

    }
}