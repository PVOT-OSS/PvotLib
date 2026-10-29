// SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
// SPDX-License-Identifier: Apache-2.0

package com.prauga.pvot.catalog.hub

import com.prauga.pvot.catalog.R

/** Every page of the catalog, in the order the index lists them. */
internal fun catalogEntries(): List<CatalogEntry> = listOf(
    // Foundations
    CatalogEntry(
        id = "color", title = "Color", group = CatalogGroup.Foundations,
        summary = "The palette in light and dark; tap a swatch to copy its hex.",
        icon = R.drawable.ic_t_palette, keywords = "palette theme scheme hex primary surface",
        page = { ColorsPage(it) }
    ),
    CatalogEntry(
        id = "typography", title = "Typography", group = CatalogGroup.Foundations,
        summary = "The type scale, with live preview of your own text.",
        icon = R.drawable.ic_t_typography, keywords = "font text style scale",
        page = { TypographyPage(it) }
    ),
    CatalogEntry(
        id = "shape-spacing", title = "Shape & spacing", group = CatalogGroup.Foundations,
        summary = "Corner and spacing tokens, plus a radius playground.",
        icon = R.drawable.ic_t_shape, keywords = "radius corner padding gap tokens",
        page = { ShapeSpacingPage(it) }
    ),
    CatalogEntry(
        id = "motion", title = "Motion", group = CatalogGroup.Foundations,
        summary = "Durations, easing, springs, press and reveal, all replayable.",
        icon = R.drawable.ic_t_wave_sine, keywords = "animation easing spring duration transition reveal press",
        page = { MotionPage(it) }
    ),
    CatalogEntry(
        id = "icons", title = "Icons", group = CatalogGroup.Foundations,
        summary = "The Tabler icon set the catalog uses, resizable.",
        icon = R.drawable.ic_t_icons, keywords = "tabler glyph symbol",
        page = { IconsPage(it) }
    ),
    // Pvot components
    CatalogEntry(
        id = "nav-bar", title = "Navigation bar", group = CatalogGroup.Components,
        summary = "Floating bar with a pill, drag-to-select and a compact mode.",
        icon = R.drawable.ic_t_layout_navbar, keywords = "bottom tabs navigation compact drag PvotNavBar",
        page = { NavBarPage(it) }
    ),
    CatalogEntry(
        id = "card", title = "Card", group = CatalogGroup.Components,
        summary = "Static, clickable, expandable, outlined and disabled.",
        icon = R.drawable.ic_t_cards, keywords = "surface container PvotCard",
        page = { CardsPage(it) }
    ),
    CatalogEntry(
        id = "skeleton", title = "Skeleton", group = CatalogGroup.Components,
        summary = "Shimmer placeholders and the handoff to real content.",
        icon = R.drawable.ic_t_rectangle, keywords = "loading placeholder shimmer PvotSkeleton",
        page = { SkeletonPage(it) }
    ),
    CatalogEntry(
        id = "pickers", title = "Pickers", group = CatalogGroup.Components,
        summary = "Wheel pickers for a time of day and a duration.",
        icon = R.drawable.ic_t_player_play, keywords = "time clock duration wheel PvotClockPicker PvotDurationPicker",
        page = { PickersPage(it) }
    ),
    CatalogEntry(
        id = "text-fields", title = "Text fields", group = CatalogGroup.Components,
        summary = "Floating label, focus ring, validation shake, counter and more.",
        icon = R.drawable.ic_t_forms, keywords = "input form textfield password email PvotTextField search",
        page = { InputsPage(it) }
    ),
    CatalogEntry(
        id = "slider", title = "Slider", group = CatalogGroup.Components,
        summary = "Pvot's minimal slider: continuous, stepped and range.",
        icon = R.drawable.ic_t_adjustments, keywords = "slider range steps scrub PvotSlider PvotRangeSlider",
        page = { SliderPage(it) }
    ),
    CatalogEntry(
        id = "overlays", title = "Dialogs & sheets", group = CatalogGroup.Components,
        summary = "Animated dialogs and bottom sheets with actions and controls.",
        icon = R.drawable.ic_t_message_circle, keywords = "dialog sheet modal snackbar toast alert PvotDialog PvotBottomSheet",
        page = { OverlaysPage(it) }
    ),
    CatalogEntry(
        id = "states", title = "State content", group = CatalogGroup.Components,
        summary = "Loading, empty and error content, and section headers.",
        icon = R.drawable.ic_t_loader_2, keywords = "uistate loading empty error retry header coreui",
        page = { StatesPage(it) }
    ),
    // Material elements
    CatalogEntry(
        id = "buttons", title = "Buttons", group = CatalogGroup.Material,
        summary = "Filled to text, icon buttons, FABs and a loading state.",
        icon = R.drawable.ic_t_click, keywords = "button fab icon action",
        page = { ButtonsPage(it) }
    ),
    CatalogEntry(
        id = "selection", title = "Selection controls", group = CatalogGroup.Material,
        summary = "Switches, checkbox and radio buttons.",
        icon = R.drawable.ic_t_toggle_left, keywords = "switch checkbox radio",
        page = { SelectionPage(it) }
    ),
    CatalogEntry(
        id = "chips", title = "Chips", group = CatalogGroup.Material,
        summary = "Filter, input, assist and suggestion chips.",
        icon = R.drawable.ic_t_cards, keywords = "tag filter chip",
        page = { ChipsPage(it) }
    ),
    CatalogEntry(
        id = "progress", title = "Progress & badges", group = CatalogGroup.Material,
        summary = "Determinate and indeterminate progress, and badges.",
        icon = R.drawable.ic_t_loader_2, keywords = "progress spinner linear circular badge",
        page = { ProgressPage(it) }
    ),
    CatalogEntry(
        id = "lists", title = "Lists", group = CatalogGroup.Material,
        summary = "List items, dividers and swipe-to-remove with undo.",
        icon = R.drawable.ic_t_list, keywords = "list item swipe dismiss undo divider",
        page = { ListsPage(it) }
    ),
    CatalogEntry(
        id = "tabs", title = "Tabs & segmented", group = CatalogGroup.Material,
        summary = "A sliding tab row and a segmented control.",
        icon = R.drawable.ic_t_toggle_left, keywords = "tabs segmented control",
        page = { TabsPage(it) }
    )
)
