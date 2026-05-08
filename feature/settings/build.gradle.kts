plugins {
    alias(libs.plugins.business.feature)
    alias(libs.plugins.business.orbit)
    alias(libs.plugins.aboutlibraries)
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
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.androidx.navigation.compose)
            implementation(compose.materialIconsExtended)
            implementation(libs.aboutlibraries.compose.m3)
        }
        commonTest.dependencies {
            implementation(libs.orbit.test)
        }
    }
}

compose.resources {
    publicResClass = false
    packageOfResClass = "in.koreatech.business.feature.settings.resources"
}

aboutLibraries {
    collect {
        fetchRemoteLicense = false
    }
    export {
        outputFile = file("src/commonMain/composeResources/files/aboutlibraries.json")
        prettyPrint = true
    }
}

android {
    namespace = "in.koreatech.business.feature.settings"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
