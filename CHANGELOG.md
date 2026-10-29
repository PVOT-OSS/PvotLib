# Changelog

PvotLib follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things. `design-system` and `core-ui` are released
together under one version.

## Unreleased

### Added
- `design-system`:
  - `effects`: glass. `Modifier.pvotGlass` draws an element as clear glass over a
    `PvotGlassBackdrop` (`rememberPvotGlassBackdrop`, `Modifier.pvotGlassBackdrop`): frosted
    on Android 12+, with a rim that bends, splits and mirrors what lies behind on Android 13+.
    `Modifier.pvotGlassRim` draws a rim of light that turns as the phone tilts. Styled through
    `PvotGlassStyle` and `PvotGlassDefaults`.
  - `components`: `PvotGlassCard` and `PvotGlassIconButton`, with their defaults. Over a
    backdrop they float on a soft shadow, and swell and brighten while pressed.
  - `navigation`: `PvotNavBarDefaults.glassColors()`.

### Changed
- `PvotNavBar` takes a `backdrop`: given one, the bar is glass over it, with
  `PvotNavBarDefaults.glassColors()` and a softer shadow. A new parameter, so code compiled
  against 0.1.0 must be recompiled.

## 0.1.0 (unreleased)

The first published release.

### Added
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
