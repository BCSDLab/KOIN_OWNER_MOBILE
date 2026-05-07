plugins {
    alias(libs.plugins.business.umbrella)
    alias(libs.plugins.business.orbit)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(projects.core.common)
            api(projects.core.designsystem)
            api(projects.core.ui)
            api(projects.domain)
            api(projects.data)
            api(projects.feature.auth)
            api(projects.feature.store)
            api(projects.feature.settings)
            api(projects.feature.insertstore)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.napier)
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preference)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.compose.viewmodel.navigation)
            implementation(libs.androidx.navigation.compose)
        }
        androidMain.dependencies {
            implementation(libs.koin.android)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.orbit.test)
            implementation(libs.koin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
    }

    val isMacOS = System.getProperty("os.name").contains("mac", ignoreCase = true)
    if (isMacOS) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { target ->
            target.binaries.framework {
                baseName = "Umbrella"
                isStatic = false
                export(projects.core.common)
                export(projects.core.designsystem)
                export(projects.core.ui)
                export(projects.domain)
                export(projects.data)
                export(projects.feature.auth)
                export(projects.feature.store)
                export(projects.feature.settings)
                export(projects.feature.insertstore)
            }
        }
    }
}

android {
    namespace = "in.koreatech.business.umbrella"

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}
