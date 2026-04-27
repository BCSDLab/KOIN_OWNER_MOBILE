plugins {
    alias(libs.plugins.business.feature)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.common)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(compose.materialIconsExtended)
        }
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "koreatech.business.designsystem.resources"
}

android {
    namespace = "in.koreatech.business.core.designsystem"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
