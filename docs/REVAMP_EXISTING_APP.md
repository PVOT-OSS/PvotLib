# Revamping an existing app onto PvotLib

A playbook for moving an app (PvotMeasure, PvotCalculator, Messages, ...) from its current UI to the
Pvot design system. The aim is not a reskin: the app should end up using the library's components
and tokens, so it looks and moves like every other Pvot app and gets future fixes for free.

Read [CLAUDE.md](../CLAUDE.md) first for the rules that apply everywhere. This file is the process.

## Principles

1. **Components over styling.** If the library has the component, use it. Do not restyle a Material
   widget to resemble one. See the table in CLAUDE.md.
2. **Behavior stays, presentation changes.** Do not change what the app does, its data layer,
   navigation graph or business logic, unless the user asked. A revamp that quietly changes behavior
   is hard to review and easy to regress.
3. **Fix the library, not the app.** If a component is missing or wrong, the fix belongs in PvotLib
   (GUIDE.md section 8). Do not paste a one-off workaround into the app; report the gap.
4. **Small, reviewable steps, each one runnable.** The app builds and launches after every step.
5. **Do not commit or push.** Hand the user a summary per step and let them commit.

## 0. Before touching code

- Read the app's README, module layout and screens. List every screen and every distinct UI element.
- Build it and run it on the device first. Screenshot each screen as the "before".
- Find the existing design: theme, colors, fonts, custom views, icon set, animations. Note anything
  the library cannot express (a custom canvas, camera overlay, AR view). Those stay as they are,
  wrapped in the new theme.
- Check the app's license is compatible (Apache-2.0 is). Add Tabler's notice to the app's third-party
  file when its icons are adopted.

Write a short inventory for the user: screens, components found, and the Pvot component each maps
to. Get a nod on anything ambiguous before starting.

## 1. Add the dependency

From the Maven repository (preferred) or as a source include, see README "Installation". Components
added after 0.1.0 only exist in a newer release or when including from source; check CHANGELOG.

```kotlin
// libs.versions.toml
pvot-design-system = { group = "com.prauga.pvot", name = "design-system", version.ref = "pvot" }
pvot-core-ui       = { group = "com.prauga.pvot", name = "core-ui", version.ref = "pvot" }
```

Compose UI, Material 3 and Activity Compose come with the library. Remove any duplicate declarations
in the app's own catalog that now conflict.

## 2. Theme first

Everything else depends on this being right.

1. Make the main activity extend `PvotBaseActivity` (edge-to-edge) and call `setPvotContent { }`.
2. Wrap the content in `PvotAppTheme`. Keep dynamic color off unless the app already offered it.
3. Delete the app's own `MaterialTheme`, color and typography definitions, and the XML theme's color
   attributes the Compose UI no longer reads. Keep the XML theme only for window background and the
   splash.
4. Replace hardcoded `Color(0x...)` in composables with `MaterialTheme.colorScheme` roles. Where the
   old color has no role, pick the nearest role; do not add a new color.
5. Build, run, screenshot. Even before any component changes the app should look recognisably Pvot:
   violet primary, tinted surfaces, black dark background.

## 3. Scaffold and navigation

