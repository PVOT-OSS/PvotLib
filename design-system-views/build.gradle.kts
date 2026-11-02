// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

plugins {
    alias(libs.plugins.pvot.android.library)
    alias(libs.plugins.pvot.android.tokens)
    `maven-publish`
}

android {
    namespace = "com.prauga.pvot.designsystem.views"
    defaultConfig { minSdk = 24 }
    publishing { singleVariant("release") { withSourcesJar() } }
}

dependencies { api(libs.google.material) }

configurations.configureEach {
    withDependencies {
        removeIf { it.group == "org.jetbrains.kotlin" && it.name == "kotlin-stdlib" }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "design-system-views"
            afterEvaluate { from(components["release"]) }
            pom {
                name.set("Pvot Design System for Views")
                description.set("Pvot's theme for XML layouts and Material Components widgets.")
            }
        }
    }
}
