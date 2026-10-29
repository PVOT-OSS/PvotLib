// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    `kotlin-dsl`
}

group = "com.prauga.pvot.buildlogic"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.binaryCompatibilityValidator.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidLibrary") {
            id = "pvot.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
        register("androidApplication") {
            id = "pvot.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidCompose") {
            id = "pvot.android.compose"
            implementationClass = "AndroidComposeConventionPlugin"
        }
        register("androidApi") {
            id = "pvot.android.api"
            implementationClass = "AndroidApiConventionPlugin"
        }
    }
}
