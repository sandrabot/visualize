/*
 * Copyright 2023 Avery Carroll
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.ByteArrayOutputStream

plugins {
    application
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("com.github.gmazzo.buildconfig") version "5.3.5"
    id("io.ktor.plugin") version "2.3.9"
}

group = "com.sandrabot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("reflect"))
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("io.ktor:ktor-client-java")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("MainKt")
}

buildConfig {
    packageName("")
    className("BuildInfo")
    val commit = executeCommand("git", "rev-parse", "HEAD")
    buildConfigField("String", "COMMIT", "\"$commit\"")
    buildConfigField("String", "VERSION", "\"$version\"")
    buildConfigField("String", "DETAILED_VERSION", "\"${version}_${commit.take(8)}\"")
}

fun executeCommand(vararg parts: String): String {
    val stdout = ByteArrayOutputStream()
    exec {
        commandLine = parts.asList()
        standardOutput = stdout
    }
    return stdout.toString("utf-8").trim()
}
