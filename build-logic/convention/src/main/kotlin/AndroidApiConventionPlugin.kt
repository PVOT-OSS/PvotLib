// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import kotlinx.validation.KotlinApiBuildTask
import kotlinx.validation.KotlinApiCompareTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class AndroidApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("org.jetbrains.kotlinx.binary-compatibility-validator")
        val apiFileName = "${project.name}.api"
        val apiBuild = tasks.register<KotlinApiBuildTask>("apiBuild") {
            inputClassesDirs.from(
                tasks.named<KotlinCompile>("compileReleaseKotlin").flatMap { it.destinationDirectory }
            )
            runtimeClasspath.from(configurations.named("bcv-rt-jvm-cp-resolver"))
            outputApiFile.set(layout.buildDirectory.file("api/$apiFileName"))
            doLast {
                val file = outputApiFile.get().asFile
                file.writeText(
                    file.readText().replace(
                        Regex("""public final class \S+/ComposableSingletons\$\S+ \{\n.*?\n}\n\n?""", RegexOption.DOT_MATCHES_ALL),
                        ""
                    )
                )
            }
        }
        tasks.register<Sync>("apiDump") {
            group = "verification"
            description = "Records the public API in api/$apiFileName."
            from(apiBuild.flatMap { it.outputApiFile })
            into(layout.projectDirectory.dir("api"))
        }
        val apiCheck = tasks.register<KotlinApiCompareTask>("apiCheck") {
            group = "verification"
            description = "Fails if the public API differs from api/$apiFileName."
            projectApiFile.set(layout.projectDirectory.file("api/$apiFileName"))
            generatedApiFile.set(apiBuild.flatMap { it.outputApiFile })
            mustRunAfter("apiDump")
        }
        tasks.named("check") { dependsOn(apiCheck) }
        dependencies {
            add("bcv-rt-jvm-cp", libs.lib("kotlin-metadata-jvm"))
        }
    }
}
