package `in`.koreatech.business.convention

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureComposeProject(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        sourceSets.apply {
            commonMain.dependencies {
                implementation(libs.findLibrary("compose-foundation").get())
                implementation(libs.findLibrary("compose-runtime").get())
                implementation(libs.findLibrary("compose-material3").get())
                implementation(libs.findLibrary("compose-ui").get())
                implementation(libs.findLibrary("compose-ui-backhandler").get())
                implementation(libs.findLibrary("compose-components-resources").get())
                implementation(libs.findLibrary("compose-components-ui-tooling-preview").get())
            }
        }
    }
}
