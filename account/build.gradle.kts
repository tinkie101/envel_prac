import com.expediagroup.graphql.plugin.gradle.tasks.GraphQLGenerateClientTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    jacoco
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("org.flywaydb.flyway")
    id("com.expediagroup.graphql")

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
    val graphqlKotlinVersion: String by project
    val graphqlKotlinTypesVersion: String by project

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.postgresql:postgresql")
    implementation("io.leangen.graphql:graphql-spqr-spring-boot-starter:$graphqlSpqrVersion")
    implementation("com.expediagroup:graphql-kotlin-spring-client:$graphqlKotlinVersion")
    implementation("com.expediagroup:graphql-kotlin-types:$graphqlKotlinTypesVersion")

    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    integrationImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")
    integrationImplementation("org.springframework.boot:spring-boot-starter-test")
    integrationImplementation("org.springframework.security:spring-security-test")
}

tasks {
    test {
        finalizedBy(jacocoTestReport)
    }

    jacocoTestReport {
        dependsOn(test)
        reports {
            xml.required.set(true)
            html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
        }
    }

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

    withType<com.expediagroup.graphql.plugin.gradle.tasks.GraphQLIntrospectSchemaTask> {
        enabled = false
    }

    withType<GraphQLGenerateClientTask> {
        dependsOn(clean)

        schemaFile.set(File("${project.projectDir}/src/main/resources/graphql/schema.graphql"))
    }
}

graphql {
    client {
        endpoint = "http://localhost:8081/graphql"
        packageName = "com.example.account.graphql.client"
        queryFileDirectory = "${project.projectDir}/src/main/resources/graphql/client"
    }
}

flyway {
    url = "jdbc:postgresql://localhost:4321/envel"
    user = System.getenv("ACCOUNT_USER")
    password = System.getenv("ACCOUNT_PASSWORD")
    schemas = arrayOf("account")
    defaultSchema = "account"
    locations = arrayOf("filesystem:flyway")
}