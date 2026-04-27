plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.detekt)
    alias(libs.plugins.spotless)
}

detekt {
    config.setFrom(files("config/detekt.yml"))
    baseline = file("config/detekt-baseline.xml")
    buildUponDefaultConfig = true
    source.setFrom(
        "composeApp/src",
        "data/src",
        "domain/src",
        "core/src"
    )
}

spotless {
    kotlin {
        target("**/*.kt")
        targetExclude("**/build/**")
        trimTrailingWhitespace()
        endWithNewline()
    }
    kotlinGradle {
        target("**/*.kts")
        targetExclude("**/build/**")
        trimTrailingWhitespace()
        endWithNewline()
    }
}
