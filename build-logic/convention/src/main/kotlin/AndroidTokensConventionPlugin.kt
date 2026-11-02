// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

class AndroidTokensConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val designSystem =
                rootProject.layout.projectDirectory.dir(
                    "design-system/src/main/java/com/prauga/pvot/designsystem"
                )
            val themeDir = designSystem.dir("theme")
            val motionDir = designSystem.dir("motion")
            val tokensFile = layout.projectDirectory.file("src/main/res/values/pvot_tokens.xml")
            val generate = { PvotTokens.generate(themeDir.asFile, motionDir.asFile) }
            tasks.register("tokensDump") {
                group = "verification"
                description =
                    "Writes the design-system tokens to src/main/res/values/pvot_tokens.xml."
                inputs.dir(themeDir)
                inputs.dir(motionDir)
                outputs.file(tokensFile)
                doLast { tokensFile.asFile.writeText(generate()) }
            }
            val tokensCheck =
                tasks.register("tokensCheck") {
                    group = "verification"
                    description =
                        "Fails if src/main/res/values/pvot_tokens.xml differs from the design-system tokens."
                    inputs.dir(themeDir)
                    inputs.dir(motionDir)
                    inputs.file(tokensFile)
                    mustRunAfter("tokensDump")
                    doLast {
                        val recorded = tokensFile.asFile.takeIf { it.exists() }?.readText()
                        if (recorded != generate()) {
                            throw GradleException(
                                "pvot_tokens.xml is out of date with design-system. Run :${project.name}:tokensDump and commit the result."
                            )
                        }
                    }
                }
            tasks.named("check") { dependsOn(tokensCheck) }
        }
    }
}
