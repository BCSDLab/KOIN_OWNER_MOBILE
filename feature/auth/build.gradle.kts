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
            implementation(projects.core.ui)
            implementation(projects.domain)
            implementation(projects.data)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(compose.materialIconsExtended)
        }
        commonTest.dependencies {
            implementation(libs.orbit.test)
        }
    }
}

android {
    namespace = "in.koreatech.business.feature.auth"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
