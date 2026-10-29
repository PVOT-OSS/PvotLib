// SPDX-FileCopyrightText: 2026 The LineageOS Project
// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.node.traverseAncestors
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

/**
 * How [pvotGlass] looks: a clear glass like a drop of water.
 *
 * Every size is for a glass whose shorter side is [referenceSize], and scales with the shorter
 * side of the glass it is drawn on, so the glass looks the same at any size.
 *
 * @param referenceSize The shorter side the other sizes are tuned for
 * @param band How far in from the edge the rim reaches: the bend, reflection and light
 * @param strength How far out the bend reads at the very edge
 * @param aberration How far apart red and blue split at the edge
 * @param edgeBlur How much the rim is softened, strongest at the edge
 * @param blur How much what shows through is frosted, middle and rim alike
 * @param reflection How strongly the rim mirrors what lies just outside it, 0 to 1
 * @param mirrorScale How far out the mirror reaches, for its depth into the rim
 * @param glow How bright the rim's light is at the edge facing the light, 0 to 1
 * @param darkGlow How much of that light shows over what is dark, 0 to 1
 * @param vividSpread How much further than the bend vivid colors spread into the rim
 * @param vividMix How strongly that spread shows for a fully saturated color, 0 to 1
 * @param fillColor The glass's tint, drawn over what shows through
 */
@Immutable
public data class PvotGlassStyle(
    val referenceSize: Dp = 46.dp,
    val band: Dp = 12.dp,
    val strength: Dp = 11.dp,
    val aberration: Dp = 3.dp,
    val edgeBlur: Dp = 2.5.dp,
    val blur: Dp = 5.dp,
    val reflection: Float = 0.85f,
    val mirrorScale: Float = 1f,
    val glow: Float = 0.22f,
    val darkGlow: Float = 0.35f,
    val vividSpread: Dp = 11.dp,
    val vividMix: Float = 1f,
    val fillColor: Color = PvotGlassDefaults.FillColor,
)

/** Defaults for [pvotGlass] and [pvotGlassRim]. */
public object PvotGlassDefaults {

    /** A corner radius that rounds the glass into a capsule, or a circle if it is square. */
    public val CapsuleRadius: Dp = Dp.Infinity

    /** The glass's tint: a faint white haze, so what shows through is a little brighter. */
    public val FillColor: Color = Color(0x0AFFFFFF)

    /** The glass's default look. */
    public val Style: PvotGlassStyle = PvotGlassStyle()

    /** Width of the rim of light round the glass. */
    public val RimWidth: Dp = 1.dp

    /** How much the rim of light is blurred, 0 for a crisp line. */
    public val RimSoftness: Dp = 0.dp

    /** The glass's tint while it is pressed: brighter, as if lit from inside. */
    public val PressedFillColor: Color = Color(0x26FFFFFF)

    /** How much a glass control swells while it is pressed. */
    public const val PressedScale: Float = 1.04f

    /** Elevation of the shadow that lifts glass controls off the content behind them. */
    public val ShadowElevation: Dp = 12.dp

    /** Opacity of that shadow, kept soft so the glass floats rather than sits heavy. */
    public const val ShadowAlpha: Float = 0.18f
}

/**
 * Draws this element's background as glass over [backdrop]: what lies behind shows through
 * frosted, and a soft rim along the edge bends it in from just outside, with a slight red and
 * blue split, a mirrored copy of what lies outside the edge, and a soft light in place of a
 * border.
 *
 * The rim needs Android 13, the frost Android 12; before them the glass is its tint alone.
 * Only what is drawn in [backdrop] shows through, not other windows or the wallpaper.
 *
 * @param backdrop The content behind the glass, marked with [pvotGlassBackdrop], or null to
 * draw only the tint and the rim's light
 * @param cornerRadius The glass's corner radius, [PvotGlassDefaults.CapsuleRadius] for a capsule
 * @param style How the glass looks
 */
public fun Modifier.pvotGlass(
    backdrop: PvotGlassBackdrop?,
    cornerRadius: Dp = PvotGlassDefaults.CapsuleRadius,
    style: PvotGlassStyle = PvotGlassDefaults.Style,
): Modifier = this then GlassElement(backdrop, cornerRadius, style)

private data class GlassElement(
    val backdrop: PvotGlassBackdrop?,
    val cornerRadius: Dp,
    val style: PvotGlassStyle,
) : ModifierNodeElement<GlassNode>() {

    override fun create(): GlassNode = GlassNode(backdrop, cornerRadius, style)

    override fun update(node: GlassNode) {
        node.update(backdrop, cornerRadius, style)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pvotGlass"
        properties["backdrop"] = backdrop
        properties["cornerRadius"] = cornerRadius
        properties["style"] = style
    }
}

