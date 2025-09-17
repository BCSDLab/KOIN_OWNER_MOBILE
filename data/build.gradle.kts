plugins {
    alias(libs.plugins.business.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
        }

        commonMain.dependencies {
            implementation(projects.domain)

            implementation(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
        }

        iosMain.dependencies {
        }
    }
}

android {
    namespace = "in.koreatech.business.data"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}

dependencies {
}
