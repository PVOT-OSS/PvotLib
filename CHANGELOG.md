<!--
SPDX-FileCopyrightText: 2026 Saalim Quadri <danascape@gmail.com>
SPDX-License-Identifier: Apache-2.0
-->

# Changelog

PvotLib follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things. `design-system`, `core-ui` and
`design-system-views` are released together under one version.

## Unreleased

### Added
- `design-system`: `PvotFastScroller` takes a `LazyGridState` too, for a grid split into sections
  such as photos by month, and its bubble widens into a pill to fit a label longer than a letter,
  such as "Nov 2026".
- `design-system`: `PvotSwipeRow` takes `endButtons`: a swipe toward the start uncovers round
  buttons, such as History and Delete, and the row stands open on them as a card in
  `openContainerColor`, as in iOS Recents. Tap a button to run it; swipe on past
  `PvotSwipeRowDefaults.FullSwipeFraction` to run the last one. One row stands open at a time, and
  tapping a row or scrolling shuts it. An end button whose `PvotSwipeAction` has a `menu` opens it
  as a `PvotMenu` titled with its label, such as a callback reminder's times. `PvotSwipeRowState`
  gains `isOpen` and `close()`; shown on the catalog's Swipe row page.
- `design-system`: `PvotMenu` takes a `title`, a dimmed line above the items
  (`PvotMenuColors.titleColor`, `PvotMenuDefaults.TitleHeight`), and an overload takes its icon as a
  `Painter`.
- `design-system`: `PvotSwitch` (`components`), a pill switch whose thumb lifts into a lens under
  the finger and slides; `PvotSwitchRow` now ends with one. `PvotMenu` (`components`), a round
  button whose menu grows out of it, with `PvotMenuItem` and `PvotMenuSeparator`; tap an item, or
  press the button and slide onto one to pick it. Each with its `*Colors` and `*Defaults`; shown on
  the catalog's Settings rows and Menu pages.
- `design-system`: lift to lens. Held, the pill of `PvotSegmentedControl` and the item under a drag
  along `PvotNavBar` lift into a lens over their labels and follow the finger; the thumbs of
  `PvotSlider` and `PvotRangeSlider` lift into a lens that swells the track. A quick swipe up or
  down that starts on a segmented control or switch still scrolls the list it is in.
  `PvotMotion.TouchHoldMillis`, `slideSpring()`, `followSpring()` and `liftSpring()` carry the
  timing.
- `design-system`: `Modifier.pvotGlassPress`, the press feel of every glass control: the longer side
  grows by a fixed `PvotGlassDefaults.PressGrow`, the control stretches toward a drag and follows
  it a little, a soft light shows at the point of touch, and it springs back past its size on
  release. `pvotGlassSurface` uses it in place of `pvotPressScale`.
- `design-system`: `PvotGlassStyle.dome` (the middle swells what is under it, for a lens) and
  `PvotGlassStyle.saturation` (what shows through is more vivid, 1.5 by default);
  `PvotGlassDefaults.LensStyle`, `LabelLensStyle` and `lensStyle(lift)`; `LightThemeFillColor`,
  `LightThemeGlowGain` and `themeFillColor()`, which `style(intensity)` applies on a light theme.
- `catalog`: a Glass playground page with the bend, dispersion, frost, rim, dome and saturation of a
  pane you can drag over the background, and the material and lens presets.
- `design-system`: `PvotAvatarStack` (`components.list`), overlapping avatars with a "+n" count, and
  the agenda pieces in `components.calendar`: `PvotDateHeader`, `PvotDayStrip` (with
  `PvotDayStripColors`) and `PvotEventCard`, each with its `*Defaults`. Shown on the catalog's
  Calendar page. They come from the PVOT Calendar app's Compose rework.
- `design-system`: camera controls in `components.camera`, on glass over a live preview:
  `PvotShutterButton` (`PvotShutterState`, hold to record, slide right to lock, slide left for a
  burst), `PvotZoomSelector` and `PvotZoomDial`, `PvotModeSelector`, `PvotControlPanel` with
  `PvotControlTile`, `PvotAdjustmentSheet` with `PvotRuler` and `PvotChoiceRow`, and `PvotGlassChip`,
  each with its `*Colors` and `*Defaults`, plus `PvotCameraGlassDefaults`. Shown on the catalog's
  Camera page. They come from the PVOT Camera app's Compose screen.

