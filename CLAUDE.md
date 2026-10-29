# PvotLib

PvotLib is the Pvot design system: a Jetpack Compose library (`design-system`, `core-ui`) plus a
catalog app (`catalog`) that shows every token and component. PVOT-OSS improves existing open source
apps, and this library is how those apps get one consistent look. It is used two ways:

| You are... | Read this first |
|---|---|
| Moving an **existing app** onto the design system (for example PvotMeasure) | [docs/REVAMP_EXISTING_APP.md](docs/REVAMP_EXISTING_APP.md) |
| Starting a **new app** on the design system | [docs/NEW_APP.md](docs/NEW_APP.md) |
| Adding or changing a **library component** | [docs/GUIDE.md](docs/GUIDE.md) sections 7 and 8, then the rules below |

[docs/GUIDE.md](docs/GUIDE.md) is the reference for every token, motion spec and component. Read the
catalog page for a component before using it: `catalog/.../hub/` has one page per component, with
live demos and a usage snippet.

## Working agreements

- **Never commit or push.** Make the change, then tell the user what to commit. The user owns git.
  Work happens on `feat/design-revamp` (the `fork` remote is `debz-g/PvotLib`); `origin` is
  PVOT-OSS and is not ours to push to.
- Ask before anything hard to undo or outward-facing. Keep to what was asked.
- Say what you verified and what you did not. "It builds" is not "I looked at it on the device".
- Look at UI changes on a phone, not just in the build: `./gradlew :catalog:installDebug`. The
  user's test device is an S23; the iOS simulator tools do not apply, this is Android.

## The design in one screen

- **Look:** minimal and clean, vivid color, generous space, soft filled surfaces, no heavy outlines or
  shadows. Dark theme is the primary view; check light too.
- **Motion:** short, eased, a little springy, never sluggish and never bouncing a layout. Use
  `PvotMotion` and `PvotDurations`. Press feedback on anything tappable, `pvotReveal` for list
  entrances, ease state changes with the settle curve.
- **Interaction:** think about the finger. Large touch targets (48dp minimum), the whole surface of a
  field or row is the target, haptics on meaningful ticks, drag where drag is natural (nav bar,
  sliders, sheets), undo instead of confirm where an action is reversible.
- **Icons:** Tabler Icons (MIT), outline, 24dp, 2dp stroke, round caps, as vector drawables tinted
  through `LocalContentColor`. Credit lives in `THIRD_PARTY_NOTICES.md`; keep the per-file comment.

## Component rules

Use the Pvot component whenever one exists. Do not use the Material equivalent.

| Need | Use | Never |
|---|---|---|
| Bottom navigation | `PvotNavBar` (`compact` driven by scroll) | `NavigationBar` |
| Slider, range | `PvotSlider`, `PvotRangeSlider` | Material `Slider`, `RangeSlider` |
| Text input, search | `PvotTextField` | `TextField`, `OutlinedTextField` |
| Dialog | `PvotDialog` + `PvotDialogAction` | `AlertDialog` |
| Bottom sheet | `PvotBottomSheet` + `PvotSheetItem` | `ModalBottomSheet` |
| Card / surface | `PvotCard` | `Card` |
| Screen scroll container | `PvotScreen` | bare `LazyColumn` |
| Loading placeholder | `PvotSkeleton`; `PvotLoadingContent` for a whole screen | ad-hoc shimmer |
| Loading, empty, error | `UiState<T>`, `PvotLoadingContent`, `PvotEmptyContent`, `PvotErrorContent` | one-off layouts |
| Time, duration | `PvotClockPicker`, `PvotDurationPicker` | Material time picker |
| Group title | `PvotSectionHeader` | ad-hoc `Text` |

Not built yet (Pvot buttons, chips, switches, list items, tabs, progress): use the Material 3
component, which `PvotAppTheme` already styles, and keep it to default parameters so a Pvot version
can replace it cleanly. If a screen needs one badly, say so and propose adding it to the library
rather than hand-styling it in the app.

## Tokens, not literals

- Color: `MaterialTheme.colorScheme.*`. Never a hex in app or component code. Component-specific
  colors go in that component's `*Colors` and `*Defaults`.
- Space: `PvotSpacing` (XS 4, S 8, M 12, L 16). Screens pad L; items sit M apart; sections about 28dp.
- Shape: `PvotShapes` (Small 8, Card 16, Selection 20, Pill). Dialogs and sheets use 28dp corners.
- Type: `MaterialTheme.typography`. Do not set raw font sizes except inside a component.
- Motion: `PvotMotion`, `PvotDurations`. Do not invent durations.

## Library code rules (summary of GUIDE.md section 7)

- Explicit API mode: every public declaration says `public` and its type, and has KDoc.
- The public API is recorded in `design-system/api/design-system.api` and `core-ui/api/core-ui.api`.
  After any public change run `./gradlew apiDump` and tell the user the `.api` diff is part of the
  change. CI fails without it.
- Layers: `theme` → `effects` → `motion` → `components`, `navigation` → `platform`. A package imports
  only from earlier layers. `core-ui` does not depend on `design-system`.
- Library resources start with `pvot_`. All user-visible text in an app is a string resource.
- Components take `colors`/`sizes` parameters whose defaults come from a `*Defaults` object; the
  theme never holds component configuration.
- A new component is not done until it has: the composable with KDoc, a catalog page listed in
  `CatalogEntries.kt`, a row in the GUIDE components table, a README roadmap row, a CHANGELOG entry
  under the unreleased version, and a look on the device.

## Commands

```
./gradlew :catalog:assembleDebug     # build the catalog
./gradlew :catalog:installDebug      # install on the connected device
./gradlew apiDump                    # record public API changes
./gradlew build                      # build, test, API check, lint
```

If the wrapper's Gradle distribution cannot be downloaded, a compatible Gradle already in
`~/.gradle/wrapper/dists` can run the same tasks. Do not edit the wrapper properties to work around it.
Min SDK is 27; compile SDK is 37.1.

## Where things are

```
design-system/   theme, effects, motion, components, navigation, platform
core-ui/         UiState and loading / error / empty content
catalog/         the catalog app; hub/ holds the pages, legacy/ the pre-redesign UI
docs/            GUIDE.md plus the two playbooks above
```

The catalog has an "Enable New UI" switch (About, Settings). Off shows the pre-redesign UI from
`catalog/.../legacy/`; every redesign change in the catalog is gated on `LocalNewUi`. Library
defaults are the new design, with no switch.
