// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.PowerManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Whether the device asks for less glass than the app would draw, or null to ask the device.
 *
 * Provided by tests to force one answer; apps leave it alone.
 */
public val LocalPvotGlassReduced: ProvidableCompositionLocal<Boolean?> = staticCompositionLocalOf {
    null
}

/**
 * Holds an app's glass preference to what the device can and should draw.
 *
 * The pattern for an app: persist a `Float` preference, offer the user the three stops
 * [PvotGlassDefaults.SolidIntensity], [PvotGlassDefaults.FrostedIntensity] and
 * [PvotGlassDefaults.defaultIntensity] (Solid, Frosted, Glass), or a slider up to
 * [PvotGlassDefaults.maxIntensity], and wrap the root in [ProvidePvotGlassIntensity].
 */
public object PvotGlassIntensity {

    /**
     * Whether the device asks for less effect right now: battery saver is on, or the device cannot
     * blur. Observed live, so the glass follows the battery saver switch.
     */
    @Composable
    public fun isReduced(): Boolean {
        LocalPvotGlassReduced.current?.let {
            return it
        }
        if (!PvotGlassDefaults.isBlurSupported) return true
        val context = LocalContext.current
        val powerManager = remember(context) { context.getSystemService(PowerManager::class.java) }
        var saving by remember { mutableStateOf(powerManager?.isPowerSaveMode == true) }
        DisposableEffect(context, powerManager) {
            if (powerManager == null) return@DisposableEffect onDispose {}
            val receiver =
                object : BroadcastReceiver() {
                    override fun onReceive(context: Context, intent: Intent) {
                        saving = powerManager.isPowerSaveMode
                    }
                }
            ContextCompat.registerReceiver(
                context,
                receiver,
                IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED,
            )
            saving = powerManager.isPowerSaveMode
            onDispose { context.unregisterReceiver(receiver) }
        }
        return saving
    }

    /**
     * [preferred] held to the device: [PvotGlassDefaults.SolidIntensity] while [isReduced], else at
     * most [PvotGlassDefaults.maxIntensity].
     *
     * @param preferred The app's preference, 0 to 1
     */
    @Composable
    public fun effective(preferred: Float): Float =
        if (isReduced()) PvotGlassDefaults.SolidIntensity
        else preferred.coerceIn(0f, PvotGlassDefaults.maxIntensity())
}

/**
 * Provides [LocalPvotGlassIntensity] to [content] as [PvotGlassIntensity.effective] of [preferred]:
 * the app's preference, held to what the device can draw and set to solid while battery saver is
 * on.
 *
 * @param preferred The app's glass preference, 0 to 1
 * @param content The tree whose glass reads the intensity
 */
@Composable
public fun ProvidePvotGlassIntensity(preferred: Float, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalPvotGlassIntensity provides PvotGlassIntensity.effective(preferred),
        content = content,
    )
}
