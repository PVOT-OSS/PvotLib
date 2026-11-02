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
├── effects/      pvotPressScale, pvotGlass, pvotGlassBackdrop, pvotGlassRim, PvotGlassStyle, PvotGlassDefaults
├── motion/       PvotMotion, PvotDurations, pvotReveal
├── components/   PvotCard, PvotGlassCard, PvotGlassIconButton, PvotScreen, PvotSkeleton, PvotDialog, PvotDialogContent, PvotBottomSheet,
│   │             PvotSegmentedControl, PvotSnackbarHost, PvotSnackbar, PvotSlideToAct, PvotCapsule,
│   │             PvotTabRow
│   ├── call/     PvotCallButton, PvotCallActionButton, PvotCallAction, their *Colors and *Defaults
│   ├── keypad/   PvotKeypad, PvotKeypadKey, PvotKeypadColors, PvotKeypadDisplay, their *Defaults
│   ├── progress/ PvotLinearProgress, PvotProportionBar, their *Colors and *Defaults
│   ├── button/   PvotButton, PvotButtonStyle, PvotFab, PvotIconButton, their *Colors and *Defaults
│   ├── list/     PvotListRow, PvotLetterTile, PvotAvatar, PvotSwipeRow, PvotSwipeAction, PvotSwitchRow, PvotPreferenceRow, PvotListGroup, PvotFastScroller
│   ├── picker/   PvotClockPicker, PvotDurationPicker, PvotPickerColors, PvotPickerDefaults
│   ├── textfield/ PvotTextField, PvotTextFieldColors, PvotTextFieldDefaults
│   └── slider/   PvotSlider, PvotRangeSlider, PvotSliderColors, PvotSliderDefaults
├── navigation/   PvotNavBar, PvotNavBarItem, PvotTabItem, PvotNavBarColors, PvotNavBarSizes, PvotNavBarDefaults,
│                 PvotTopBar, PvotTopBarAction, rememberPvotTopBarCompact
└── platform/     PvotBaseActivity, enablePvotEdgeToEdge, setPvotContent

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

An activity that has to extend something else (an `AppCompatActivity`, a dialer's in-call
activity) gets the same setup from two extensions on `ComponentActivity`:

```kotlin
override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enablePvotEdgeToEdge()
    setPvotContent { PvotAppTheme { /* content */ } }
}
```

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
| primary | `#5A4BFF` | `#8577FF` |
| secondary | `#1F6FFF` | `#6FA3FF` |
| tertiary | `#E5287A` | `#FF6BA6` |
| error | `#BA1A1A` | `#FFB4AB` |
| background / surface | `#FFFFFF` | `#000000` |
| surfaceVariant | `#EEE9FF` | `#231F36` |

**Semantic roles.** Material has no success or warning role, so `PvotTheme.extendedColors` adds
them: `success`, `onSuccess`, `successContainer`, `onSuccessContainer` and the four `warning`
roles, arranged as the Material roles are (`PvotLightExtendedColors`, `PvotDarkExtendedColors`,
passed to `PvotAppTheme` as `extendedColors`). Success is an answer button or a saved state;
warning a caution that is not an error, such as a call on hold. Errors keep `colorScheme.error`.
The hues stay the same under dynamic color. Every pair meets 4.5:1.

| Role | Light | Dark |
|---|---|---|
| success | `#137A3F` | `#5FD48A` |
| successContainer | `#B9F0C8` | `#005A2C` |
| warning | `#8A5A00` | `#F5C451` |
| warningContainer | `#FFDEA6` | `#634100` |

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

**Glass:** `Modifier.pvotGlass(backdrop)` draws an element as clear glass, like a drop of water:
what lies behind it shows through frosted, and a rim along the edge bends it in from just
outside, with a slight red and blue split, a mirrored copy of what lies outside the edge and a
soft light in place of a border. `Modifier.pvotGlassRim()` adds a fine rim of light that turns
as the phone tilts and settles back once it is held still.

Only content marked with `Modifier.pvotGlassBackdrop` shows through, and it should include an opaque
background (put `.background(...)` after `.pvotGlassBackdrop(...)` in the chain), or the glass lets the
sharp content behind it show through, so mark the content behind
the glass and draw the glass over it, not inside it:

