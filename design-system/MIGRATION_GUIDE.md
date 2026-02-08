# Design System Optimization Migration Guide

This guide helps you migrate from the old design system APIs to the new optimized versions.

## Overview

The design system has been refactored to improve performance, maintainability, and testability. All old APIs remain functional with deprecation warnings, so you can migrate at your own pace.

## Breaking Changes

**None** - All changes are backward compatible. Old APIs are deprecated but still functional.

## API Changes

### 1. PvotNavBar Configuration

#### Old API (Deprecated)

```kotlin
PvotNavBar(
    tabs = listOf(
        PvotTabItem(R.drawable.ic_home, R.string.home),
        PvotTabItem(R.drawable.ic_search, R.string.search),
        PvotTabItem(R.drawable.ic_profile, R.string.profile)
    ),
    selectedTab = 0,
    onTabClick = { index -> /* handle click */ },
    sizes = PvotNavBarSizes(),
    colors = PvotNavBarColors(),
    modifier = Modifier
)
```

#### New API (Recommended)

```kotlin
PvotNavBar(
    config = NavBarConfig(
        tabs = listOf(
            PvotTabItem(R.drawable.ic_home, R.string.home),
            PvotTabItem(R.drawable.ic_search, R.string.search),
            PvotTabItem(R.drawable.ic_profile, R.string.profile)
        ),
        selectedTab = 0,
        appearance = NavBarAppearance(
            sizes = PvotNavBarSizes(),
            colors = PvotNavBarColors()
        ),
        behavior = NavBarBehavior(
            enableHapticFeedback = true,
            animationDuration = 240
        )
    ),
    onTabClick = { index -> /* handle click */ },
    modifier = Modifier
)
```

**Benefits**:
- Cleaner API with fewer top-level parameters
- Grouped related configuration
- Better validation and error messages
- Performance optimizations enabled by default

### 2. WheelEngine Configuration

#### Old API (Deprecated)

```kotlin
WheelEngine(
    values = (0..59).toList(),
    label = { it.toString() },
    suffix = "min",
    initialIndex = 0,
    onValueSelected = { value -> /* handle selection */ },
    itemHeight = 40.dp,
    visibleItemsCount = 5,
    wheelWidth = 110.dp,
    colors = PvotPickerColors(),
    enableHapticFeedback = true,
    modifier = Modifier
)
```

#### New API (Recommended)

```kotlin
WheelEngine(
    config = WheelConfig(
        values = (0..59).toList(),
        label = { it.toString() },
        suffix = "min",
        initialIndex = 0,
        appearance = WheelAppearance(
            itemHeight = 40.dp,
            visibleItemsCount = 5,
            wheelWidth = 110.dp,
            colors = PvotPickerColors()
        ),
        behavior = WheelBehavior(
            enableHapticFeedback = true,
            enable3DEffect = true
        )
    ),
    onValueSelected = { value -> /* handle selection */ },
    modifier = Modifier
)
```

**Benefits**:
- Configuration validation at creation time
- Clearer separation of appearance and behavior
- Performance optimizations built-in
- Better error messages for invalid configurations

### 3. Advanced Usage with Dependency Injection

The new APIs support dependency injection for testing and customization:

```kotlin
// Custom text cache for testing
val textCache = TextMeasurementCache(maxSize = 50)

// Custom animation coordinator
val animationCoordinator = AnimationCoordinator(maxConcurrentAnimations = 8)

// Custom validation engine
val validationEngine = ValidationEngine()

PvotNavBar(
    config = navBarConfig,
    onTabClick = { },
    textCache = textCache,
    animationCoordinator = animationCoordinator,
    validationEngine = validationEngine
)
```

```kotlin
// Custom transform engine
val transformEngine = TransformEngine(
    maxRotationDegrees = 45f,
    minScale = 0.8f
)

// Custom scroll calculator
val scrollCalculator = ScrollCalculator()

// Performance monitoring
val performanceMonitor = PerformanceMonitor(enabled = true)

WheelEngine(
    config = wheelConfig,
    onValueSelected = { },
    transformEngine = transformEngine,
    scrollCalculator = scrollCalculator,
    performanceMonitor = performanceMonitor
)
```

## Migration Steps

### Step 1: Update Dependencies

Ensure you're using the latest version of the design system:

```kotlin
dependencies {
    implementation("com.prauga.pvot:design-system:2.0.0")
}
```

### Step 2: Update PvotNavBar Usages

Find all PvotNavBar usages:

```bash
# Search for old API usage
grep -r "PvotNavBar(" --include="*.kt"
```

Update each usage:

```kotlin
// Before
PvotNavBar(
    tabs = tabs,
    selectedTab = selectedTab,
    onTabClick = onTabClick,
    sizes = sizes,
    colors = colors
)

// After
PvotNavBar(
    config = NavBarConfig(
        tabs = tabs,
        selectedTab = selectedTab,
        appearance = NavBarAppearance(
            sizes = sizes,
            colors = colors
        )
    ),
    onTabClick = onTabClick
)
```

