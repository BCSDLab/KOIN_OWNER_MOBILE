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

        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "ComposeApp"
                isStatic = true
            }
        }

        sourceSets.apply {
            commonMain.configure {
                kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
            }
        }
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
        if (name != "kspCommonMainKotlinMetadata") {
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
