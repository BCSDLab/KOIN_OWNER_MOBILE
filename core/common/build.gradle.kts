plugins {
    alias(libs.plugins.business.feature)
    alias(libs.plugins.business.orbit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.napier)
        }
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "in.koreatech.business.core.common"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