```kotlin
val backdrop = rememberPvotGlassBackdrop()
Box {
    LazyColumn(Modifier.pvotGlassBackdrop(backdrop)) { /* … */ }
    Box(
        Modifier
            .align(Alignment.TopCenter)
            .height(46.dp)
            .fillMaxWidth()
            .pvotGlass(backdrop)
            .pvotGlassRim()
    )
}
```

| Android | What the glass shows |
|---|---|
| 13+ | frost and the bending rim |
| 12 | frost |
| 11 and older | the tint alone |

**How much glass:** `LocalPvotGlassIntensity` (0 to 1, default 1) is read by every glass in the tree,
including `PvotNavBar`, `PvotTopBar`, `PvotGlassCard` and `PvotGlassIconButton`. 1 is the full
effect. `PvotGlassDefaults.FrostedIntensity` (0.1) is a frosted blur with a surface-colored tint and
no edge lens, rim or light, which is what a frosted header or bar looks like.
`PvotGlassDefaults.SolidIntensity` (0) is an opaque surface with no blur at all, which costs
nothing: the stop for a user who wants no transparency. Below `PvotGlassDefaults.LensThreshold`
the edge lens is switched off entirely, so the glass stops paying for it. For a style of your own,
`PvotGlassDefaults.style(intensity, base)` returns `base` at that intensity, and
`pvotGlassRim(alpha = intensity)` fades the rim.

Let the user choose, and let the device cap it: persist a `Float`, offer Solid / Frosted / Glass
(or a slider up to `PvotGlassDefaults.maxIntensity()`), and wrap the root in
`ProvidePvotGlassIntensity(preference)`. It provides `PvotGlassIntensity.effective(preference)`:
solid while battery saver is on or the device cannot blur, observed live, else the preference held
to the device. Apps should keep glass to a few floating surfaces (bars, sheets); at 0 the catalog
turns its cards back into plain cards.

`PvotGlassStyle` holds every value of the effect, tuned for a glass 46dp across and scaled with
the glass's shorter side; `PvotGlassDefaults` holds the tint, the rim's width,
`CapsuleRadius`, and the press and shadow values of the glass components. The glass can't show
other windows or the wallpaper.