### Changed
- The glass bends the other way, as iOS does: the rim pulls what lies just inside it out to the
  edge, as a drop of water does, instead of showing what lies outside. The default
  `PvotGlassStyle` follows Glisse's measured defaults: the bend reaches 0.45 of the shorter side,
  a slight split (red furthest out), no blur, no mirror and a faint rim light (0.13), over a dark
  grey tint at a little over half strength (white on a light theme), saturated 1.5x. Set
  `PvotGlassStyle.saturation` to 1, `blur` to 5.dp and `reflection` to 0.85f for the old look.
  `Regular` and `Thick` keep their blur.
- Glass controls swell under the finger by a fixed amount of their longer side and overshoot on
  release (`pvotGlassPress`), instead of the uniform 1.04 scale.
- `PvotSwitchRow`'s `switchColors` is a `PvotSwitchColors` and `PvotSettingsRowDefaults.switchColors()`
  returns one, now that the row ends with a `PvotSwitch` rather than the Material switch.
- The press, lift and drag feel is adapted from Glisse (MIT), credited in `THIRD_PARTY_NOTICES.md`.

### Fixed
- `PvotScreenScaffold` keeps its content padding at the top bar's expanded height while a
  `PvotTopBar` folds and unfolds: the compact bar floats over the content instead of moving the
  first item under the finger, which jumped the hero card on screens such as call details. A bar
  that is compact for good pads by its compact height, as before.
- `rememberPvotTopBarCompact` has hysteresis: the bar folds once the list scrolls past the
  threshold and unfolds only once the list is back at the top, so a list that stops near the
  threshold no longer flaps the bar.

## 1.0.0 (2026-11-03)

The first published release.

