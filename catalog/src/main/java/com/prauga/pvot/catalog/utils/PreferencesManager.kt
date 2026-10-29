// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.utils

import android.content.Context
import android.content.SharedPreferences
import com.prauga.pvot.designsystem.effects.PvotGlassDefaults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PreferencesManager {
    private const val PREFS_NAME = "pvot_preferences"
    private const val KEY_DYNAMIC_COLOR = "dynamic_color"
    private const val KEY_NEW_UI = "new_ui"
    private const val KEY_GLASS = "glass_intensity"

    private lateinit var prefs: SharedPreferences
    private val _dynamicColorEnabled = MutableStateFlow(false)
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    private val _newUiEnabled = MutableStateFlow(true)
    val newUiEnabled: StateFlow<Boolean> = _newUiEnabled.asStateFlow()

    private val _glassIntensity = MutableStateFlow(1f)
    val glassIntensity: StateFlow<Float> = _glassIntensity.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _dynamicColorEnabled.value = prefs.getBoolean(KEY_DYNAMIC_COLOR, false)
        _newUiEnabled.value = prefs.getBoolean(KEY_NEW_UI, true)
        _glassIntensity.value = if (prefs.contains(KEY_GLASS)) {
            prefs.getFloat(KEY_GLASS, 1f)
        } else {
            PvotGlassDefaults.defaultIntensity()
        }
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_DYNAMIC_COLOR, enabled).apply()
        _dynamicColorEnabled.value = enabled
    }

    fun setNewUiEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_NEW_UI, enabled).apply()
        _newUiEnabled.value = enabled
    }

    fun setGlassIntensity(value: Float) {
        prefs.edit().putFloat(KEY_GLASS, value).apply()
        _glassIntensity.value = value
    }
}
