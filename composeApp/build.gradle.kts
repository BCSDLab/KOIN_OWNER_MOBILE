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
            description = "KOIN 사장님 데스크톱 앱"
        }
    }
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
