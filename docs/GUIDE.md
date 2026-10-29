# PvotLib guide

The reference for the Pvot design system: how the libraries are laid out, every token, the motion
specs, each component, and the rules for working on the libraries. To add PvotLib to an app, see
[Installation](../README.md#installation).

- **Artifacts:** `com.prauga.pvot:design-system` and `com.prauga.pvot:core-ui`, released together
  under one version
- **Packages:** `com.prauga.pvot.designsystem`, `com.prauga.pvot.coreui`
- **Min SDK:** 27 · **Compile SDK:** 37.1
- **Depends on (exposed as `api`):** Compose BOM, Compose UI, Material 3, Activity Compose
- **AOSP:** built inline as `PvotDesignSystem` and `PvotCoreUi` (`Android.bp`)
- **Catalog app:** `catalog/` shows every component on a phone

```
design-system/src/main/java/com/prauga/pvot/designsystem/
├── theme/        PvotAppTheme, PvotTheme, PvotThemeDefaults, palette, PvotTypography, PvotShapes, PvotSpacing
├── effects/      pvotPressScale
├── motion/       PvotMotion, PvotDurations, pvotReveal
├── components/   PvotCard, PvotScreen, PvotSkeleton, PvotDialog, PvotBottomSheet
│   ├── picker/   PvotClockPicker, PvotDurationPicker, PvotPickerColors, PvotPickerDefaults
│   ├── textfield/ PvotTextField, PvotTextFieldColors, PvotTextFieldDefaults
│   └── slider/   PvotSlider, PvotRangeSlider, PvotSliderColors, PvotSliderDefaults
├── navigation/   PvotNavBar, PvotNavBarItem, PvotTabItem, PvotNavBarColors, PvotNavBarSizes, PvotNavBarDefaults
└── platform/     PvotBaseActivity

core-ui/src/main/java/com/prauga/pvot/coreui/
└── UiState, PvotLoadingContent, PvotErrorContent, PvotEmptyContent, PvotSectionHeader
```

Packages are layered: `theme` → `effects` → `motion` → `components`, `navigation` → `platform`.
A package only imports from the layers before it; `theme` imports nothing from the library.
`core-ui` doesn't depend on `design-system`.

---

## 1. Setup

```kotlin
class MainActivity : PvotBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPvotContent {
            PvotAppTheme(dynamicColor = true) {
                // content
            }
        }
    }
}
```

`PvotBaseActivity` turns on edge-to-edge before any content is set. `PvotAppTheme` installs a
Material 3 theme, so stock Material components pick up the Pvot palette too.

| Parameter | Default | |
|---|---|---|
| `darkTheme` | `isSystemInDarkTheme()` | Which arrangement of the palette to use |
| `dynamicColor` | `false` | Prefer the wallpaper colors on Android 12+ |
| `colorScheme` | `PvotThemeDefaults.colorScheme(darkTheme, dynamicColor)` | Overrides both of the above |
| `typography` | `PvotTypography` | |

`PvotTheme.isDynamicColor` tells components whether dynamic color is in effect;
`PvotThemeDefaults.isDynamicColorSupported` whether the device can supply it.

## 2. Color

Read colors from `MaterialTheme.colorScheme`. The Pvot palette fills every Material role in a
light and a dark arrangement (`PvotPrimaryLight`, `PvotOnPrimaryDark`, …, `PvotScrim`), exposed
as `PvotLightColorScheme` and `PvotDarkColorScheme`.

| Role | Light | Dark |
|---|---|---|
| primary | `#6B66FF` | `#CBBEFF` |
| secondary | `#605B71` | `#CAC3DC` |
| tertiary | `#7D5260` | `#EFB8C8` |
| error | `#BA1A1A` | `#FFB4AB` |
| background / surface | `#FFFFFF` | `#000000` |

Component colors come from each component's `*Defaults` object and are never read from the theme
directly; see [Components](#6-components).

## 3. Typography

`PvotTypography` overrides `bodyLarge` (16sp / 24sp, 0.5sp tracking, normal weight); every other
style is the Material default.

## 4. Shape & spacing

| `PvotShapes` | Value | Used by |
|---|---|---|
| `Small` | 8dp corners | `PvotSkeleton` |
| `Card` | 16dp corners | `PvotCard` |
| `Selection` | 20dp corners | picker selection row |
| `Pill` | circle | unselected nav item |

| `PvotSpacing` | Value | Used for |
|---|---|---|
| `XS` | 4dp | gaps between closely related elements |
| `S` | 8dp | gaps inside a component, e.g. icon to label |
| `M` | 12dp | gaps between list items |
| `L` | 16dp | padding around a screen's content |

## 5. Motion

| `PvotDurations` | ms | Used for |
|---|---|---|
| `Short` | 200 | quick state changes, e.g. a nav item scaling on selection |
| `Medium` | 240 | a nav item growing into or out of its pill |
| `Long` | 260 | a list item revealing into place |

`PvotMotion` holds the rest:

| Value | |
|---|---|
| `Settle` | Expo-out easing, `CubicBezierEasing(0.16, 1, 0.3, 1)` |
| `RevealDurationMillis` | 260 (`PvotDurations.Long`) |
| `StaggerStepMillis` | 20, delay added per item |
| `MaxStaggeredItems` | 3, past this the delay stops growing |
| `RevealRise` | 12dp |
| `PressedScale` | 0.97 |
| `PressSpring` / `pressSpring()` | damping 0.55, `StiffnessMediumLow`: settles with some life |

**Press feedback:** `Modifier.pvotPressScale(interactionSource)` scales an element to
`PressedScale` on `PressSpring` while pressed. `PvotCard` applies it when it has an `onClick`.

**Entrances:** `Modifier.pvotReveal(index)` fades and rises an item into place, delayed by
`index × StaggerStepMillis` up to `MaxStaggeredItems`, so long lists never wait.

```kotlin
PvotScreen {
    itemsIndexed(entries) { index, entry ->
        FeedCard(entry, Modifier.pvotReveal(index))
    }
}
```

## 6. Components

| Component | Package | What it is |
|---|---|---|
| `PvotCard` | `components` | Rounded, translucent surface; pass `onClick` to make it a button with press feedback |
| `PvotScreen` | `components` | `LazyColumn` with screen padding that clears the floating nav bar |
| `PvotSkeleton` | `components` | Shimmering placeholder shown while content loads |
| `PvotDialog` | `components` | Centered dialog that eases in and out over its own scrim; icon, title, message, optional content and stacked `PvotDialogAction` buttons. Driven by `visible` |
| `PvotBottomSheet` | `components` | Modal sheet with a slim handle, optional title and scrolling content; `PvotSheetItem` rows. Driven by `visible` |
| `PvotTextField` | `components.textfield` | Soft filled field: floating label, focus ring, validation shake, animated supporting text, counter, icons, multiline |
| `PvotSlider` | `components.slider` | Minimal slider: slim track, round thumb, tap to jump, optional snapping steps |
| `PvotRangeSlider` | `components.slider` | Two-thumb version of `PvotSlider` for choosing a span |
| `PvotClockPicker` | `components.picker` | Wheel picker for a `LocalTime` (hours, minutes) |
| `PvotDurationPicker` | `components.picker` | Wheel picker for a `Duration` under 24h (hours, minutes, seconds) |
| `PvotNavBar` | `navigation` | Floating bottom bar; selected tab opens into a filled pill; dragging along the bar previews and selects. Takes a list of `PvotTabItem` or a slot of `PvotNavBarItem` |
| `PvotNavBarItem` | `navigation` | One item for the slot overload of `PvotNavBar` |
| `PvotLoadingContent` | `core-ui` | Indeterminate progress indicator for `UiState.Loading` |
| `PvotErrorContent` | `core-ui` | Title, message and retry button for `UiState.Error` |
| `PvotEmptyContent` | `core-ui` | Message shown when there is nothing to list |
| `PvotSectionHeader` | `core-ui` | Title above a group of items |

**Colors and sizes.** Each component takes a `colors` (and the nav bar a `sizes`) parameter whose
default comes from its `*Defaults` object:

| | Default | Overriding |
|---|---|---|
| Nav bar colors | `PvotNavBarDefaults.themeColors()`: derived from the color scheme | `PvotNavBarDefaults.colors(...)`, `PvotNavBarDefaults.colors(colorScheme)` |
| Nav bar sizes | `LocalPvotNavBarSizes.current` | `PvotNavBarSizes(...)`; provide `LocalPvotNavBarSizes` so `PvotScreen` clears the same height |
| Text field colors | `PvotTextFieldDefaults.themeColors()`, derived from the color scheme | `PvotTextFieldDefaults.colors(colorScheme, ...)` |
| Slider colors | `PvotSliderDefaults.themeColors()`, derived from the color scheme | `PvotSliderDefaults.colors(colorScheme, ...)` |
| Picker colors | `PvotPickerDefaults.themeColors()`, derived from the color scheme | `PvotPickerDefaults.colors(...)`, `PvotPickerDefaults.colors(colorScheme)` |
| Card | `PvotCardDefaults.shape`, `.colors()`, `.elevation()` | parameters of `PvotCard` |
| Skeleton | `PvotSkeletonDefaults.Shape`, `.baseColor`, `.highlightColor` | parameters of `PvotSkeleton` |
| Screen | `PvotScreenDefaults.contentPadding()`, `.VerticalArrangement` | parameters of `PvotScreen` |

**Loading states.** Model a screen's content as `UiState<T>` and show the matching component:

```kotlin
when (val state = uiState) {
    UiState.Loading -> PvotLoadingContent()
    is UiState.Error -> PvotErrorContent(title, state.message, onRetry = { retryKey++ })
    is UiState.Success -> Content(state.data)
}
```

`UiState.Error` keeps the `cause`, so callers can tell a network error from a parse error without
matching on the message.

## 7. Rules for library code

**API**
- Explicit API mode is on: every public declaration states `public` and its type.
- Every public declaration has KDoc that says what it is for.
- The public API is recorded in `design-system/api/design-system.api` and `core-ui/api/core-ui.api`.
  `./gradlew build` and CI fail when it changes without `./gradlew apiDump`; commit the `.api`
  diff with the change.
- Semantic versioning. Before 1.0.0, a minor version may break the API; list every break in the
  changelog.
- Library resources start with `pvot_` (lint enforces it).

**Design**
- Use the tokens: `MaterialTheme.colorScheme`, `PvotShapes`, `PvotSpacing`, `PvotMotion`,
  `PvotDurations`. A value used by one component only belongs in that component's `*Defaults`.
- Component colors and sizes go through a `*Colors`/`*Sizes` class and a `*Defaults` object, and
  the component takes them as parameters. The theme never holds component configuration.
- Respect the package layers; `theme` imports nothing from the library.

**Build**
- Module setup lives in `build-logic`: `pvot.android.library`, `pvot.android.application`,
  `pvot.android.compose`, `pvot.android.api`. Modules declare only their namespace, publishing
  and dependencies.
- A new AndroidX dependency in a library also goes into its `Android.bp`.

## 8. Adding a component

1. The composable, in the package of its layer, with KDoc and a `*Defaults` object for its values.
2. A catalog page: add a page composable under `catalog/.../hub/` and list it in `CatalogEntries.kt`.
3. A row in the components table above.
4. An entry in `CHANGELOG.md` under the unreleased version.
5. `./gradlew apiDump`, then `./gradlew build`.
6. Look at it on a phone: `./gradlew :catalog:installDebug`.
