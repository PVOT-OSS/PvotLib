// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.prauga.pvot.catalog.R
import com.prauga.pvot.coreui.PvotEmptyContent
import com.prauga.pvot.coreui.PvotSectionHeader
import com.prauga.pvot.designsystem.components.PvotCard
import com.prauga.pvot.designsystem.components.PvotScreen
import com.prauga.pvot.designsystem.components.textfield.PvotTextField
import com.prauga.pvot.designsystem.motion.PvotDurations
import com.prauga.pvot.designsystem.motion.PvotMotion
import com.prauga.pvot.designsystem.motion.pvotReveal
import com.prauga.pvot.designsystem.navigation.LocalPvotNavBarSizes
import com.prauga.pvot.designsystem.theme.PvotSpacing

/** One page of the catalog: what it is called, where it is listed and how it draws itself. */
internal class CatalogEntry(
    val id: String,
    val title: String,
    val summary: String,
    val group: CatalogGroup,
    @DrawableRes val icon: Int,
    val keywords: String = "",
    val page: @Composable (onBack: () -> Unit) -> Unit
)

internal enum class CatalogGroup(val title: String) {
    Foundations("Foundations"),
    Components("Pvot components"),
    Material("Material elements")
}

/**
 * The design catalog: a searchable index of every foundation and component, and a page for each.
 * Opening a page slides it in over the index, the index keeps its scroll position, and system back
 * returns to it.
 */
@Composable
internal fun CatalogHub(modifier: Modifier = Modifier) {
    val entries = remember { catalogEntries() }
    var openId by rememberSaveable { mutableStateOf<String?>(null) }
    var query by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val snackbar = remember { SnackbarHostState() }
    val navBarHeight = LocalPvotNavBarSizes.current.barHeight

    BackHandler(enabled = openId != null) { openId = null }

    CompositionLocalProvider(
        LocalCatalogSnackbar provides snackbar,
        LocalContentColor provides MaterialTheme.colorScheme.onBackground
    ) {
        Box(modifier = modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = openId,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = { pageTransition(forward = targetState != null) },
                label = "catalogPage"
            ) { id ->
                val entry = entries.firstOrNull { it.id == id }
                if (entry == null) {
                    CatalogIndex(
                        entries = entries,
                        query = query,
                        onQueryChange = { query = it },
                        listState = listState,
                        onOpen = { openId = it.id }
                    )
                } else {
                    entry.page { openId = null }
                }
            }
            SnackbarHost(
                hostState = snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = navBarHeight + PvotSpacing.M * 2 + PvotSpacing.S)
                    .padding(horizontal = PvotSpacing.L)
            )
        }
    }
}

/** Forward pushes the page in from the right while the index drifts left; back reverses it. */
private fun pageTransition(forward: Boolean): ContentTransform {
    val slide = tween<IntOffset>(PvotDurations.Long, easing = PvotMotion.Settle)
    val fadeIn = fadeIn(tween(PvotDurations.Long, easing = PvotMotion.Settle))
    val fadeOut = fadeOut(tween(PvotDurations.Short))
    return if (forward) {
        (slideInHorizontally(slide) { it / 4 } + fadeIn) togetherWith
            (slideOutHorizontally(slide) { -it / 8 } + fadeOut)
    } else {
        (slideInHorizontally(slide) { -it / 8 } + fadeIn) togetherWith
            (slideOutHorizontally(slide) { it / 4 } + fadeOut)
    }.apply { targetContentZIndex = if (forward) 1f else -1f }
}

@Composable
private fun CatalogIndex(
    entries: List<CatalogEntry>,
    query: String,
    onQueryChange: (String) -> Unit,
    listState: LazyListState,
    onOpen: (CatalogEntry) -> Unit
) {
    val needle = query.trim()
    val visible = if (needle.isEmpty()) entries else entries.filter {
        it.title.contains(needle, ignoreCase = true) ||
            it.summary.contains(needle, ignoreCase = true) ||
            it.keywords.contains(needle, ignoreCase = true)
    }

    PvotScreen(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = listState,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(ItemGap)
    ) {
        item(key = "title") {
            Column(
                modifier = Modifier.padding(bottom = PvotSpacing.XS),
                verticalArrangement = Arrangement.spacedBy(PvotSpacing.XS)
            ) {
                Text(
                    text = "Design Catalog",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${entries.size} pages across tokens, Pvot components and the Material " +
                        "elements the theme styles.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        item(key = "search") { SearchField(query, onQueryChange) }

        if (visible.isEmpty()) {
            item(key = "empty") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = SectionGap),
                    contentAlignment = Alignment.Center
                ) {
                    PvotEmptyContent(message = "Nothing matches \"$needle\"")
                }
            }
        }

        var revealIndex = 0
        CatalogGroup.entries.forEach { group ->
            val inGroup = visible.filter { it.group == group }
            if (inGroup.isEmpty()) return@forEach
            item(key = "header-${group.name}") {
                PvotSectionHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = PvotSpacing.M)
                        .animateItem(),
                    title = group.title
                )
            }
            inGroup.forEach { entry ->
                val index = revealIndex++
                item(key = entry.id) {
                    EntryRow(
                        entry = entry,
                        onClick = { onOpen(entry) },
                        modifier = Modifier
                            .animateItem()
                            .pvotReveal(index)
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit) {
    val focus = LocalFocusManager.current
    PvotTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = CircleShape,
        placeholder = "Search tokens and components",
        leadingIcon = {
            Icon(painter = painterResource(R.drawable.ic_t_search), contentDescription = null)
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_t_x),
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focus.clearFocus() })
    )
}

@Composable
private fun EntryRow(entry: CatalogEntry, onClick: () -> Unit, modifier: Modifier = Modifier) {
    PvotCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(PvotSpacing.L),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(PvotSpacing.L)
        ) {
            IconBadge(icon = entry.icon)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = entry.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_t_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
