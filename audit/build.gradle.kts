import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")

    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
}

java.sourceCompatibility = JavaVersion.VERSION_11

sourceSets.create("integration") {
    compileClasspath += sourceSets.main.get().output
    runtimeClasspath += sourceSets.main.get().output
}

val integrationImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.implementation.get())
}

configurations["integrationRuntimeOnly"].extendsFrom(configurations.runtimeOnly.get())

dependencies {
    val mockitoKotlinVersion: String by project
    val graphqlSpqrVersion: String by project

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.postgresql:postgresql")
    implementation("io.leangen.graphql:graphql-spqr-spring-boot-starter:$graphqlSpqrVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.mockito:mockito-inline")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    integrationImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
}
tasks {
    task<Test>("integration") {
        description = "Runs the integration tests"
        group = "verification"

        testClassesDirs = sourceSets["integration"].output.classesDirs
        classpath = sourceSets["integration"].runtimeClasspath
        shouldRunAfter("test")
    }
    
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
    user = System.getenv("AUDIT_USER")
    password = System.getenv("AUDIT_PASSWORD")
    defaultSchema = "audit"
    schemas = arrayOf("audit")
    locations = arrayOf("filesystem:flyway")
}