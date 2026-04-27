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
            // Cross-feature: store renders settings screens (ThemeSettings/Privacy/Terms/OSS)
            // and embeds findpassword/insertstore navigation graphs.
            implementation(projects.feature.settings)
            implementation(projects.feature.auth)
            implementation(projects.feature.insertstore)
            implementation(libs.compose.ui.backhandler)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(compose.materialIconsExtended)
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
