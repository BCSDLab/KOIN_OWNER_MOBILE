package `in`.koreatech.business.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureOrbit(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("orbit-core").get())
                implementation(libs.findLibrary("orbit-viewmodel").get())
                implementation(libs.findLibrary("orbit-compose").get())
            }
        }
    }
}
