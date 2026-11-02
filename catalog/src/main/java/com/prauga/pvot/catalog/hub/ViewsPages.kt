// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.theme.PvotShapes
import com.prauga.pvot.designsystem.theme.PvotTheme
import com.prauga.pvot.designsystem.views.R as ViewsR

@Composable
internal fun ViewsPage(onBack: () -> Unit) {
    val dark = isSystemInDarkTheme()
    val dynamic = PvotTheme.isDynamicColor
    DetailPage(
        title = "XML theme",
        summary =
            "design-system-views brings the same palette, shapes, type and motion to View-based " +
                "apps. Parent the app's theme on PvotTheme and every Material Components widget " +
                "follows, with no layout changes.",
        onBack = onBack,
        code =
            "<style name=\"AppTheme\" parent=\"PvotTheme\" />\n\n" +
                "// or pick one explicitly\n" +
                "setTheme(R.style.PvotTheme_Dark)",
    ) {
        DemoSection(
            title = "Material Components under PvotTheme",
            caption =
                "Stock widgets inflated from an XML layout. The theme follows the catalog's dark " +
                    "mode and dynamic color setting.",
        ) {
            key(dark, dynamic) {
                AndroidView(
                    modifier = Modifier.fillMaxWidth().clip(PvotShapes.Card),
                    factory = { context ->
                        val themeRes =
                            if (dark) ViewsR.style.PvotTheme_Dark else ViewsR.style.PvotTheme_Light
                        var themed: Context = ContextThemeWrapper(context, themeRes)
                        if (dynamic) themed = DynamicColors.wrapContextIfAvailable(themed)
                        LayoutInflater.from(themed).inflate(R.layout.views_demo, null).apply {
                            findViewById<View>(R.id.views_demo_alert).setOnClickListener {
                                AlertDialog.Builder(themed).showDemoDialog()
                            }
                            findViewById<View>(R.id.views_demo_material_alert).setOnClickListener {
                                MaterialAlertDialogBuilder(themed).showDemoDialog()
                            }
                        }
                    },
                )
            }
        }
    }
}

private fun AlertDialog.Builder.showDemoDialog() {
    setTitle(R.string.views_demo_dialog_title)
        .setMessage(R.string.views_demo_dialog_message)
        .setPositiveButton(R.string.views_demo_dialog_delete, null)
        .setNegativeButton(R.string.views_demo_cancel, null)
        .setNeutralButton(R.string.views_demo_dialog_archive, null)
        .show()
}
