plugins {
    alias(libs.plugins.ktor.plugin)
    alias(libs.plugins.openapi.generator)
    alias(libs.plugins.google.devtools.ksp)
    kotlin("jvm") version "2.0.20"
}

group = "com.dpv"
version = "0.0.1"

application {
    mainClass = "com.ApplicationKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.logging)
    implementation(libs.bundles.di)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.bundles.db)
    implementation(libs.bundles.util)
    testImplementation(libs.bundles.testing)
}
