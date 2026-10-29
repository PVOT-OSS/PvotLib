# Changelog

PvotLib follows [semantic versioning](https://semver.org): a major version may break the API, minor
versions add to it, and patch versions only fix things. `design-system` and `core-ui` are released
together under one version.

## 0.1.0 (unreleased)

The first published release.

### Added
- `design-system`: `PvotAppTheme` and `PvotTheme` (Material 3 with dynamic color and extended
  nav bar and picker colors), `PvotBaseActivity`, `PvotNavBar` (tab item and slot overloads),
  `PvotCard`, `PvotScreen`, `PvotSkeleton`, `PvotClockPicker`, `PvotDurationPicker`,
  `Modifier.pvotPressScale`, `Modifier.pvotReveal` and `PvotMotion`.
- `core-ui`: `UiState`, `PvotLoadingContent`, `PvotErrorContent`, `PvotEmptyContent` and
  `PvotSectionHeader`.
- Published to the Maven repository at https://pvot-oss.github.io/PvotLib/maven as
  `com.prauga.pvot:design-system` and `com.prauga.pvot:core-ui`.
