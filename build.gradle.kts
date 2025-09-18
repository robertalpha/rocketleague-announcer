val ktor_version: String by project
val htmx_version: String by project
val tailwindcss_version: String by project
val kotlinwind_version: String by project
val kotlin_version: String by project
val kotest_version: String by project
val kotlinx_serialization_json_version: String by project
val logback_version: String by project
val testcontainers_version: String by project
val eclipse_paho_version: String by project
val discord_voice_version: String by project

plugins {
    application
    kotlin("jvm") version "2.1.21"
    kotlin("plugin.serialization") version "2.1.21"

    id("io.ktor.plugin") version "3.2.3"
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

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-config-yaml:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-server-call-logging:${ktor_version}")
    implementation("io.ktor:ktor-server-webjars:${ktor_version}")
    implementation("org.webjars.npm:htmx.org:${htmx_version}")

    implementation("io.github.allangomes:kotlinwind-css:$kotlinwind_version")
    implementation("org.webjars.npm:tailwindcss__browser:$tailwindcss_version")


    implementation("io.ktor:ktor-server-html-builder:${ktor_version}")
    implementation("io.ktor:ktor-client-core:${ktor_version}")
    implementation("io.ktor:ktor-client-cio:${ktor_version}")

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_json_version")


    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:$eclipse_paho_version")

    implementation("com.janoz.discord:discord-voice:$discord_voice_version" )

    testImplementation("org.testcontainers:testcontainers:$testcontainers_version")
    testImplementation("org.testcontainers:junit-jupiter:$testcontainers_version")

    testImplementation("io.ktor:ktor-server-test-host:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    testImplementation("io.kotest:kotest-runner-junit5:${kotest_version}")
    testImplementation("io.kotest:kotest-assertions-core:${kotest_version}")

}