### Added
- `design-system`, the glass floating layer:
  - `components`: `Modifier.pvotGlassSurface` and `PvotGlassSurface`, the recipe every glass
    control is built from (press swell, shadow, hairline edge, glass, clip, rim, click, with
    `onLongClick` and a `role`), public so an app can build its own glass control;
    `PvotGlassCard` and `PvotGlassIconButton` are built on it.
  - `effects`: `PvotGlassMaterial` (`Clear`, `Regular`, `Thick`) with
    `PvotGlassDefaults.ClearStyle/RegularStyle/ThickStyle`, `styleOf`, `materialFill` and
    `style(intensity, material)`; text sits only on `Regular` or `Thick`, whose surface tint keeps
    it readable (held to 4.5:1 on `Thick` and 3:1 on `Regular` over the palette by a unit test).
    `PvotGlassDefaults.SolidIntensity` and `FrostedIntensity`.
  - `effects`: `PvotGlassIntensity.isReduced()` (battery saver, observed live, or no blur) and
    `effective(preferred)`, and `ProvidePvotGlassIntensity(preferred)` for an app's root;
    `LocalPvotGlassReduced` for tests. `LocalPvotGlassBackdrop`, the backdrop the glass
    components draw over by default.
  - `theme`: `PvotExtendedColors` (success and warning, each with content and container roles),
    `PvotLightExtendedColors`, `PvotDarkExtendedColors`, `PvotThemeDefaults.extendedColors` and
    `PvotTheme.extendedColors`; `PvotAppTheme` takes `extendedColors`.
  - `components.background`: `PvotLiveBackground` (the catalog's drifting blobs, moved into the
    library, stepped at about fifteen frames a second and still when animations are off),
    `PvotGradientBackground`, `PvotPhotoBackground` with `preparePvotPhotoBackground` (a photo
    shrunk and box-blurred once off the main thread, then drawn under a scrim), `PvotBackgroundMotion`
    and `PvotBackgroundDefaults`.
  - `components`: `PvotScreenScaffold`, the frame of a glass screen: the plain theme background
    (a photo when passed) and content inside one backdrop, top bar, bottom bar, floating action, snackbar host and overlays over it, each
    getting the backdrop through `LocalPvotGlassBackdrop`; the content gets the padding that
    clears the top bar, or the status bar when there is none.
  - `navigation`: `PvotTopBar` over a `backdrop` draws its compact bar as glass, fading in as the
    large title folds, with `PvotTopBarDefaults.glassColors()` and `.Material`.
  - `components.call`: `PvotCallButton` (a call control with its label, a toggle that morphs
    and reads as a switch, a badge dot), `PvotCallActionButton` and `PvotCallAction` (answer in
    the success color, decline and end in the error color, never glass), with `*Colors` and
    `*Defaults`.
  - `components.keypad`: `PvotKeypad` and `PvotKeypadKey` (down and up per key, keyboard haptics,
    long presses, "2, ABC" for TalkBack, other scripts' digits through `PvotKeypadDefaults.keys`)
    and `PvotKeypadDisplay` (the number and a backspace that clears when held, or a slot).
  - `components.list`: `PvotAvatar`, a photo from any `Painter` or the letter tile, sized from a
    list row to the call screen, with a ring and a badge.
  - `components`: `PvotSlideToAct`, `PvotSlideTarget`, `PvotSlideDecision` and
    `PvotSlideToActDefaults` (`decide`, `isTouchExplorationEnabled`): slide up or down to act.
  - `components`: `PvotCapsule` (a glass pill grouping floating controls), `PvotTabRow` with `*Colors` and
    `*Defaults` (text tabs with a sliding underline).
  - `components.button`: `PvotIconButton` with `*Colors` and `*Defaults` (round icon buttons and
    toggles, tonal, filled or plain).
  - `components.list`: `PvotListGroup` with `*Scope`, `*Colors` and `*Defaults` (rows on one card
    with dividers); `PvotRowIconStyle` and `PvotRowChevron`, taken by `PvotSwitchRow` (`iconStyle`)
    and `PvotPreferenceRow` (`iconStyle`, `chevron`); icon `pvot_ic_arrow_right`.
  - `components.progress`: `PvotLinearProgress` (seekable with `onSeek`) and `PvotProportionBar`,
    with `*Colors` and `*Defaults`; `PvotProportionBarDefaults.tonalColors` gives one hue in steps.
  - `components.call`: `PvotCallActionButton(labelInside = true)`, a pill with its label inside.
  - `components.keypad`: `PvotKeypadColors`, `PvotKeypadDefaults.flatColors()` (as before) and
    `.filledColors()` (round filled keys); `PvotKeypad` takes `colors`.
  - `effects`: `PvotGlassDefaults.materialStyle(material)`, a material's tinted style for a glass
    component's `style`.
  - Icons `pvot_ic_phone`, `pvot_ic_phone_off` and `pvot_ic_backspace`; strings `pvot_state_on`,
    `pvot_state_off` and `pvot_keypad_backspace`.
  - `design-system/src/test`: the first JVM unit tests (materials, extended colors, background
    phase, photo blur), and instrumented tests for the glass surface, top bar and scaffold.
- `design-system-views`, a new library (`com.prauga.pvot:design-system-views`) for apps with XML
  layouts. `PvotTheme` (and `.Light`, `.Dark`, `.Dialog`) is a Material 3 theme for Material
  Components for Android carrying the Pvot palette, corner scale, body type, a filled card style and
  transparent system bars; `@color/pvot_*`, `@dimen/pvot_corner_*`, `@dimen/pvot_spacing_*`,
  `@integer/pvot_duration_*` and `@interpolator/pvot_settle` expose the tokens. The colors, corners,
  spacing and durations are generated from the Kotlin tokens by `tokensDump`; `tokensCheck` fails
  the build when they drift. Builds in AOSP as `PvotDesignSystemViews`. Its minimum SDK is 24, below
  the other libraries' 27, so apps that still support Android 7 can use it. Floating action buttons
  are pill-shaped in the primary color, like `PvotFab`. Alert dialogs (AppCompat and
  `MaterialAlertDialogBuilder`, and any dialog on `PvotTheme.Dialog`) use the `PvotDialog` surface and
  pill actions: a filled primary positive button, soft neutral negative and neutral buttons. Popup
  and overflow menus sit on the surface variant with 8dp corners.
- `catalog`: an "XML theme" page inflating stock Material Components widgets under `PvotTheme`.
- `design-system`:
  - `effects`: glass. `Modifier.pvotGlass` draws an element as clear glass over a
    `PvotGlassBackdrop` (`rememberPvotGlassBackdrop`, `Modifier.pvotGlassBackdrop`): frosted
    on Android 12+, with a rim that bends, splits and mirrors what lies behind on Android 13+.
    `Modifier.pvotGlassRim` draws a rim of light that turns as the phone tilts. Styled through
    `PvotGlassStyle` and `PvotGlassDefaults`.
  - `LocalPvotGlassIntensity` and `PvotGlassDefaults.style(intensity)` set how much glass, from a
    frosted blur only (0) to the full effect (1); `pvotGlassRim` takes an `alpha`.
  - `components`: `PvotGlassCard` and `PvotGlassIconButton`, with their defaults. Over a
    backdrop they float on a soft shadow, and swell and brighten while pressed.
  - `navigation`: `PvotNavBarDefaults.glassColors()`.
  - `navigation`: `PvotTabItem.badgeCount` shows a count badge on a tab's icon (above 999 it
    reads "999+"), and TalkBack reads it with the tab's description.
  - Components for list and settings screens, first needed by the Phone app:
    - `components.list`: `PvotListRow` (leading slot, title, supporting text, trailing slot,
      `onClick`, `onLongClick`, `selected`; a slot overload for rich text), `PvotLetterTile`,
      `PvotSwipeRow` with `PvotSwipeAction`, `PvotSwipeRowState` and
      `rememberPvotSwipeRowState` (start and end actions; a removing action slides the row off
      for Undo), `PvotSwitchRow` and `PvotPreferenceRow`, with `PvotListRowColors`,
      `PvotListRowDefaults`, `PvotLetterTileColors`, `PvotLetterTileDefaults`,
      `PvotSwipeRowDefaults` and `PvotSettingsRowDefaults`.
    - `components`: `PvotDialogContent`, the card of a `PvotDialog` without its window, for a
      dialog whose window comes from elsewhere (such as an `android.app.Dialog` with platform
      window flags). `PvotDialog` and `PvotDialogContent` take a null `title` for a message-only
      dialog.
    - `components.list`: `PvotFastScroller` with `PvotFastScrollSection`,
      `PvotFastScrollerColors` and `PvotFastScrollerDefaults` (drag along the end edge of a long
      list to jump by section, with a bubble and a haptic tick per section).
    - `components.button`: `PvotButton` in four `PvotButtonStyle`s (filled, tonal, text,
      destructive) and `PvotFab`, round or extended, with `PvotButtonColors`,
      `PvotButtonDefaults`, `PvotFabColors` and `PvotFabDefaults`.
    - `components`: `PvotSegmentedControl` and `PvotSnackbarHost` / `PvotSnackbar`, with their
      `*Colors` and `*Defaults`.
    - `navigation`: `PvotTopBar`, whose large title folds into the bar when `compact`,
      `PvotTopBarAction`, `rememberPvotTopBarCompact`, `PvotTopBarColors` and
      `PvotTopBarDefaults`.
    - `platform`: `ComponentActivity.enablePvotEdgeToEdge()` and
      `ComponentActivity.setPvotContent {}`, for activities that cannot extend
      `PvotBaseActivity`. `PvotBaseActivity` now calls `enablePvotEdgeToEdge()`; the window
      setup is unchanged.
    - Icons `pvot_ic_arrow_left`, `pvot_ic_chevron_right`, `pvot_ic_x` and `pvot_ic_user` (Tabler)
      and the strings `pvot_top_bar_back` and `pvot_snackbar_dismiss`.
