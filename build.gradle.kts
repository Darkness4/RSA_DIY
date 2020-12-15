import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.20"
    id("com.diffplug.spotless").version("5.8.2")

    application
}
group = "me.nguye"
version = "1.0"

configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
        ktlint("0.40.0")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint("0.40.0")
    }
}

repositories {
    jcenter()
}
dependencies {
    val kotest_version = "4.3.1"
    testImplementation(kotlin("test-junit5"))
    testImplementation("io.mockk:mockk:1.10.0")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.kotest:kotest-assertions-core:$kotest_version")
    testImplementation("io.kotest:kotest-property:$kotest_version")
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClass.set("MainKt")
}