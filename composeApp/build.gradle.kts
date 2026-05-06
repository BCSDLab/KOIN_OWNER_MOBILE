import org.gradle.api.tasks.JavaExec
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.business.application)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(projects.umbrella)
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
        }
        commonMain.dependencies {
            implementation(projects.umbrella)
            implementation(libs.napier)
        }
        jvmMain.dependencies {
            implementation(projects.umbrella)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi)
            packageName = "KoinOwner"
            packageVersion = "1.0.0"
            vendor = "BCSDLab"
            description = "KOIN Owner Desktop"

            macOS {
                iconFile.set(project.file("icons/icon-mac.icns"))
            }
            windows {
                iconFile.set(project.file("icons/icon-windows.ico"))
            }
            linux {
                iconFile.set(project.file("icons/icon-linux.png"))
            }
        }
    }
}

// 로컬 개발 실행(`./gradlew run`)은 stage,
// 패키징 배포본은 기본값 prod 유지
tasks.matching { it.name == "run" }.configureEach {
    if (this is JavaExec) systemProperty("app.debug", "true")
}

android {
    namespace = "in.koreatech.business"

    defaultConfig {
        applicationId = "in.koreatech.business"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0.0"
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
