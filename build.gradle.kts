import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

val postgres_version: String by project
val exposedVersion: String by project
plugins {
    kotlin("jvm") version "1.9.21"
    id("io.ktor.plugin") version "2.3.6"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
    id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "io.github.zidbrain"
version = "0.0.1"

jib {
    from {
        image = "amazoncorretto:21"
        platforms {
            platform {
                os = "linux"
                val current = DefaultNativePlatform.getCurrentArchitecture().name
                architecture =
                    if (current.contains("aarch") || current.contains("arm")) "arm64"
                    else "amd64"
            }
        }
    }
    to {
        image = "fdelivery"
        tags = setOf("1.0")
    }
    container {
        jvmFlags = listOf("-Xdebug")
    }
}

application {
    mainClass.set("io.github.zidbrain.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm")
    implementation("io.ktor:ktor-server-content-negotiation-jvm")
    implementation("io.ktor:ktor-server-websockets-jvm")
    implementation("io.ktor:ktor-server-cors")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-crypt:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-json:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("io.ktor:ktor-server-call-logging-jvm")
    implementation("io.ktor:ktor-server-host-common-jvm")
    implementation("io.ktor:ktor-server-auth-jvm")
    implementation("io.ktor:ktor-server-auth-jwt-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.0")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}

val run by tasks.getting(JavaExec::class) {
    environment("DATABASE_HOST", "localhost")
    environment("STATIC_PATH", "./buid/resources/main/static")
}
