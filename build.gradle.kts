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

// KSP의 AndroidTest classpath 직렬화가 Gradle configuration cache와 호환되지 않아
// 이 task가 그래프에 포함되면 ktlintFormat/Check 등 다른 task의 캐시 저장 단계가
// 실패한다. 영향 범위가 좁은 task 단위 마킹으로 우회.
subprojects {
    tasks.matching { it.name.startsWith("ksp") && it.name.contains("AndroidTest") }
        .configureEach {
            notCompatibleWithConfigurationCache(
                "KSP AndroidTest classpath serialization fails under Gradle configuration cache"
            )
        }
}
