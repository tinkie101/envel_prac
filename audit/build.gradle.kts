import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.3"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("org.flywaydb.flyway") version "7.11.2"

    kotlin("jvm") version "1.5.21"
    kotlin("plugin.spring") version "1.5.21"
    kotlin("plugin.jpa") version "1.5.21"
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    val mockitoKotlinVersion: String by project
    val graphqlSpqrVersion: String by project

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.leangen.graphql:graphql-spqr-spring-boot-starter:$graphqlSpqrVersion")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.postgresql:postgresql")
    implementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "11"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

flyway {
    url = "jdbc:postgresql://localhost:4321/envel"
    user = "postgres_user"
    password = "postgres_password"
    schemas = arrayOf("audit")
    locations = arrayOf("filesystem:flyway")
}