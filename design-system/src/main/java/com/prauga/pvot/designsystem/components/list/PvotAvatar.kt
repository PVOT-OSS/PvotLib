// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.designsystem.components.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Defaults for [PvotAvatar]. */
public object PvotAvatarDefaults {

    /** A list row's leading avatar. */
    public val Small: Dp = PvotListRowDefaults.LeadingSize

    /** A search result or a sheet's header. */
    public val Medium: Dp = 56.dp

    /** A favorites tile at its smallest. */
    public val Large: Dp = 72.dp

    /** A favorites tile. */
    public val ExtraLarge: Dp = 96.dp

    /** The caller on the call screen and the contact on its details page. */
    public val Hero: Dp = 120.dp

    /** Width of the [ring][PvotAvatar] round the avatar. */
    public val RingWidth: Dp = 3.dp

    /** Gap between the avatar and its ring, so the ring reads apart from the photo. */
    public val RingGap: Dp = 3.dp
}

/**
 * A person's picture: their photo, or a [PvotLetterTile] with their initial when there is none.
 *
 * The library loads no images; pass the photo as a [painter] from whatever loader the app uses. A
 * [ring] in a state color, such as the call's, goes round the avatar, and [badge] draws at its
 * bottom end, such as a call type icon. The avatar is decorative unless [contentDescription] is
 * set: the row or screen next to it already says the name.
 *
 * @param name The person's name, whose initial shows without a photo
 * @param modifier Modifier for the avatar
 * @param painter The photo, or null for the letter tile
 * @param colorKey What picks the letter tile's color, such as a contact's lookup key
 * @param size Diameter of the avatar, not counting the ring
 * @param ring Color of a ring round the avatar, or null for none
 * @param contentDescription What TalkBack reads, or null for a decorative avatar
 * @param badge Content at the avatar's bottom end, or null for none
 */
@Composable
public fun PvotAvatar(
    name: String,
    modifier: Modifier = Modifier,
    painter: Painter? = null,
    colorKey: Any = name,
    size: Dp = PvotAvatarDefaults.Small,
    ring: Color? = null,
    contentDescription: String? = null,
    badge: (@Composable BoxScope.() -> Unit)? = null,
) {
    val ringPadding =
        if (ring != null) PvotAvatarDefaults.RingWidth + PvotAvatarDefaults.RingGap else 0.dp
    val semantics =
        if (contentDescription != null) {
            Modifier.semantics { this.contentDescription = contentDescription }
        } else {
            Modifier.clearAndSetSemantics {}
        }
    Box(modifier.then(semantics)) {
        Box(
            modifier =
                Modifier.then(
                        if (ring != null) {
                            Modifier.border(PvotAvatarDefaults.RingWidth, ring, CircleShape)
                        } else {
                            Modifier
                        }
                    )
                    .padding(ringPadding),
            contentAlignment = Alignment.Center,
        ) {
            if (painter != null) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(size).clip(CircleShape),
                )
            } else {
                PvotLetterTile(name = name, colorKey = colorKey, size = size)
            }
        }
        if (badge != null) {
            Box(Modifier.align(Alignment.BottomEnd), content = badge)
        }
    }
}
