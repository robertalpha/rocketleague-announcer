import io.miret.etienne.gradle.sass.CompileSass

val ktor_version: String by project
val htmx_version: String by project
val webjars_htmx_ext_sse_version: String by project
val webjars_htmx_ext_json_enc_version: String by project
val kotlin_version: String by project
val kotest_version: String by project
val kotlinx_serialization_json_version: String by project
val logback_version: String by project
val testcontainers_version: String by project
val eclipse_paho_version: String by project
val discord_voice_version: String by project
val kotlin_logging_version: String by project

plugins {
    application
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.serialization") version "2.2.20"

    id("io.ktor.plugin") version "3.3.0"

    // ./gradlew dependencyUpdates
    id("com.github.ben-manes.versions") version "0.53.0"

    // ./gradlew spotlessKotlinCheck
    id("com.diffplug.spotless") version "8.0.0"

    // sass compiler
    id("io.miret.etienne.sass") version "1.6.0"

}

kotlin{
    compilerOptions {
        freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
    }
}


group = "nl.vanalphenict"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

// Static code analysis tools, for later
spotless {
    kotlin {
        target("src/**/*.kt")
        targetExclude("src/main/kotlin/model/Announcement.kt")
        ktfmt().kotlinlangStyle()
    }
}

tasks.compileSass {
    entryPoint("main.scss", "style.css")
    destPath = "${project.layout.buildDirectory.get()}/resources/main/web/style"
    sourceMap = CompileSass.SourceMap.none
    style = compressed
}

tasks.processResources.configure { finalizedBy("compileSass") }

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-sse:${ktor_version}")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-server-call-logging:${ktor_version}")
    implementation("io.ktor:ktor-server-webjars:${ktor_version}")

    implementation("org.webjars.npm:htmx.org:${htmx_version}")
    implementation("org.webjars.npm:htmx-ext-sse:${webjars_htmx_ext_sse_version}")
    implementation("org.webjars.npm:htmx-ext-json-enc:${webjars_htmx_ext_json_enc_version}")

    implementation("io.ktor:ktor-server-html-builder:${ktor_version}")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")


    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:$eclipse_paho_version")

    implementation("com.janoz.discord:discord-voice:$discord_voice_version" )
    implementation("io.github.oshai:kotlin-logging-jvm:$kotlin_logging_version")

    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainers_version")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    testImplementation("io.kotest:kotest-runner-junit5:${kotest_version}")
    testImplementation("io.kotest:kotest-assertions-core:${kotest_version}")

}
