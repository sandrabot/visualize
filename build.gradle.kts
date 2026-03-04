/*
 * Copyright 2026 Avery Carroll and contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

val logback_version: String by project

plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("com.github.gmazzo.buildconfig") version "6.0.7"
    id("io.ktor.plugin") version "3.4.0"
}

group = "com.sandrabot"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-java")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("ch.qos.logback:logback-classic:$logback_version")
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("MainKt")
}

buildConfig {
    packageName("")
    className("BuildInfo")
    buildConfigField("VERSION", provider { "${project.version}" })
    buildConfigField("COMMIT", gitCommand("rev-parse", "HEAD"))
    buildConfigField("LOCAL_CHANGES", gitCommand("diff", "--shortstat"))
    buildConfigField("BUILD_TIME", System.currentTimeMillis())
    useKotlinOutput { internalVisibility = false }
}

fun gitCommand(vararg parts: String) = providers.exec {
    commandLine("git", *parts)
}.standardOutput.asText.get().trim()
