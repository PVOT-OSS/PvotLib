# Design System Usage Examples

This document provides practical examples of using the optimized design system components.

## Table of Contents

1. [Basic Navigation Bar](#basic-navigation-bar)
2. [Customized Navigation Bar](#customized-navigation-bar)
3. [Basic Wheel Picker](#basic-wheel-picker)
4. [Time Picker with Multiple Wheels](#time-picker-with-multiple-wheels)
5. [Duration Picker](#duration-picker)
6. [Testing Examples](#testing-examples)
7. [Performance Monitoring](#performance-monitoring)

## Basic Navigation Bar

Simple navigation bar with default styling:

```kotlin
@Composable
fun SimpleNavBar() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val config = NavBarConfig(
        tabs = listOf(
            PvotTabItem(
                iconRes = R.drawable.ic_home,
                labelRes = R.string.home,
                contentDescriptionRes = R.string.home_desc
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_search,
                labelRes = R.string.search,
                contentDescriptionRes = R.string.search_desc
            ),
            PvotTabItem(
                iconRes = R.drawable.ic_profile,
                labelRes = R.string.profile,
                contentDescriptionRes = R.string.profile_desc
            )
        ),
        selectedTab = selectedTab
    )
    
    PvotNavBar(
        config = config,
        onTabClick = { index -> selectedTab = index }
    )
}
```

## Customized Navigation Bar

Navigation bar with custom appearance and behavior:

```kotlin
@Composable
fun CustomNavBar() {
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val config = NavBarConfig(
        tabs = listOf(
            PvotTabItem(R.drawable.ic_home, R.string.home),
            PvotTabItem(R.drawable.ic_search, R.string.search),
            PvotTabItem(R.drawable.ic_profile, R.string.profile),
            PvotTabItem(R.drawable.ic_settings, R.string.settings)
        ),
        selectedTab = selectedTab,
        appearance = NavBarAppearance(
            sizes = PvotNavBarSizes(
                barHeight = 72.dp,
                expandedIconSize = 28.dp,
                collapsedIconSize = 24.dp,
                labelFontSize = 14.sp
            ),
            colors = PvotNavBarColors(
                containerColor = MaterialTheme.colorScheme.surface,
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ),
        behavior = NavBarBehavior(
            enableHapticFeedback = true,
            animationDuration = 300,
            enablePerformanceMonitoring = BuildConfig.DEBUG
        )
    )
    
    PvotNavBar(
        config = config,
        onTabClick = { index -> 
            selectedTab = index
            // Handle navigation
        },
        modifier = Modifier.fillMaxWidth()
    )
}
```

## Basic Wheel Picker

Simple wheel picker for selecting values:

```kotlin
@Composable
fun SimpleWheelPicker() {
    var selectedValue by remember { mutableIntStateOf(30) }
    
    val config = WheelConfig(
        values = (0..59).toList(),
        label = { it.toString().padStart(2, '0') },
        suffix = "min",
        initialIndex = 30
    )
    
    WheelEngine(
        config = config,
        onValueSelected = { value -> selectedValue = value },
        modifier = Modifier.padding(16.dp)
    )
}
```

## Time Picker with Multiple Wheels

Complete time picker with hours, minutes, and AM/PM:

```kotlin
@Composable
fun TimePicker(
    initialHour: Int = 12,
    initialMinute: Int = 0,
    initialAmPm: String = "AM",
    onTimeSelected: (hour: Int, minute: Int, amPm: String) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }
    var selectedAmPm by remember { mutableStateOf(initialAmPm) }
    
    // Shared animation coordinator for all wheels
    val animationCoordinator = remember { AnimationCoordinator(maxConcurrentAnimations = 3) }
    
    // Shared performance monitor
    val performanceMonitor = remember {
        PerformanceMonitor(
            enabled = BuildConfig.DEBUG,
            warningThreshold = 100
        )
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Hours wheel
        WheelEngine(
            config = WheelConfig(
                values = (1..12).toList(),
                label = { it.toString().padStart(2, '0') },
                initialIndex = initialHour - 1,
                appearance = WheelAppearance(
                    itemHeight = 48.dp,
                    visibleItemsCount = 5,
                    wheelWidth = 80.dp
                )
            ),
            onValueSelected = { hour ->
                selectedHour = hour
                onTimeSelected(selectedHour, selectedMinute, selectedAmPm)
            },
            animationCoordinator = animationCoordinator,
            performanceMonitor = performanceMonitor
        )
        
        Text(
            text = ":",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        // Minutes wheel
        WheelEngine(
            config = WheelConfig(
                values = (0..59).toList(),
                label = { it.toString().padStart(2, '0') },
                initialIndex = initialMinute,
                appearance = WheelAppearance(
                    itemHeight = 48.dp,
                    visibleItemsCount = 5,
                    wheelWidth = 80.dp
                )
            ),
            onValueSelected = { minute ->
                selectedMinute = minute
                onTimeSelected(selectedHour, selectedMinute, selectedAmPm)
            },
            animationCoordinator = animationCoordinator,
            performanceMonitor = performanceMonitor
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // AM/PM wheel
        WheelEngine(
            config = WheelConfig(
                values = listOf(0, 1),
                label = { if (it == 0) "AM" else "PM" },
                initialIndex = if (initialAmPm == "AM") 0 else 1,
                appearance = WheelAppearance(
                    itemHeight = 48.dp,
                    visibleItemsCount = 3,
                    wheelWidth = 70.dp
                ),
                behavior = WheelBehavior(
                    enable3DEffect = false // Simpler for AM/PM
                )
            ),
            onValueSelected = { value ->
                selectedAmPm = if (value == 0) "AM" else "PM"
                onTimeSelected(selectedHour, selectedMinute, selectedAmPm)
            },
            animationCoordinator = animationCoordinator,
            performanceMonitor = performanceMonitor
        )
    }
    
    // Log performance warnings in debug builds
    LaunchedEffect(Unit) {
        if (BuildConfig.DEBUG) {
            delay(10000)
            performanceMonitor.logWarnings()
        }
    }
}
```

## Duration Picker

Picker for selecting duration (hours and minutes):

```kotlin
@Composable
fun DurationPicker(
    initialHours: Int = 0,
    initialMinutes: Int = 0,
    onDurationSelected: (hours: Int, minutes: Int) -> Unit
) {
    var selectedHours by remember { mutableIntStateOf(initialHours) }
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes) }
    
    val animationCoordinator = remember { AnimationCoordinator() }
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Select Duration",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hours wheel
            WheelEngine(
                config = WheelConfig(
                    values = (0..23).toList(),
                    label = { it.toString() },
                    suffix = "h",
                    initialIndex = initialHours,
                    appearance = WheelAppearance(
                        itemHeight = 50.dp,
                        visibleItemsCount = 5,
                        wheelWidth = 100.dp
                    )
                ),
                onValueSelected = { hours ->
                    selectedHours = hours
                    onDurationSelected(selectedHours, selectedMinutes)
                },
                animationCoordinator = animationCoordinator
            )
            
            Spacer(modifier = Modifier.width(24.dp))
            
            // Minutes wheel
            WheelEngine(
                config = WheelConfig(
                    values = (0..59).toList(),
                    label = { it.toString() },
                    suffix = "m",
                    initialIndex = initialMinutes,
                    appearance = WheelAppearance(
                        itemHeight = 50.dp,
                        visibleItemsCount = 5,
                        wheelWidth = 100.dp
                    )
                ),
                onValueSelected = { minutes ->
                    selectedMinutes = minutes
                    onDurationSelected(selectedHours, selectedMinutes)
                },
                animationCoordinator = animationCoordinator
            )
        }
        
        Text(
            text = "Total: ${selectedHours}h ${selectedMinutes}m",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}
```

## Testing Examples

### Unit Test Example

```kotlin
class NavBarConfigTest {
    @Test
    fun `valid config is created successfully`() {
        val config = NavBarConfig(
            tabs = listOf(
                PvotTabItem(R.drawable.ic_home, R.string.home),
                PvotTabItem(R.drawable.ic_search, R.string.search)
            ),
            selectedTab = 0
        )
        
        assertEquals(2, config.tabs.size)
        assertEquals(0, config.selectedTab)
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `empty tabs throws exception`() {
        NavBarConfig(
            tabs = emptyList(),
            selectedTab = 0
        )
    }
    
    @Test(expected = IllegalArgumentException::class)
    fun `out of bounds selection throws exception`() {
        NavBarConfig(
            tabs = listOf(PvotTabItem(0, 0)),
            selectedTab = 5
        )
    }
}
```

### Composable Test Example

```kotlin
class PvotNavBarTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    
    @Test
    fun `nav bar renders all tabs`() {
        val config = NavBarConfig(
            tabs = listOf(
                PvotTabItem(R.drawable.ic_home, R.string.home),
                PvotTabItem(R.drawable.ic_search, R.string.search),
                PvotTabItem(R.drawable.ic_profile, R.string.profile)
            ),
            selectedTab = 0
        )
        
        composeTestRule.setContent {
            PvotNavBar(
                config = config,
                onTabClick = {}
            )
        }
        
        // Verify all tabs are rendered
        composeTestRule.onNodeWithContentDescription("Home").assertExists()
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
        composeTestRule.onNodeWithContentDescription("Profile").assertExists()
    }
    
    @Test
    fun `clicking tab triggers callback`() {
        var clickedIndex = -1
        val config = NavBarConfig(
            tabs = listOf(
                PvotTabItem(R.drawable.ic_home, R.string.home),
                PvotTabItem(R.drawable.ic_search, R.string.search)
            ),
            selectedTab = 0
        )
        
        composeTestRule.setContent {
            PvotNavBar(
                config = config,
                onTabClick = { index -> clickedIndex = index }
            )
        }
        
        composeTestRule.onNodeWithContentDescription("Search").performClick()
        assertEquals(1, clickedIndex)
    }
}
```

### Integration Test Example

```kotlin
class DesignSystemIntegrationTest {
    @Test
    fun `all components work together`() {
        val transformEngine = TransformEngine()
        val scrollCalculator = ScrollCalculator()
        val textCache = TextMeasurementCache()
        val animationCoordinator = AnimationCoordinator()
        val validationEngine = ValidationEngine()
        
        // Create configurations
        val navBarConfig = NavBarConfig(
            tabs = listOf(
                PvotTabItem(0, 0),
                PvotTabItem(1, 1)
            ),
            selectedTab = 0
        )
        
        val wheelConfig = WheelConfig(
            values = (0..59).toList(),
            initialIndex = 30
        )
        
        // Validate configurations
        val navBarValidation = validationEngine.validateNavBarConfig(navBarConfig)
        assertTrue(navBarValidation is ValidationResult.Valid)
        
        val wheelValidation = validationEngine.validateWheelConfig(wheelConfig)
        assertTrue(wheelValidation is ValidationResult.Valid)
        
        // Test transform calculations
        val transform = transformEngine.calculateTransform(
            itemIndex = 30,
            firstVisibleIndex = 28,
            scrollOffset = 0f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
        
        assertTrue(transform.rotationX in -60f..60f)
        assertTrue(transform.scale in 0.7f..1f)
        assertTrue(transform.alpha in 0.3f..1f)
    }
}
```

## Performance Monitoring

### Enable Performance Monitoring

```kotlin
@Composable
fun MonitoredComponents() {
    val performanceMonitor = remember {
        PerformanceMonitor(
            enabled = BuildConfig.DEBUG,
            warningThreshold = 100
        )
    }
    
    // Use in components
    WheelEngine(
        config = wheelConfig,
        onValueSelected = { },
        performanceMonitor = performanceMonitor
    )
    
    // Log warnings periodically
    LaunchedEffect(Unit) {
        while (true) {
            delay(10000)
            performanceMonitor.logWarnings()
            
            // Get specific metrics
            val metrics = performanceMonitor.getMetrics("WheelEngine")
            Log.d("Performance", "Recompositions: ${metrics.recompositionCount}")
        }
    }
}
```

### Custom Performance Tracking

```kotlin
@Composable
fun CustomPerformanceTracking() {
    val performanceMonitor = remember { PerformanceMonitor(enabled = true) }
    
    DisposableEffect(Unit) {
        performanceMonitor.recordRecomposition("MyComponent")
        
        onDispose {
            val metrics = performanceMonitor.getMetrics("MyComponent")
            if (metrics.recompositionCount > 50) {
                Log.w("Performance", "High recomposition count: ${metrics.recompositionCount}")
            }
        }
    }
    
    // Component content
}
```

## Advanced Customization

### Custom Transform Engine

```kotlin
class CustomTransformEngine : ITransformEngine {
    override fun calculateTransform(
        itemIndex: Int,
        firstVisibleIndex: Int,
        scrollOffset: Float,
        itemHeightPx: Float,
        halfVisibleItems: Float
    ): ItemTransform {
        // Custom transformation logic
        val distanceFromCenter = (itemIndex - firstVisibleIndex) - (scrollOffset / itemHeightPx)
        val normalizedDistance = (distanceFromCenter / halfVisibleItems).coerceIn(-1f, 1f)
        
        return ItemTransform(
            rotationX = normalizedDistance * 45f, // Less rotation
            scale = 1f - (abs(normalizedDistance) * 0.2f), // Less scaling
            alpha = 1f - (abs(normalizedDistance) * 0.5f) // More fade
        )
    }
}

@Composable
fun CustomWheelPicker() {
    val customTransformEngine = remember { CustomTransformEngine() }
    
    WheelEngine(
        config = wheelConfig,
        onValueSelected = { },
        transformEngine = customTransformEngine
    )
}
```

### Shared Resources

```kotlin
@Composable
fun SharedResourcesExample() {
    // Share cache across multiple nav bars
    val textCache = remember { TextMeasurementCache(maxSize = 50) }
    
    // Share animation coordinator
    val animationCoordinator = remember { AnimationCoordinator(maxConcurrentAnimations = 10) }
    
    Column {
        PvotNavBar(
            config = config1,
            onTabClick = {},
            textCache = textCache,
            animationCoordinator = animationCoordinator
        )
        
        PvotNavBar(
            config = config2,
            onTabClick = {},
            textCache = textCache, // Reuse cache
            animationCoordinator = animationCoordinator // Reuse coordinator
        )
    }
}
```

## Best Practices

1. **Reuse configuration objects** when possible
2. **Share caches and coordinators** across components
3. **Enable performance monitoring** in debug builds only
4. **Validate configurations** early in development
5. **Use stable keys** for LazyColumn items
6. **Monitor recomposition counts** during development
7. **Test with real devices** for accurate performance metrics

## Troubleshooting

See [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) for common issues and solutions.
