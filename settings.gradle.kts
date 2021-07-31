rootProject.name = "envel"

pluginManagement {
    val graphqlKotlinVersion: String by settings
    val springBootVersion: String by settings
    val springDependencyManagementVersion: String by settings
    val flywayVersion: String by settings
    val jvmVersion: String by settings

    plugins {
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyManagementVersion
        id("org.flywaydb.flyway") version flywayVersion
        id("com.expediagroup.graphql") version graphqlKotlinVersion

        kotlin("jvm") version jvmVersion
        kotlin("plugin.spring") version jvmVersion
        kotlin("plugin.jpa") version jvmVersion
    }
}

include("account", "audit", "gateway")