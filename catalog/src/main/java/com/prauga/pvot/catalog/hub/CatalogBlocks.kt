// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import com.prauga.pvot.catalog.components.GlassPanel
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.slider.PvotSlider
import com.prauga.pvot.designsystem.theme.PvotSpacing
import kotlinx.coroutines.launch

/** Where pages show their "copied" and other brief messages. */
internal val LocalCatalogSnackbar = staticCompositionLocalOf { SnackbarHostState() }

/** The vertical rhythm of every page: sections sit [SectionGap] apart, items inside [ItemGap]. */
internal val SectionGap = 28.dp
internal val ItemGap = PvotSpacing.M

/** Copies [text] to the clipboard, ticks the haptics and says [message] in a snackbar. */
@Composable
internal fun rememberCopier(): (text: String, message: String) -> Unit {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val snackbar = LocalCatalogSnackbar.current
    val scope = rememberCoroutineScope()
    return { text, message ->
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("pvot", text))
        haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
}

/** A snackbar message without copying anything. */
@Composable
internal fun rememberSnackbarSender(): (String) -> Unit {
    val snackbar = LocalCatalogSnackbar.current
    val scope = rememberCoroutineScope()
    return { message ->
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }
}

/**
 * The frame of every detail page: a back button and title, a summary, the page's sections and an
 * optional code sample. The page scrolls inside [PvotScreen], so it clears the nav bar and drives
 * its compact mode.
 */
@Composable
internal fun DetailPage(
    title: String,
    summary: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    code: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    PvotScreen(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(SectionGap)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(PvotSpacing.S)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(R.drawable.ic_t_arrow_left),
                            contentDescription = "Back"
                        )
                    }
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = PvotSpacing.XS)
                    )
                }
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = PvotSpacing.S)
                )
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(SectionGap),
                content = content
            )
        }
        if (code != null) {
            item {
                DemoSection(title = "Usage") { CodeBlock(code) }
            }
        }
    }
}

/**
 * A titled group on a page. With [framed] the content sits in a card, centered, which is what a
 * live demo wants; without it the content lays out in the page's width.
 */
@Composable
internal fun DemoSection(
    title: String,
    modifier: Modifier = Modifier,
    caption: String? = null,
    framed: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(ItemGap)) {
        Column(
            modifier = Modifier.padding(horizontal = PvotSpacing.S),
            verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (caption != null) {
                Text(
                    text = caption,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (framed) {
            GlassPanel(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(PvotSpacing.L),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(ItemGap),
                    content = content
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(ItemGap), content = content)
        }
    }
}

/** A code sample the user can select or copy in one tap. */
@Composable
internal fun CodeBlock(code: String, modifier: Modifier = Modifier) {
    val copy = rememberCopier()
    GlassPanel(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = PvotSpacing.L, top = PvotSpacing.S, bottom = PvotSpacing.S),
            verticalAlignment = Alignment.Top
        ) {
            SelectionContainer(modifier = Modifier.weight(1f)) {
                Text(
                    text = code,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(top = PvotSpacing.S)
                        .horizontalScroll(rememberScrollState())
                )
            }
            IconButton(onClick = { copy(code, "Code copied") }) {
                Icon(
                    painter = painterResource(R.drawable.ic_t_copy),
                    contentDescription = "Copy code",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/** A labelled switch row that toggles the whole row. */
@Composable
internal fun OptionSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

/** A labelled slider with its current value on the right. */
@Composable
internal fun OptionSlider(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        PvotSlider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}

/** A row of mutually exclusive choices, scrolling sideways when they don't fit. */
@Composable
internal fun <T> ChoiceChips(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    label: (T) -> String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(PvotSpacing.S)
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selected,
                onClick = { onSelect(option) },
                label = { Text(label(option)) },
                shape = CircleShape
            )
        }
    }
}

/** A tinted circle holding an icon, the catalog's leading visual. */
@Composable
internal fun IconBadge(@DrawableRes icon: Int, modifier: Modifier = Modifier, size: Int = 44) {
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size(size.dp)
            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size((size / 2).dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}
