// SPDX-FileCopyrightText: 2026 The LineageOS Project
// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import android.graphics.BlurMaskFilter
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Shader
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireView
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import kotlin.math.min

/**
 * Draws a rim of light round this element: a fine white edge, brightest where the light
 * catches it (the top left while the phone is level), clear across the middle and with a
 * glint opposite. With [followTilt] the light turns as the phone tilts, and settles back once
 * it is held still.
 *
 * Drawn over the element's content. The tilt reads the gravity sensor only while a rim that
 * follows it is shown, and stays still when animations are turned off.
 *
 * @param cornerRadius The element's corner radius, [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param width Width of the rim
 * @param softness How much the rim is blurred, 0 for a crisp line
 * @param followTilt Whether the light turns as the phone tilts
 * @param alpha How strong the rim is, 0 to 1; at 0 nothing is drawn and the tilt sensor is left
 * alone
 */
public fun Modifier.pvotGlassRim(
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    width: Dp = PvotGlassDefaults.RimWidth,
    softness: Dp = PvotGlassDefaults.RimSoftness,
    followTilt: Boolean = true,
    alpha: Float = 1f,
): Modifier = this then GlassRimElement(cornerRadius, width, softness, followTilt, alpha)

private data class GlassRimElement(
    val cornerRadius: Dp,
    val width: Dp,
    val softness: Dp,
    val followTilt: Boolean,
    val alpha: Float,
) : ModifierNodeElement<GlassRimNode>() {

    override fun create(): GlassRimNode = GlassRimNode(cornerRadius, width, softness, followTilt, alpha)

    override fun update(node: GlassRimNode) {
        node.update(cornerRadius, width, softness, followTilt, alpha)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pvotGlassRim"
        properties["cornerRadius"] = cornerRadius
        properties["width"] = width
        properties["softness"] = softness
        properties["followTilt"] = followTilt
        properties["alpha"] = alpha
    }
}

private class GlassRimNode(
    var cornerRadius: Dp,
    var width: Dp,
    var softness: Dp,
    var followTilt: Boolean,
    var alpha: Float,
) : Modifier.Node(), DrawModifierNode {

    private val onTilt = Runnable { invalidateDraw() }
    private val shader = LinearGradient(
        0f,
        0f,
        1f,
        0f,
        intArrayOf(LIT_COLOR, CLEAR_COLOR, GLINT_COLOR),
        floatArrayOf(0f, 0.45f, 1f),
        Shader.TileMode.CLAMP,
    )
    private val matrix = Matrix()
    private val rect = RectF()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        shader = this@GlassRimNode.shader
    }
    private val evenPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = EVEN_COLOR
    }
    private var paintKey: Any? = null

    private var tilting = false

    private fun syncTilt() {
        val wanted = isAttached && followTilt && alpha > 0f
        if (wanted == tilting) return
        tilting = wanted
        if (wanted) {
            TiltLight.addListener(requireView().context, onTilt)
        } else {
            TiltLight.removeListener(onTilt)
        }
    }

    override fun onAttach() {
        syncTilt()
    }

    override fun onDetach() {
        if (tilting) TiltLight.removeListener(onTilt)
        tilting = false
    }

    fun update(cornerRadius: Dp, width: Dp, softness: Dp, followTilt: Boolean, alpha: Float) {
        this.cornerRadius = cornerRadius
        this.width = width
        this.softness = softness
        this.followTilt = followTilt
        this.alpha = alpha
        syncTilt()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        val stroke = width.toPx()
        if (alpha <= 0f || stroke <= 0f || size.width <= 0f || size.height <= 0f) return
        val blur = softness.toPx()
        val key = stroke to blur
        if (key != paintKey) {
            paintKey = key
            val filter = if (blur > 0f) BlurMaskFilter(blur, BlurMaskFilter.Blur.NORMAL) else null
            paint.strokeWidth = stroke
            paint.maskFilter = filter
            evenPaint.strokeWidth = stroke
            evenPaint.maskFilter = filter
        }
        val inset = stroke / 2f
        rect.set(inset, inset, size.width - inset, size.height - inset)
        val radius = min(cornerRadius.toPx(), size.minDimension / 2f) - inset
        val lit = TiltLight.layRimGradient(
            matrix,
            rect.left,
            rect.top,
            rect.width(),
            rect.height(),
            followTilt,
        )
        shader.setLocalMatrix(matrix)
        drawIntoCanvas { canvas ->
            val native = canvas.nativeCanvas
            if (lit < 1f) {
                evenPaint.alpha = ((EVEN_COLOR ushr 24) * (1f - lit) * alpha).toInt()
                native.drawRoundRect(rect, radius, radius, evenPaint)
            }
            paint.alpha = (lit * 255 * alpha).toInt()
            native.drawRoundRect(rect, radius, radius, paint)
        }
    }

    private companion object {
        const val LIT_COLOR = 0xB3FFFFFF.toInt()
        const val CLEAR_COLOR = 0x00FFFFFF
        const val GLINT_COLOR = 0x4DFFFFFF
        const val EVEN_COLOR = 0x40FFFFFF
    }
}