- `core-ui`: `PvotEmptyContent` takes an optional `icon`, `actionLabel` and `onAction`, and
  centers its message. Existing calls compile unchanged.
- `components`: `PvotDialog` (with `PvotDialogAction`, `PvotDialogActionStyle`, `PvotDialogDefaults`)
  and `PvotBottomSheet` (with `PvotSheetItem`, `PvotBottomSheetDefaults`). Both are driven by a
  `visible` flag, so they animate out as well as in.
- `components.textfield`: `PvotTextField`, a soft filled field with a floating label, a focus ring,
  a validation shake, animated supporting text, a character counter, icon slots and multiline,
  plus `PvotTextFieldColors` and `PvotTextFieldDefaults`.
- `components.slider`: `PvotSlider` and `PvotRangeSlider`, a minimal slider of our own with
  tap-to-jump, optional snapping steps and haptic ticks, plus `PvotSliderColors` and
  `PvotSliderDefaults`. Pvot no longer uses the Material slider.
- `PvotMotion.bounceSpring()`, `PvotNavBar(compact = ...)` and `PvotNavBarSizes.compactBarHeight`
  and `compactItemSize`.
- The catalog uses [Tabler Icons](https://tabler.io/icons) (MIT, see `THIRD_PARTY_NOTICES.md`).
- `design-system`:
  - `theme`: `PvotAppTheme` (Material 3 with optional dynamic color), `PvotTheme`,
    `PvotThemeDefaults`, the Pvot palette, `PvotTypography`, and the `PvotShapes` and
    `PvotSpacing` tokens.
  - `effects`: `Modifier.pvotPressScale`.
  - `motion`: `PvotMotion`, `PvotDurations` and `Modifier.pvotReveal`.
  - `components`: `PvotCard`, `PvotScreen`, `PvotSkeleton`, `PvotClockPicker` and
    `PvotDurationPicker`, with `PvotPickerColors` and `PvotPickerDefaults`.
  - `navigation`: `PvotNavBar` (tab item and slot overloads), `PvotNavBarItem`, `PvotTabItem`,
    `PvotNavBarColors`, `PvotNavBarSizes` and `PvotNavBarDefaults`.
  - `platform`: `PvotBaseActivity`.
- `core-ui` (`com.prauga.pvot.coreui`): `UiState`, `PvotLoadingContent`, `PvotErrorContent`,
  `PvotEmptyContent` and `PvotSectionHeader`.
- The public API of both libraries is recorded in `api/` and checked by the build.
- [docs/GUIDE.md](docs/GUIDE.md): every token, motion spec, component and library rule.
- Published to the Maven repository at https://pvot-oss.github.io/PvotLib/maven as
  `com.prauga.pvot:design-system` and `com.prauga.pvot:core-ui`.

### Changed
- `PvotCallActionButton` gives haptic feedback: a confirm for Answer, a reject for Decline and End.
- Call buttons answer in a vivid green and end in a vivid red, the same in light and dark
  (`PvotCallAnswer`, `PvotCallEnd`), so they read at a glance; `PvotCallActionButtonDefaults.themeColors()`
  keeps the theme's success and error colors.
- `LocalPvotGlassIntensity` 0 now means **solid**: an opaque surface with no blur, which costs
  nothing to draw. The frosted blur that 0 used to mean is `PvotGlassDefaults.FrostedIntensity`
  (0.1); `defaultIntensity()` returns it where the device blurs but has no lens. An app that
  persisted 0 for the frosted look should store `FrostedIntensity` instead.
- `PvotAppTheme` takes `extendedColors`, `PvotTopBar` takes `backdrop` and `material`, and
  `PvotNavBar`, `PvotGlassCard` and `PvotGlassIconButton` default their `backdrop` to
  `LocalPvotGlassBackdrop`: source-compatible, binary-incompatible, like the nav bar's `backdrop`
  before.
- The toolchain moves to Android Gradle Plugin 9.5.0, Kotlin 2.4.21 and Java 21 (library bytecode
  and CI), so apps can adopt libraries that require them, such as SceneView for AR. Kotlin 2.4
  adds a no-argument constructor to `PvotGlassStyle` and `PvotNavBarSizes` in the recorded API.
- `PvotSwipeRow` takes `thresholdFraction` and `dragResistance`
  (`PvotSwipeRowDefaults.DragResistance`), so a list can make its swipes heavier and harder to set
  off by accident; the defaults keep today's feel. Source-compatible, binary-incompatible.
- `PvotNavBar` takes a `backdrop`: given one, the bar is glass over it, with
  `PvotNavBarDefaults.glassColors()` and a softer shadow.
- A more vivid palette: saturated violet primary, electric-blue secondary, pink tertiary and
  tinted surface variants.
- `PvotNavBar` is simpler: no chip behind unselected items, a solid primary pill for the
  selected one, a smaller bar and softer shadow. `PvotNavBarDefaults.themeColors()` now always
  derives from the color scheme.
- `PvotNavBar` is a little bigger (68dp bar, 52dp items, 24dp icons) and takes `compact`: it
  animates down to a shorter bar of icon-only circles. The catalog compacts it while the user
  swipes up through a list and restores it on the way back.
- `PvotNavBar` items open and close with one eased motion (no bounce), and dragging along the
  bar previews the item under the finger and selects it on release.

### Fixed
- `PvotSwipeRow`: no slivers of the action's color at the row's corners while it slides.
- `PvotSwipeRow` no longer leaves a thin arc of the action's color at the row's corners after a
  short swipe springs back: the row settles exactly in place, and the action's color fills only
  the gap the row leaves, never under it.
- The glass lens shader declared a variable named `smooth`, a reserved word in GLSL. Devices that
  compile AGSL through a GLSL front end (many Android 15 builds, and the emulator) rejected the
  whole shader, so the glass drew nothing and the content behind showed through sharp.
