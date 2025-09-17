plugins {
    alias(libs.plugins.business.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "in.koreatech.business.domain"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
}
