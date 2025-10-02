plugins {
    alias(libs.plugins.business.application)
    alias(libs.plugins.business.orbit)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.koin.android)
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.analytics)
        }
        commonMain.dependencies {
            implementation(projects.data)
            implementation(projects.domain)

            implementation(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            api(libs.koin.annotation)
            implementation(libs.koin.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.napier)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "in.koreatech.business"

    defaultConfig {
        applicationId = "in.koreatech.business"
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".dev"
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
    add("kspCommonMainMetadata", libs.koin.ksp.compiler)
}