private class GlassNode(
    var backdrop: PvotGlassBackdrop?,
    var cornerRadius: Dp,
    var style: PvotGlassStyle,
) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode {

    private var frost: GraphicsLayer? = null
    private var lens: GraphicsLayer? = null
    private var clip: GraphicsLayer? = null
    private var lensShader: RuntimeShader? = null
    private var positionInRoot = Offset.Unspecified
    private var insideBackdrop = false
    private var lensKey: Any? = null
    private var margin = 0

    override fun onAttach() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val context = requireGraphicsContext()
            frost = context.createGraphicsLayer()
            clip = context.createGraphicsLayer().apply { clip = true }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                lens = context.createGraphicsLayer()
                lensShader = RuntimeShader(LENS_SHADER)
            }
        }
        attachBackdrop()
    }

    override fun onDetach() {
        detachBackdrop()
        val context = requireGraphicsContext()
        listOfNotNull(frost, lens, clip).forEach(context::releaseGraphicsLayer)
        frost = null
        lens = null
        clip = null
        lensShader = null
        lensKey = null
    }

    fun update(backdrop: PvotGlassBackdrop?, cornerRadius: Dp, style: PvotGlassStyle) {
        if (backdrop !== this.backdrop) {
            detachBackdrop()
            this.backdrop = backdrop
            attachBackdrop()
        }
        this.cornerRadius = cornerRadius
        this.style = style
        lensKey = null
        invalidateDraw()
    }

    private fun attachBackdrop() {
        val backdrop = backdrop ?: return
        backdrop.glasses += this
        insideBackdrop = false
        traverseAncestors(GlassBackdropNode.TraverseKey) { node ->
            if ((node as GlassBackdropNode).backdrop === backdrop) insideBackdrop = true
            !insideBackdrop
        }
    }

    private fun detachBackdrop() {
        backdrop?.glasses?.remove(this)
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val position = coordinates.positionInRoot()
        if (position != positionInRoot) {
            positionInRoot = position
            invalidateDraw()
        }
    }

    override fun ContentDrawScope.draw() {
        if (size.width > 0f && size.height > 0f) {
            val radius = min(cornerRadius.toPx(), size.minDimension / 2f)
            drawBackdrop(radius)
            drawRoundRect(style.fillColor, cornerRadius = CornerRadius(radius))
        }
        drawContent()
    }

    private fun ContentDrawScope.drawBackdrop(radius: Float) {
        val frost = frost ?: return
        val clip = clip ?: return
        val lens = lens
        val shader = lensShader
        val source = backdrop?.layer
        val sourcePosition = backdrop?.positionInRoot ?: Offset.Unspecified
        val hasSource = source != null && !insideBackdrop &&
            sourcePosition.isSpecified && positionInRoot.isSpecified
        if (!hasSource && (lens == null || shader == null)) return

        val width = size.width.toInt()
        val height = size.height.toInt()
        updateLens(width, height, radius)
        val copySize = IntSize(width + margin * 2, height + margin * 2)

        frost.record(copySize) {
            if (hasSource) {
                translate(
                    margin - (positionInRoot.x - sourcePosition.x),
                    margin - (positionInRoot.y - sourcePosition.y),
                ) {
                    drawLayer(source!!)
                }
            }
        }
        val top = if (lens != null && shader != null) {
            lens.record(copySize) { drawLayer(frost) }
            lens
        } else {
            frost
        }
        clip.setRoundRectOutline(Offset.Zero, size, radius)
        clip.record(IntSize(width, height)) {
            translate(-margin.toFloat(), -margin.toFloat()) { drawLayer(top) }
        }
        drawLayer(clip)
    }

    private fun ContentDrawScope.updateLens(width: Int, height: Int, radius: Float) {
        val key = Triple(width, height, radius) to density
        if (key == lensKey) return
        lensKey = key
        val scale = min(width, height) / style.referenceSize.toPx()
        val band = style.band.toPx() * scale
        val strength = style.strength.toPx() * scale
        val aberration = style.aberration.toPx() * scale
        val edgeBlur = style.edgeBlur.toPx() * scale
        val vividSpread = style.vividSpread.toPx() * scale
        val blur = style.blur.toPx() * scale
        margin = ceil(
            max(strength + aberration + edgeBlur * 2 + vividSpread, band * style.mirrorScale) +
                blur * 2
        ).toInt() + 2
        frost?.renderEffect = if (blur > 0f) BlurEffect(blur, blur, TileMode.Clamp) else null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setLensUniforms(radius, width, height, band, strength, aberration, edgeBlur, vividSpread)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setLensUniforms(
        radius: Float,
        width: Int,
        height: Int,
        band: Float,
        strength: Float,
        aberration: Float,
        edgeBlur: Float,
        vividSpread: Float,
    ) {
        val shader = lensShader ?: return
        shader.setFloatUniform("origin", margin.toFloat(), margin.toFloat())
        shader.setFloatUniform("size", width.toFloat(), height.toFloat())
        shader.setFloatUniform("radius", radius)
        shader.setFloatUniform("band", band)
        shader.setFloatUniform("strength", strength)
        shader.setFloatUniform("aberration", aberration)
        shader.setFloatUniform("edgeBlur", edgeBlur)
        shader.setFloatUniform("vividSpread", vividSpread)
        shader.setFloatUniform("reflection", style.reflection)
        shader.setFloatUniform("mirrorScale", style.mirrorScale)
        shader.setFloatUniform("glow", style.glow)
        shader.setFloatUniform("darkGlow", style.darkGlow)
        shader.setFloatUniform("vividMix", style.vividMix)
        lens?.renderEffect =
            android.graphics.RenderEffect.createRuntimeShaderEffect(shader, "content")
                .asComposeRenderEffect()
    }
}