**Materials:** `PvotGlassMaterial` says how much a glass hides what is behind it. Pick it by what
sits on the glass, not by taste: text is readable only on `Regular` (a surface tint at about half
strength, a deeper blur: bars, search fields, titled cards) or `Thick` (a nearly opaque tint, the
deepest blur: sheets and dialogs with body text); `Clear` (the faint haze, today's look) is for
controls that carry an icon alone, and for controls over a photo the screen already dims.
`PvotGlassDefaults.style(intensity, material)` is `style(intensity, base)` with the material's
tint, `PvotGlassDefaults.materialFill(material, colorScheme)`; the tint stays at every intensity.
With the tint, `onSurface` text on `Thick` keeps at least 4.5:1 and on `Regular` 3:1 over any blob
of the live background, which a unit test holds the palette to; over a photo, the photo
background's scrim is the guard.

**Budget:** glass costs with its area and with every frame of whatever moves behind it, and each
frame of the backdrop re-records every glass over it. Keep to two glass surfaces on a screen (a
bar and a sheet, a bar and a floating control), never put glass under body text or in list rows,
and keep what moves behind glass to what the user scrolls. `Modifier.pvotGlassSurface` is the recipe every glass control
is built from (press swell, shadow, hairline edge, glass, clip, rim, click); use it, or
`PvotGlassSurface`, for a control of your own rather than composing the pieces.

**Backgrounds:** the design is glass over the plain theme background: the bars and floating
controls refract the content that scrolls under them, never a decorative backdrop. A moving
gradient behind glass was the old catalog look and is not the design. `PvotPhotoBackground` draws
a photo blurred and dimmed, for a screen about that photo, such as a call; blur it once with
`preparePvotPhotoBackground` off the main thread and pass the result, so the backdrop never blurs
again. `PvotLiveBackground` and `PvotGradientBackground` stay for the catalog's old UI and demos;
a screen does not use them.

**Scaffold:** `PvotScreenScaffold` wires it all: the background and the content inside one
backdrop, with the opaque background first, and the top bar, bottom bar, floating action,
snackbar host and overlays over it, each getting the backdrop through `LocalPvotGlassBackdrop`
(which `PvotNavBar`, `PvotTopBar`, `PvotGlassCard`, `PvotGlassIconButton` and `PvotGlassSurface`
read by default). Its content receives the padding that clears the top bar; `PvotScreen` clears
the nav bar on its own. Inside the content the local is null, since glass inside its own backdrop
shows only its fill; glass that belongs with the content goes in the `overlay` slot.

```kotlin
PvotScreenScaffold(
    topBar = { PvotTopBar(title, compact = compact) },
    bottomBar = { PvotNavBar(selected, onSelect, tabs) },
) { padding ->
    PvotScreen(contentPadding = PvotScreenDefaults.contentPadding(top = padding.calculateTopPadding())) { … }
}
```

Glass belongs to the layer that floats over the content: the nav bar, buttons and cards over a
screen that scrolls under them. Mark the whole screen as the backdrop and pass it to
`PvotNavBar`, so the content shows through the bar, bent at its edge, as it scrolls by:

```kotlin
val backdrop = rememberPvotGlassBackdrop()
Scaffold(bottomBar = { PvotNavBar(selectedTab, onTabClick, tabs, backdrop = backdrop) }) {
    Screen(Modifier.fillMaxSize().pvotGlassBackdrop(backdrop))
}
```

## 6. Components

| Component | Package | What it is |
|---|---|---|
| `PvotCard` | `components` | Rounded, translucent surface; pass `onClick` to make it a button with press feedback |
| `PvotGlassCard` | `components` | Card of glass over a backdrop, with a tilt-following rim; pass `onClick` to make it a button |
| `PvotGlassIconButton` | `components` | Round glass button for an icon floating over a backdrop |
| `PvotGlassSurface`, `Modifier.pvotGlassSurface` | `components` | The recipe every glass control is built from: press swell, shadow, hairline edge, glass, clip, rim and click; for a glass control of your own |
| `PvotScreenScaffold` | `components` | The frame of a glass screen: background and content inside one backdrop, bars, floating action, snackbars and overlays over it, each glass over that backdrop by default |
| `PvotLiveBackground` | `components.background` | A slow drift of soft color: the catalog's old UI and demos, not a screen background; still when animations are off |
| `PvotGradientBackground` | `components.background` | The still version of it, for the same demos |
| `PvotPhotoBackground` | `components.background` | A photo blurred once with `preparePvotPhotoBackground` and dimmed, as the background of a screen about it |
| `PvotScreen` | `components` | `LazyColumn` with screen padding that clears the floating nav bar |
| `PvotSkeleton` | `components` | Shimmering placeholder shown while content loads |
| `PvotDialog` | `components` | Centered dialog that eases in and out over its own scrim; icon, title (optional), message, optional content and stacked `PvotDialogAction` buttons. Driven by `visible` |
| `PvotDialogContent` | `components` | The card of a `PvotDialog` alone, without window, scrim or animation, for a dialog whose window comes from elsewhere, such as an `android.app.Dialog` that keeps platform window flags |
| `PvotBottomSheet` | `components` | Modal sheet with a slim handle, optional title and scrolling content; `PvotSheetItem` rows. Driven by `visible` |
| `PvotTextField` | `components.textfield` | Soft filled field: floating label, focus ring, validation shake, animated supporting text, counter, icons, multiline |
| `PvotSlider` | `components.slider` | Minimal slider: slim track, round thumb, tap to jump, optional snapping steps |
| `PvotRangeSlider` | `components.slider` | Two-thumb version of `PvotSlider` for choosing a span |
| `PvotClockPicker` | `components.picker` | Wheel picker for a `LocalTime` (hours, minutes) |
| `PvotDurationPicker` | `components.picker` | Wheel picker for a `Duration` under 24h (hours, minutes, seconds) |
| `PvotNavBar` | `navigation` | Floating bottom bar; selected tab opens into a filled pill; dragging along the bar previews and selects; `compact` shrinks it to icons. Takes a list of `PvotTabItem` or a slot of `PvotNavBarItem`; pass a `backdrop` to make it glass |
| `PvotNavBarItem` | `navigation` | One item for the slot overload of `PvotNavBar` |
| `PvotTopBar` | `navigation` | Back button, title and actions; the large title folds into the bar when `compact`, driven by `rememberPvotTopBarCompact(listState)`; over a `backdrop` the compact bar is glass. `PvotTopBarAction` for its icon buttons |
| `PvotButton` | `components.button` | Pill button in four `PvotButtonStyle`s: `Filled`, `Tonal`, `Text`, `Destructive`; optional icon |
| `PvotFab` | `components.button` | Floating action button; with `text` it is a pill whose label folds away when `expanded` is false |
| `PvotListRow` | `components.list` | Leading slot (avatar, `PvotLetterTile`, icon), title, supporting text, trailing slot; optional `onClick`, `onLongClick` and `selected`. A slot overload takes rich title and supporting text |
| `PvotLetterTile` | `components.list` | Round tile with a name's first letter, colored by a stable key; a person icon when there is no letter |
| `PvotAvatar` | `components.list` | A person's photo from any `Painter`, or their `PvotLetterTile`; sizes from a list row to the call screen, an optional state-color ring and a badge slot. Decorative unless described |
| `PvotSwipeRow` | `components.list` | Slides sideways to reveal a start and an end `PvotSwipeAction`; removing actions slide the row off for Undo, others spring back. Offered to TalkBack as actions |
| `PvotFastScroller` | `components.list` | Over the end edge of a long list of `PvotFastScrollSection`s: a thumb while scrolling, and a drag along the edge jumps by section with a label bubble and a haptic tick. Hidden when the list fits and from TalkBack |
| `PvotSwitchRow` | `components.list` | Settings row with a switch; the whole row toggles |
| `PvotPreferenceRow` | `components.list` | Settings row with a summary, the current value and a chevron |
| `PvotCallButton` | `components.call` | Call control: round button with its label under it as one target; with `checked` a toggle that morphs to a primary rounded square, reads as a switch and ticks a haptic; a `badge` dot for more choices |
| `PvotCallActionButton` | `components.call` | Answer, decline or end: solid success or error color, never glass; a circle, or a pill with `EndWidth` |
| `PvotKeypad` | `components.keypad` | Twelve round keys reporting down and up (tones while held), with keyboard haptics, long presses and TalkBack labels such as "2, ABC" |
| `PvotKeypadDisplay` | `components.keypad` | The number above a keypad with a backspace that clears when held; a slot for an app's own text field |
| `PvotCapsule` | `components` | A pill grouping a row of floating controls; glass over a backdrop, solid otherwise. The container of the dock and of a call's controls |
| `PvotTabRow` | `components` | Text tabs with an underline that slides to the selected tab, sharing the width or scrolling; for switching what a list shows |
| `PvotIconButton` | `components.button` | A round icon button, tonal, filled or plain, at 36, 48 or 56dp; a toggle with `checked` reads as a switch and ticks its haptic |
| `PvotListGroup` | `components.list` | Rows on one card with inset hairlines between them, under a title; rows in a group use `PvotRowIconStyle.Plain` and `PvotRowChevron.Arrow` |
| `PvotLinearProgress` | `components.progress` | A thin rounded progress bar, read at draw time; with `onSeek` it takes taps and drags and TalkBack can set it |
| `PvotProportionBar` | `components.progress` | A bar of shares whose parts are tonal steps of one hue (`PvotProportionBarDefaults.tonalColors`), with legend dots in the same colors |
| `PvotSlideToAct` | `components` | A puck slid up or down to act, flung or past a distance, springing back otherwise; haptics at the threshold, the target's color past halfway, each target a TalkBack action. `PvotSlideToActDefaults.decide` is the pure decision |
| `PvotSegmentedControl` | `components` | Exclusive choices with a pill that slides to the selected one |
| `PvotSnackbarHost`, `PvotSnackbar` | `components` | Snackbars of a `SnackbarHostState`, with an action such as Undo and an optional close button |
| `PvotLoadingContent` | `core-ui` | Indeterminate progress indicator for `UiState.Loading` |
| `PvotErrorContent` | `core-ui` | Title, message and retry button for `UiState.Error` |
| `PvotEmptyContent` | `core-ui` | Message shown when there is nothing to list, with an optional icon and action |
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
| Glass card | `PvotGlassCardDefaults.CornerRadius`, `.ContentPadding`, `PvotGlassDefaults.Style` | parameters of `PvotGlassCard` |
| Glass materials | `PvotGlassDefaults.ClearStyle`, `.RegularStyle`, `.ThickStyle`; `.materialStyle(material)` (tinted, for a glass component's `style`); `.style(intensity, material)` | a `PvotGlassStyle` of your own as `base` |
| Backgrounds | `PvotBackgroundDefaults.colors()`, `.motion()`, `.PhotoScrimAlpha`, `.PhotoBlurRadius` | parameters of each background |
| Scaffold | `PvotScreenScaffoldDefaults.FloatingActionMargin` | slots of `PvotScreenScaffold` |
| Skeleton | `PvotSkeletonDefaults.Shape`, `.baseColor`, `.highlightColor` | parameters of `PvotSkeleton` |
| Screen | `PvotScreenDefaults.contentPadding()`, `.VerticalArrangement` | parameters of `PvotScreen` |
| Button colors | `PvotButtonDefaults.themeColors(style)` | `PvotButtonDefaults.colors(colorScheme, style)`, or a `PvotButtonColors` |
| FAB colors | `PvotFabDefaults.themeColors()` | `PvotFabDefaults.colors(colorScheme, ...)` |
| Top bar colors | `PvotTopBarDefaults.themeColors()`, or `.glassColors()` over a backdrop; `.windowInsets` (status bar); `.Material` | `PvotTopBarDefaults.colors(colorScheme, ...)` |
| Row colors (list, switch, preference) | `PvotListRowDefaults.themeColors()` | `PvotListRowDefaults.colors(colorScheme, ...)`; switch: `PvotSettingsRowDefaults.switchColors()` |
| Avatar | `PvotAvatarDefaults.Small` to `.Hero`, `.RingWidth` | parameters of `PvotAvatar` |
| Call buttons | `PvotCallButtonDefaults.colors()` (tonal), `.glassColors()`; `PvotCallActionButtonDefaults.colors(action)` | a `PvotCallButtonColors` or `PvotCallActionButtonColors` |
| Keypad | `PvotKeypadDefaults.keys()`, `.KeySize`; `PvotKeypadDisplayDefaults` | `keys(digits, zeroLongPressLabel, oneLongPressLabel)` or a list of `PvotKeypadKey` |
| Tab row | `PvotTabRowDefaults.TabHeight`, `.IndicatorHeight`, `.colors()` | colors, `scrollable` |
| Icon button | `PvotIconButtonDefaults.Size`, `.LargeSize`, `.SmallSize`, `.tonalColors()`, `.filledColors()`, `.plainColors()` | size, colors |
| List group | `PvotListGroupDefaults.DividerInset`, `.colors()` | `dividerInset`, colors |
| Progress | `PvotLinearProgressDefaults.Height`, `.colors()`; `PvotProportionBarDefaults.Height`, `.tonalColors(base, track)` | colors |
| Keypad keys | `PvotKeypadDefaults.flatColors()`, `.filledColors()` | colors |
| Slide to act | `PvotSlideToActDefaults.UpDistance`, `.DownDistance`, `.FlingVelocity`, `.PuckSize` | a `PvotSlideTarget` per end |
| Letter tile | `PvotLetterTileDefaults.themePalette()`, the container roles | `PvotLetterTileDefaults.palette(colorScheme)` or a list of `PvotLetterTileColors` |
| Swipe actions | `PvotSwipeRowDefaults.action(...)` (primary), `.destructiveAction(...)` (error, removes the row) | their color parameters, or `PvotSwipeAction(...)` |
| Segmented control colors | `PvotSegmentedControlDefaults.themeColors()` | `PvotSegmentedControlDefaults.colors(colorScheme, ...)` |
| Snackbar colors | `PvotSnackbarDefaults.themeColors()`, the inverse roles | `PvotSnackbarDefaults.colors(colorScheme, ...)` |

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
  `PvotDurations`, `PvotGlassStyle`. A value used by one component only belongs in that component's `*Defaults`.
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
