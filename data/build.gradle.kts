plugins {
    alias(libs.plugins.business.library)
}

kotlin {
    sourceSets {
        androidMain.dependencies {
        }

        commonMain.dependencies {
            implementation(projects.domain)
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
