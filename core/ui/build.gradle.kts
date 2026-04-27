plugins {
    alias(libs.plugins.business.feature)
    alias(libs.plugins.business.orbit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(projects.core.common)
            implementation(projects.core.designsystem)
            implementation(libs.compose.ui.backhandler)
            implementation(compose.materialIconsExtended)
        }
    }
}

android {
    namespace = "in.koreatech.business.core.ui"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