### Step 3: Update WheelEngine Usages

Find all WheelEngine usages and update similarly.

### Step 4: Update Tests

Update test code to use new APIs:

```kotlin
// Before
@Test
fun testNavBar() {
    composeTestRule.setContent {
        PvotNavBar(
            tabs = testTabs,
            selectedTab = 0,
            onTabClick = {}
        )
    }
}

// After
@Test
fun testNavBar() {
    val config = NavBarConfig(
        tabs = testTabs,
        selectedTab = 0
    )
    
    composeTestRule.setContent {
        PvotNavBar(
            config = config,
            onTabClick = {}
        )
    }
}
```

### Step 5: Enable Performance Monitoring (Optional)

For debugging and optimization:

```kotlin
val performanceConfig = PerformanceConfig(
    enablePerformanceMonitoring = BuildConfig.DEBUG,
    enableCaching = true,
    recompositionWarningThreshold = 100
)

// Use in your components
WheelEngine(
    config = wheelConfig,
    onValueSelected = { },
    performanceMonitor = PerformanceMonitor(
        enabled = performanceConfig.enablePerformanceMonitoring
    )
)
```

## Configuration Validation

The new APIs validate configuration at creation time:

```kotlin
// This will throw IllegalArgumentException with clear message
val invalidConfig = NavBarConfig(
    tabs = emptyList(), // Error: Tab list cannot be empty
    selectedTab = 0
)

// This will throw IllegalArgumentException
val invalidWheelConfig = WheelConfig(
    values = listOf(1, 2, 3),
    initialIndex = 10 // Error: Initial index out of bounds (0..2)
)
```

Handle validation errors:

```kotlin
try {
    val config = NavBarConfig(
        tabs = tabs,
        selectedTab = selectedIndex
    )
    
    PvotNavBar(config = config, onTabClick = {})
} catch (e: IllegalArgumentException) {
    // Handle invalid configuration
    Log.e("NavBar", "Invalid configuration: ${e.message}")
}
```

## Performance Best Practices

### 1. Reuse Configuration Objects

```kotlin
// Good: Create once, reuse
val navBarConfig = remember {
    NavBarConfig(
        tabs = tabs,
        selectedTab = 0
    )
}

PvotNavBar(
    config = navBarConfig.copy(selectedTab = currentTab),
    onTabClick = { }
)
```

### 2. Use Stable Keys

```kotlin
// Good: Stable keys for LazyColumn items
LazyColumn {
    items(
        count = values.size,
        key = { index -> values[index] }
    ) { index ->
        // Item content
    }
}
```

### 3. Leverage Caching

```kotlin
// Good: Share cache across components
val textCache = remember { TextMeasurementCache() }

PvotNavBar(
    config = config1,
    onTabClick = {},
    textCache = textCache
)

PvotNavBar(
    config = config2,
    onTabClick = {},
    textCache = textCache // Reuse cache
)
```

### 4. Monitor Performance

```kotlin
// Development builds only
val performanceMonitor = remember {
    PerformanceMonitor(
        enabled = BuildConfig.DEBUG,
        warningThreshold = 100
    )
}

LaunchedEffect(Unit) {
    delay(10000)
    performanceMonitor.logWarnings()
}
```

## Common Issues and Solutions

### Issue: Deprecation Warnings

**Solution**: Update to new API as shown above. Old API will be removed in version 3.0.

### Issue: Configuration Validation Errors

**Solution**: Check error message for specific parameter and expected values.

```kotlin
// Error message example:
// Invalid NavBarConfig:
// selectedTab: Selected tab index out of bounds (expected: 0 to 2)
```

### Issue: Performance Not Improved

**Solution**: Ensure you're using the new API and caching is enabled:

```kotlin
val config = NavBarConfig(
    tabs = tabs,
    selectedTab = selectedTab,
    behavior = NavBarBehavior(
        enablePerformanceMonitoring = true
    )
)
```

### Issue: Tests Failing After Migration

**Solution**: Update test utilities to use new APIs:

```kotlin
// Use TestDataGenerators
val config = TestDataGenerators.createNavBarConfig(
    tabCount = 3,
    selectedTab = 0
)
```

## Rollback Plan

If you encounter issues, you can continue using the old API:

```kotlin
// Old API still works (with deprecation warning)
@Suppress("DEPRECATION")
PvotNavBar(
    tabs = tabs,
    selectedTab = selectedTab,
    onTabClick = onTabClick
)
```

## Support

For issues or questions:
- Check the [Performance Validation Guide](PERFORMANCE_VALIDATION_GUIDE.md)
- Review the [Design Document](.kiro/specs/design-system-optimization/design.md)
- File an issue on the project repository

## Timeline

- **Version 2.0**: New APIs introduced, old APIs deprecated
- **Version 2.x**: Both APIs supported
- **Version 3.0**: Old APIs removed (planned for Q3 2026)

Migrate before version 3.0 to avoid breaking changes.
