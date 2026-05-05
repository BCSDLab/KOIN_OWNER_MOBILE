plugins {
    alias(libs.plugins.business.library)
}

kotlin {
    val isMacOS = System.getProperty("os.name").contains("mac", ignoreCase = true)

    sourceSets {
        androidMain.dependencies {
            implementation(libs.koin.android)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)
        }

        commonMain.dependencies {
            implementation(projects.domain)

            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preference)
            implementation(libs.kotlinx.coroutines.core)
            implementation(project.dependencies.platform(libs.koin.bom))
            implementation(libs.koin.core)
            implementation(libs.ktor.client.auth)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.logging)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.napier)
        }

        if (isMacOS) {
            iosMain.dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
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
