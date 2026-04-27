import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "in.koreatech.business.convention"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.compose.compiler.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("businessApplication") {
            id = "in.koreatech.business.plugin.application"
            implementationClass = "BusinessApplicationPlugin"
        }
        register("businessFeature") {
            id = "in.koreatech.business.plugin.feature"
            implementationClass = "BusinessFeaturePlugin"
        }
        register("businessLibrary") {
            id = "in.koreatech.business.plugin.library"
            implementationClass = "BusinessLibraryPlugin"
        }
        register("businessOrbit") {
            id = "in.koreatech.business.plugin.orbit"
            implementationClass = "BusinessOrbitPlugin"
        }
    }
}
