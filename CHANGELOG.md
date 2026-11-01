# Changelog

PvotLib follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things. `design-system` and `core-ui` are released
together under one version.

## Unreleased

### Changed
- The toolchain moves to Android Gradle Plugin 9.5.0, Kotlin 2.4.21 and Java 21 (library bytecode
  and CI), so apps can adopt libraries that require them, such as SceneView for AR. Kotlin 2.4
  adds a no-argument constructor to `PvotGlassStyle` and `PvotNavBarSizes` in the recorded API.

### Fixed
- The glass lens shader declared a variable named `smooth`, a reserved word in GLSL. Devices that
  compile AGSL through a GLSL front end (many Android 15 builds, and the emulator) rejected the
  whole shader, so the glass drew nothing and the content behind showed through sharp.

### Added
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
  centers its message. Existing calls compile unchanged, and the old signature is kept, hidden,
  so code compiled against 0.1.0 still links.

### Changed
- `PvotNavBar` takes a `backdrop`: given one, the bar is glass over it, with
  `PvotNavBarDefaults.glassColors()` and a softer shadow. A new parameter, so code compiled
  against 0.1.0 must be recompiled.

## 0.1.0 (unreleased)

The first published release.

### Changed
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

### Added
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
