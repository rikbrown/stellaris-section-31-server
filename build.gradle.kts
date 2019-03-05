import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion by extra { "1.1.3" }

plugins {
    java
    kotlin("jvm") version "1.3.21"
    idea
    application
}

group = "codes.rik.klausewitz"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

repositories {
    mavenCentral()
    jcenter()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.1.1")

    // Klausewitz
    implementation("codes.rik.klausewitz", "parser", "1.0-SNAPSHOT")

    // 1P
    implementation("codes.rik", "kotlin-pieces", "0.+")

    // 3P
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("com.fasterxml.jackson.core", "jackson-databind", "2.9.+")
    implementation("com.fasterxml.jackson.datatype", "jackson-datatype-jsr310", "2.9.+")
    implementation("com.fasterxml.jackson.module", "jackson-module-kotlin", "2.9.+")
    implementation("com.github.ntrrgc", "ts-generator", "1.1.1")
    implementation("com.google.guava", "guava", "27.0.1-jre")
    implementation("io.github.microutils", "kotlin-logging", "1.6.24")
    implementation("io.ktor", "ktor-jackson", ktorVersion)
    implementation("io.ktor", "ktor-server-netty", ktorVersion)
    implementation("net.lingala.zip4j", "zip4j", "1.3.2")
    implementation("org.litote.kmongo", "kmongo", "3.9.2")
//    implementation("org.mongodb", "mongo-java-driver", "3.10.+")

    // Test
    testImplementation("junit", "junit", "4.12")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}