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
            // Tab "More" surfaces theme + owner profile via SettingsViewModel.
            // Decoupling further would need extracting that VM up the module graph.
            implementation(projects.feature.settings)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.material3.adaptive.navigation.suite)
        }
        commonTest.dependencies {
            implementation(libs.orbit.test)
        }
    }
}

android {
    namespace = "in.koreatech.business.feature.store"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
