# Building a new app on PvotLib

A playbook for starting a Pvot app from nothing. The design system decides the look, motion and
interaction; you decide what the app does. Read [CLAUDE.md](../CLAUDE.md) first for the rules that
apply everywhere. This file is the process and the skeleton.

## Principles

1. **Start from the design system, not from Material defaults.** The theme, scaffold and every
   control come from PvotLib from the first commit.
2. **One screen at a time, always runnable.** Build and look at each screen on the device before
   starting the next.
3. **The library is the source of truth.** Missing component or token: propose it for PvotLib
   instead of working around it in the app.
4. **Never commit or push.** Tell the user when a good commit point is reached.

## 1. Project setup

A multi-module Android app is the default shape: `:app` plus feature or data modules as it grows. Keep
it a single `:app` module until there is a reason to split.

- Min SDK 27 (the library's floor), compile and target the current SDK, Kotlin and Compose from the
  library's version catalog as a reference (`gradle/libs.versions.toml`).
- Explicit API mode and `pvot_` resource prefixes apply to libraries; an app does not need them, but
  does follow every other rule in CLAUDE.md.
- Add the dependency:

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://pvot-oss.github.io/PvotLib/maven") {
            content { includeGroup("com.prauga.pvot") }
        }
    }
}

// app/build.gradle.kts
dependencies {
    implementation(libs.pvot.design.system)
    implementation(libs.pvot.core.ui)
}
```

Components added after 0.1.0 (dialog, sheet, slider, text field, new nav bar behavior) need a release
that contains them or a source include (`include(":design-system")`). Check CHANGELOG.

- App-level notices: add `THIRD_PARTY_NOTICES.md` crediting Tabler Icons (MIT) once icons are used,
  and keep the SPDX header convention (`SPDX-License-Identifier: Apache-2.0`) on every source file.

## 2. The skeleton

```kotlin
class MainActivity : PvotBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPvotContent {
            PvotAppTheme {
                AppScaffold()
            }
        }
    }
}
```

```kotlin
@Composable
fun AppScaffold() {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var compact by remember { mutableStateOf(false) }
    val scroll = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (available.y < -4f) compact = true else if (available.y > 4f) compact = false
                return Offset.Zero
            }
        }
    }
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            PvotNavBar(
                selectedTab = tab,
                onTabClick = { tab = it; compact = false },
                tabs = tabs,
                compact = compact
            )
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .nestedScroll(scroll)
                .background(MaterialTheme.colorScheme.background)
                .padding(top = padding.calculateTopPadding())
        ) {
            when (tab) { /* one screen per tab */ }
        }
    }
}
```

Single-purpose apps with no tabs skip the nav bar and put the main task on one `PvotScreen`.

## 3. A screen

Every screen follows the same shape: a title, content in `PvotScreen`, and all three states.

```kotlin
@Composable
fun ThingsScreen(viewModel: ThingsViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        UiState.Loading -> PvotScreen { items(5) { ThingSkeleton() } }
        is UiState.Error -> PvotErrorContent(
            title = stringResource(R.string.things_error_title),
            message = s.message,
            onRetry = viewModel::retry,
        )
        is UiState.Success -> if (s.data.isEmpty()) {
            PvotEmptyContent(stringResource(R.string.things_empty))
        } else {
            PvotScreen {
                item { Text(stringResource(R.string.things_title), style = MaterialTheme.typography.titleLarge) }
                itemsIndexed(s.data, key = { _, t -> t.id }) { index, thing ->
                    ThingCard(thing, Modifier.animateItem().pvotReveal(index))
                }
            }
        }
    }
}
```

- State is `UiState<T>` from `core-ui`; the ViewModel exposes it, the screen only draws it.
- Loading uses `PvotSkeleton` shaped like the content, not a spinner, whenever the layout is known.
- Lists use stable `key`s so `animateItem()` works.
- Cross-fade or animate between states; never let content snap.

## 4. Building blocks

| For | Use |
|---|---|
| Grouped content | `PvotCard` (`onClick` makes it tappable with press feedback) |
| A form | `PvotTextField` per field, validation through `isError` and `supportingText`, `maxLength` for counters |
| A value in a range | `PvotSlider` (`steps` for discrete), `PvotRangeSlider` for spans |
| A decision | `PvotDialog` with `PvotDialogAction`s, driven by a `visible` flag |
| Choosing from actions | `PvotBottomSheet` with `PvotSheetItem` rows |
| Time input | `PvotClockPicker`, `PvotDurationPicker` |
| Titles | `PvotSectionHeader`, or `titleLarge` for a screen title |
| Switches, chips, buttons, lists, tabs | Material 3, default parameters, no restyling (library versions are planned) |

Dialogs and sheets:

```kotlin
var confirm by remember { mutableStateOf(false) }
PvotDialog(
    visible = confirm,
    onDismissRequest = { confirm = false },
    title = stringResource(R.string.delete_title),
    message = stringResource(R.string.delete_message),
    destructive = true,
    icon = { Icon(painterResource(R.drawable.ic_trash), null) },
) {
    PvotDialogAction(stringResource(R.string.delete), onClick = ::delete, style = PvotDialogActionStyle.Destructive)
    PvotDialogAction(stringResource(R.string.cancel), onClick = { confirm = false })
}
```

Where an action can be undone (delete a row, archive), do it immediately and offer Undo in a snackbar
instead of asking first.

## 5. Spacing, type, color, motion

- Screen padding `PvotSpacing.L`; items `M` apart; sections about 28dp; inside a card pad `L`.
- Text styles come from `MaterialTheme.typography`: `titleLarge` screen titles, `titleMedium` card
  titles, `bodyLarge`/`bodyMedium` content, `bodySmall` and `labelSmall` secondary. Secondary text is
  `onSurfaceVariant`.
- Primary actions use `primary`; the destructive path uses `error`. One primary action per screen.
- Motion: list entrance `pvotReveal(index)`; press `pvotPressScale` (built into `PvotCard(onClick)`,
  sheet items and dialog actions); state changes ease with `PvotMotion.Settle` and `PvotDurations`.
  Never invent durations or springs; never animate layout with an overshoot.

## 6. Interaction checklist

Run through this for every screen before calling it done:

- Whole rows and surfaces are tappable, not just their labels. Targets are 48dp or more.
- Everything tappable gives feedback (press scale, ripple on custom rows).
- Text fields show the keyboard without hiding the field (`imePadding`), and the IME action does
  something sensible (Search, Next, Done).
- Lists keep scroll position across navigation and rotation.
- Destructive actions are guarded or undoable.
- Drag is supported where natural: nav bar, sliders, sheets.
- Haptics on discrete ticks (stepped slider, selection), not on every tap.
- Back goes where the user expects and closes dialogs and sheets first.

## 7. Icons and resources

- Tabler outline icons as vector drawables (see REVAMP_EXISTING_APP.md section 5 for the conversion
  rules). Name them `ic_<name>`. Tint through `LocalContentColor`.
- Every user-visible string is a string resource. Icon-only controls have content descriptions.
- Keep drawables stroke-based at 2dp so custom glyphs sit with Tabler's.

## 8. Accessibility

Content descriptions on icon-only controls, labels on fields, sufficient contrast (the palette is
tuned for it; do not override colors), no information carried by color alone, and nothing that needs
a precise gesture without an alternative (sliders also respond to accessibility `setProgress`).

## 9. Verify, then hand off

Build and install; look at every screen in dark and light, at large font size, in landscape if the app
supports it; exercise each interaction in the checklist. Then tell the user:

1. What exists and what was verified on the device.
2. What was **not** verified.
3. **Library gaps** hit while building, each with a proposal.
4. A suggested commit point. Do not run git commit or push.

## Common mistakes

- Starting from the Android Studio template's Material theme and bolting Pvot on later.
- Using a Material `Slider`, `OutlinedTextField`, `AlertDialog` or `ModalBottomSheet`.
- A spinner where a skeleton fits, or a blank screen for an empty or failed state.
- Hex colors and raw `dp`/`sp` instead of tokens.
- Nesting a `PvotScreen` inside another vertical scroll container.
- Conditionally composing `PvotDialog` or `PvotBottomSheet` instead of passing `visible`.
- Hardcoded strings.
