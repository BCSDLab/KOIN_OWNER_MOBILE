import com.android.build.gradle.LibraryExtension
import `in`.koreatech.business.convention.configureAndroidProject
import `in`.koreatech.business.convention.configureComposeProject
import `in`.koreatech.business.convention.configureMultiplatformProject
import `in`.koreatech.business.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class BusinessUmbrellaPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(libs.findPlugin("kotlinMultiplatform").get().get().pluginId)
                apply(libs.findPlugin("androidLibrary").get().get().pluginId)
                apply(libs.findPlugin("composeMultiplatform").get().get().pluginId)
                apply(libs.findPlugin("composeCompiler").get().get().pluginId)
                apply(libs.findPlugin("ktlint").get().get().pluginId)
            }

            extensions.configure<LibraryExtension> {
                configureAndroidProject(this)
            }

            extensions.configure<KotlinMultiplatformExtension> {
                configureMultiplatformProject(this)
                configureComposeProject(this)
            }
        }
    }
}
