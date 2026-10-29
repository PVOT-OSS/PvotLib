// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

subprojects {
    plugins.withId("maven-publish") {
        extensions.configure<PublishingExtension> {
            publications.withType<MavenPublication>().configureEach {
                pom {
                    url.set("https://github.com/PVOT-OSS/PvotLib")
                    licenses {
                        license {
                            name.set("Apache License 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0")
                        }
                    }
                    developers {
                        developer {
                            id.set("danascape")
                            name.set("Saalim Quadri")
                            url.set("https://github.com/danascape")
                        }
                    }
                    scm {
                        url.set("https://github.com/PVOT-OSS/PvotLib")
                        connection.set("scm:git:https://github.com/PVOT-OSS/PvotLib.git")
                    }
                }
            }
            val releaseRepo = providers.gradleProperty("releaseRepo")
            if (releaseRepo.isPresent) {
                repositories {
                    maven {
                        name = "release"
                        url = uri(releaseRepo.get())
                    }
                }
            }
        }
    }
}
