// SPDX-FileCopyrightText: 2026 The LineageOS Project
// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.effects

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.GlobalPositionAwareModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.TraversableNode
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.node.requireGraphicsContext
import androidx.compose.ui.platform.InspectorInfo

/**
 * What shows through [pvotGlass]: the content of the element marked with
 * [pvotGlassBackdrop], copied as it draws.
 *
 * Create one with [rememberPvotGlassBackdrop], mark the content behind the glass with
 * [pvotGlassBackdrop], and pass it to each glass drawn over that content.
 */
@Stable
public class PvotGlassBackdrop internal constructor() {
    internal var layer: GraphicsLayer? = null
    internal var positionInRoot: Offset = Offset.Unspecified
    internal val glasses: MutableSet<DrawModifierNode> = mutableSetOf()

    internal fun invalidateGlasses() {
        glasses.forEach { it.invalidateDraw() }
    }
}

/** Remembers a [PvotGlassBackdrop] for this composition. */
@Composable
public fun rememberPvotGlassBackdrop(): PvotGlassBackdrop = remember { PvotGlassBackdrop() }

/**
 * Marks this element's content as [backdrop], what glass drawn over it shows through it.
 *
 * The glass must be drawn over this element, not inside it: glass inside its own backdrop
 * shows only its fill.
 *
 * @param backdrop The backdrop this content fills, passed to [pvotGlass]
 */
public fun Modifier.pvotGlassBackdrop(backdrop: PvotGlassBackdrop): Modifier =
    this then GlassBackdropElement(backdrop)

private data class GlassBackdropElement(
    val backdrop: PvotGlassBackdrop,
) : ModifierNodeElement<GlassBackdropNode>() {

    override fun create(): GlassBackdropNode = GlassBackdropNode(backdrop)

    override fun update(node: GlassBackdropNode) {
        node.update(backdrop)
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "pvotGlassBackdrop"
        properties["backdrop"] = backdrop
    }
}

internal class GlassBackdropNode(
    var backdrop: PvotGlassBackdrop,
) : Modifier.Node(), DrawModifierNode, GlobalPositionAwareModifierNode, TraversableNode {

    override val traverseKey: Any get() = TraverseKey

    override fun onAttach() {
        backdrop.layer = requireGraphicsContext().createGraphicsLayer()
    }

    override fun onDetach() {
        backdrop.layer?.let { requireGraphicsContext().releaseGraphicsLayer(it) }
        backdrop.layer = null
        backdrop.positionInRoot = Offset.Unspecified
    }

    fun update(backdrop: PvotGlassBackdrop) {
        if (backdrop === this.backdrop) return
        onDetach()
        this.backdrop = backdrop
        onAttach()
        invalidateDraw()
    }

    override fun ContentDrawScope.draw() {
        val layer = backdrop.layer
        if (layer == null) {
            drawContent()
            return
        }
        layer.record { this@draw.drawContent() }
        drawLayer(layer)
        backdrop.invalidateGlasses()
    }

    override fun onGloballyPositioned(coordinates: LayoutCoordinates) {
        val position = coordinates.positionInRoot()
        if (position != backdrop.positionInRoot) {
            backdrop.positionInRoot = position
            backdrop.invalidateGlasses()
        }
    }

    companion object TraverseKey
}
