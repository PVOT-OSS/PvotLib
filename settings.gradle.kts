// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins { id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0" }

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PvotLib"

gradle.lifecycle.beforeProject {
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

include(":catalog")

include(":design-system")

include(":core-ui")

include(":design-system-views")