- Replace the app's bottom navigation with `PvotNavBar`. Tabs are `PvotTabItem(iconRes, labelRes,
  contentDescriptionRes)`. Keep to 3 to 5 tabs.
- Apps with a top-level screen list use `PvotScreen` for each scrolling screen, so content clears the
  nav bar and scrolls with the compact behavior.
- Add scroll-driven compaction in the scaffold: swiping up through a list compacts the bar,
  swiping down or switching tabs restores it. Copy the `NestedScrollConnection` pattern from
  `catalog/.../MainActivity.kt` and pass `compact` to the bar.
- Apps without tabs (single task apps) skip the bar; do not add one for its own sake. Use a plain
  top row with a back control and a `PvotSectionHeader`-style title.
- Screen-to-screen transitions: slide plus fade using `PvotMotion.Settle` at `PvotDurations.Long`, as
  the catalog hub does. Keep the previous screen's scroll position.

## 4. Replace components, in this order

Go screen by screen, not widget type by widget type, so each screen is finished and verifiable.

| Existing | Becomes | Notes |
|---|---|---|
| `Card`, custom surfaces | `PvotCard` | add `onClick` for tappable ones; no manual elevation |
| `LazyColumn` of items | `PvotScreen` | keep item content, use `pvotReveal(index)` for entrance |
| Spinners, shimmer | `PvotSkeleton`, `PvotLoadingContent` | skeletons shaped like the real content |
| Ad-hoc empty/error layouts | `PvotEmptyContent`, `PvotErrorContent` | drive from `UiState<T>` |
| `Slider`, `RangeSlider` | `PvotSlider`, `PvotRangeSlider` | `steps` for discrete values |
| `TextField`, `OutlinedTextField` | `PvotTextField` | label as a string; validation via `isError` + `supportingText` |
| `AlertDialog` | `PvotDialog` | `visible` flag, not conditional composition; destructive actions use `PvotDialogActionStyle.Destructive` |
| `ModalBottomSheet`, action menus | `PvotBottomSheet` + `PvotSheetItem` | menus with 2 to 6 actions fit a sheet better than a dropdown |
| Time and duration inputs | `PvotClockPicker`, `PvotDurationPicker` | |
| Section titles | `PvotSectionHeader` | |

For widgets the library does not have, keep the Material 3 component with default parameters and no
local styling; list it in the "library gaps" report (step 7).

Per screen, also apply:
- **Spacing:** screen padding `PvotSpacing.L`, items `M` apart, sections about 28dp. Remove magic
  numbers; every `dp` should be a token or have a reason.
- **States:** every screen that loads has loading, empty and error. Do not leave a blank screen.
- **Press and motion:** tappable things have press feedback (`PvotCard(onClick)` does; custom rows use
  `pvotPressScale`). Changing content animates (`animateContentSize`, crossfade, `AnimatedContent`).
- **Touch targets:** 48dp minimum. Make the whole row or surface the target, not just the label.
- **Text:** all strings in `strings.xml`. No hardcoded text.

## 5. Icons

Swap the app's icons for Tabler outline icons.

1. Find each icon on tabler.io/icons; prefer the closest meaning, not the closest shape.
2. Convert the SVG to a vector drawable: 24dp viewport, `strokeColor` white, `strokeWidth` 2, round cap
   and join, `fillColor` none. Tabler's outline SVGs are plain `<path>` elements, so `pathData` copies
   across as is.
3. Tint through `LocalContentColor` (plain `Icon` with no `tint`) or a theme role. Never a hex.
4. Keep the license comment in each file and add Tabler to the app's third-party notices.

Domain-specific glyphs with no Tabler equivalent (for example a ruler overlay marker) may stay as
custom drawables; keep them stroke-based at 2dp so they sit with the rest.

## 6. Handling what the library cannot do

- **Custom canvases and camera or AR views** stay. Style only what surrounds them: controls sit on
  `PvotCard` or tinted surfaces, with `MaterialTheme.colorScheme` colors and `PvotSpacing`.
- **Full-screen immersive screens** may hide the nav bar; do it with a `visible` flag and animate it.
- **Anything that would need hand-styling** a Material widget: stop and report it. Do not fork the
  look into the app.

## 7. Verify, then report

For every screen: build, install, and look at it on the device in dark and light, at default and large
font size, and with TalkBack labels present (content descriptions on icon-only controls). Exercise the
interactions: tap, long press, drag, rotate, keyboard open on any text field.

Hand the user:
1. What changed, screen by screen, with before/after if screenshots were taken.
2. What was **not** verified.
3. **Library gaps:** components the app needed that PvotLib lacks or that behaved wrongly, each with a
   concrete proposal. This list is how the library improves.
4. A suggested commit split (theme, scaffold, then one per screen). Do not run git commit or push.

## Common mistakes

- Restyling `NavigationBar` instead of using `PvotNavBar`.
- Leaving a Material `Slider` or `AlertDialog` "for now".
- Hex colors or magic `dp` creeping back in.
- Wrapping a `PvotScreen` in another scroll container (nested vertical scroll crashes).
- Showing a dialog by adding it conditionally, which loses the exit animation: pass `visible`.
- Changing behavior while changing looks.
- Declaring done without running it on the device.
