package `in`.koreatech.business.convention

import org.gradle.api.Project
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureMultiplatformProject(
    kotlinMultiplatformExtension: KotlinMultiplatformExtension,
) {
    kotlinMultiplatformExtension.apply {
        targets.configureEach {
            compilations.configureEach {
                compileTaskProvider.get().compilerOptions {
                    // See: https://youtrack.jetbrains.com/issue/KT-61573
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                }
            }
        }

        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        jvm {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }

        val isMacOS = System.getProperty("os.name").contains("mac", ignoreCase = true)
        if (isMacOS) {
            // iosX64 (Intel iOS simulator) is dropped by Compose Multiplatform 1.11.0 /
            // androidx-lifecycle 2.11.0; only arm64 device + simulator are supported.
            iosArm64()
            iosSimulatorArm64()
        }

        sourceSets.apply {
            commonMain.configure {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
            commonTest.dependencies {
                implementation(libs.findLibrary("kotlin-test").get())
                implementation(libs.findLibrary("kotlinx-coroutines-test").get())
                implementation(libs.findLibrary("turbine").get())
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
        if (name != "kspCommonMainKotlinMetadata" && project.tasks.findByName("kspCommonMainKotlinMetadata") != null) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }

    // Fix ktlint bug
    // https://github.com/JLLeitschuh/ktlint-gradle/issues/724
    tasks.matching { it.name == "runKtlintCheckOverCommonMainSourceSet" || it.name == "runKtlintFormatOverCommonMainSourceSet" }.configureEach {
        if (project.tasks.findByName("kspCommonMainKotlinMetadata") != null) {
            dependsOn("kspCommonMainKotlinMetadata")
        }
    }
}