private const val LENS_SHADER = """
uniform shader content;
uniform float2 origin;
uniform float2 size;
uniform float radius;
uniform float band;
uniform float strength;
uniform float aberration;
uniform float edgeBlur;
uniform float reflection;
uniform float mirrorScale;
uniform float glow;
uniform float darkGlow;
uniform float vividSpread;
uniform float vividMix;

// Distance from p to the edge of a rounded rect of half-size b, corner r, centred
// at 0: negative inside.
float roundRect(float2 p, float2 b, float r) {
    float2 q = abs(p) - b + r;
    return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

half4 split(float2 at, float2 apart) {
    half4 c = content.eval(at);
    half4 red = content.eval(at + apart);
    half4 blue = content.eval(at - apart);
    return half4(red.r, c.g, blue.b, max(c.a, max(red.a, blue.a)));
}

// How vivid a colour is: its saturation, by how opaque it is. 0 for grey or black, and
// fading out for very dark colours, whose saturation is only noise.
float vividness(half4 c) {
    float hi = max(c.r, max(c.g, c.b));
    float lo = min(c.r, min(c.g, c.b));
    return clamp((hi - lo) / max(hi, 0.25) * c.a, 0.0, 1.0);
}

half4 main(float2 coord) {
    float2 halfSize = size * 0.5;
    float2 p = coord - origin - halfSize;
    if (roundRect(p, halfSize, radius) >= 0.0) {
        return content.eval(coord);
    }
    // The band is laid out against the same shape with its corners rounded at least
    // as much as the band is deep: against the glass's own corners, a band deeper
    // than they are round would meet itself at each inner corner in a sharp diagonal
    // (the nearest edge changing there from a side to the top or bottom). So its
    // lines, and the bend and the light that follow them, curve round the corners.
    // Between the glass's own corner and that rounder one, the band is at its edge.
    float bandRadius = max(radius, band);
    float d = min(roundRect(p, halfSize, bandRadius), 0.0);
    // 1 at the edge, 0 from band in.
    float t = clamp(1.0 + d / band, 0.0, 1.0);
    if (t <= 0.0) {
        return content.eval(coord);
    }
    // The outward normal: the distance's gradient.
    float2 n = float2(
        roundRect(p + float2(1.0, 0.0), halfSize, bandRadius)
            - roundRect(p - float2(1.0, 0.0), halfSize, bandRadius),
        roundRect(p + float2(0.0, 1.0), halfSize, bandRadius)
            - roundRect(p - float2(0.0, 1.0), halfSize, bandRadius));
    n = n / max(length(n), 0.0001);
    float2 tangent = float2(-n.y, n.x);
    float ease = t * t;
    // The same, eased in and out (a smoothstep): what grows across the band does so
    // with no step, so the rim fades into the middle with no edge of its own.
    float smooth = t * t * (3.0 - 2.0 * t);
    float2 apart = n * (aberration * ease);

    // The bend.
    float2 at = coord + n * (strength * ease);
    float soft = edgeBlur * smooth;
    half4 bent = split(at, apart) * 0.4
        + split(at + tangent * soft, apart) * 0.15
        + split(at - tangent * soft, apart) * 0.15
        + split(at + n * soft, apart) * 0.15
        + split(at - n * soft, apart) * 0.15;

    // The reflection: as far outside the edge as this point is inside it.
    float inside = -d;
    half4 mirrored = split(coord + n * (inside * (1.0 + mirrorScale)), apart);
    half4 c = mix(bent, mirrored, half(reflection * smooth));

    // Vivid colours spread further in: a wider ring round the bend, mixed in only
    // as much as it is saturated.
    float reach = vividSpread * smooth;
    float diag = reach * 0.7071;
    half4 around = (split(at + tangent * reach, apart)
        + split(at - tangent * reach, apart)
        + split(at + n * reach, apart)
        + split(at - n * reach, apart)
        + split(at + (n + tangent) * diag, apart)
        + split(at + (n - tangent) * diag, apart)
        + split(at - (n + tangent) * diag, apart)
        + split(at - (n - tangent) * diag, apart)) * 0.125;
    c = mix(c, around, half(clamp(vividMix * smooth * vividness(around), 0.0, 1.0)));

    // The rim's light, no line: brighter towards the edge and on the side facing the
    // light, laid over whatever shows there (also over the bare wallpaper).
    float facing = clamp(dot(n, float2(-0.7071, -0.7071)) * 0.5 + 0.5, 0.0, 1.0);
    // Over what is dark or bare only darkGlow of it, rising to all of it over what is
    // bright, where it shows no more than a haze would.
    float seen = max(c.r, max(c.g, c.b));
    float lift = mix(darkGlow, 1.0, smoothstep(0.0, 0.5, seen));
    half light = half(glow * lift * pow(t, 2.5) * (0.45 + 0.55 * facing));
    return half4(light) + c * (1.0 - light);
}
"""
