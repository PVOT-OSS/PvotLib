# Unit Testing Patterns

This document describes common patterns for writing unit tests in the Pvot design system.

## What are Unit Tests?

Unit tests verify specific examples, edge cases, and error conditions. Unlike property tests that verify universal properties across random inputs, unit tests focus on concrete scenarios that are important to get right.

## When to Use Unit Tests vs Property Tests

**Use Unit Tests for:**
- Specific examples and edge cases
- Error message formatting
- Integration between components
- Specific user scenarios
- Regression tests for known bugs
- Boundary conditions with specific values

**Use Property Tests for:**
- Universal invariants
- Mathematical properties
- Validation logic across all inputs
- Pure functions
- Caching behavior

## Basic Pattern

```kotlin
class MyComponentTest {
    
    @Test
    fun `descriptive test name in backticks`() {
        // Arrange
        val component = MyComponent()
        val input = createTestInput()
        
        // Act
        val result = component.doSomething(input)
        
        // Assert
        assertEquals(expectedValue, result)
    }
}
```

## Pattern 1: Edge Case Testing

Test specific boundary conditions that are important.

```kotlin
@Test
fun `zero scroll offset produces center transform for first item`() {
    val transform = transformEngine.calculateTransform(
        itemIndex = 0,
        firstVisibleIndex = 0,
        scrollOffset = 0f,
        itemHeightPx = 40f,
        halfVisibleItems = 2.5f
    )
    
    // Center item should have minimal rotation
    assertTrue(transform.rotationX in -0.1f..0.1f)
    assertTrue(transform.scale in 0.99f..1.01f)
    assertTrue(transform.alpha in 0.99f..1.01f)
}
```

## Pattern 2: Error Condition Testing

Test that errors are handled correctly and produce helpful messages.

```kotlin
@Test
fun `empty tab list produces validation error`() {
    val config = createNavBarConfig(tabCount = 1)
        .copy(tabs = emptyList())
    
    val result = validationEngine.validateNavBarConfig(config)
    
    assertTrue(result is ValidationResult.Invalid)
    val errors = (result as ValidationResult.Invalid).errors
    
    // Verify error details
    assertEquals(1, errors.size)
    assertEquals("tabs", errors[0].parameter)
    assertTrue(errors[0].message.contains("empty", ignoreCase = true))
}
```

## Pattern 3: Exception Testing

Test that invalid inputs throw appropriate exceptions.

```kotlin
@Test(expected = IllegalArgumentException::class)
fun `empty wheel values throws exception`() {
    WheelConfig(
        values = emptyList(),
        initialIndex = 0
    )
}

// Or with more detailed assertion:
@Test
fun `empty wheel values throws exception with message`() {
    val exception = assertThrows(IllegalArgumentException::class.java) {
        WheelConfig(
            values = emptyList(),
            initialIndex = 0
        )
    }
    
    assertTrue(exception.message?.contains("empty") == true)
}
```

## Pattern 4: Integration Testing

Test that multiple components work together correctly.

```kotlin
@Test
fun `transform engine and scroll calculator work together`() {
    val transformEngine = TransformEngine()
    val scrollCalculator = ScrollCalculator()
    
    // Calculate visible range
    val visibleRange = scrollCalculator.calculateVisibleRange(
        firstVisibleIndex = 5,
        scrollOffset = 20f,
        itemHeightPx = 40f,
        visibleItemsCount = 5
    )
    
    // Calculate transforms for visible items
    val transforms = visibleRange.map { itemIndex ->
        transformEngine.calculateTransform(
            itemIndex = itemIndex,
            firstVisibleIndex = 5,
            scrollOffset = 20f,
            itemHeightPx = 40f,
            halfVisibleItems = 2.5f
        )
    }
    
    // Verify all transforms are valid
    transforms.forEach { transform ->
        assertTrue(transform.rotationX in -60f..60f)
        assertTrue(transform.scale in 0.7f..1f)
        assertTrue(transform.alpha in 0.3f..1f)
    }
}
```

## Pattern 5: State Transition Testing

Test that components transition between states correctly.

```kotlin
@Test
fun `animation coordinator manages animation lifecycle`() {
    val coordinator = AnimationCoordinator(maxConcurrentAnimations = 3)
    
    // Initially no animations
    assertEquals(0, coordinator.getActiveAnimationCount())
    
    // Register animations
    coordinator.registerAnimation("anim1")
    assertEquals(1, coordinator.getActiveAnimationCount())
    
    // Unregister animation
    coordinator.unregisterAnimation("anim1")
    assertEquals(0, coordinator.getActiveAnimationCount())
}
```

## Pattern 6: Multiple Error Testing

Test that multiple validation errors are all reported.

```kotlin
@Test
fun `multiple validation errors are all reported`() {
    val config = createNavBarConfig(tabCount = 1)
        .copy(
            tabs = emptyList(),
            selectedTab = -1
        )
    
    val result = validationEngine.validateNavBarConfig(config)
    
    assertTrue(result is ValidationResult.Invalid)
    val errors = (result as ValidationResult.Invalid).errors
    
    // Should have multiple errors
    assertTrue(errors.size >= 2)
    assertTrue(errors.any { it.parameter == "tabs" })
    assertTrue(errors.any { it.parameter == "selectedTab" })
}
```

## Pattern 7: Boundary Value Testing

Test specific boundary values that are important.

```kotlin
@Test
fun `first item visible range does not include negative indices`() {
    val visibleRange = scrollCalculator.calculateVisibleRange(
        firstVisibleIndex = 0,
        scrollOffset = 0f,
        itemHeightPx = 40f,
        visibleItemsCount = 5
    )
    
    // Should not include negative indices
    assertTrue(visibleRange.first >= 0)
}

@Test
fun `item at boundary is considered visible`() {
    val visibleRange = 5..10
    
    // Boundary items should be visible
    assertTrue(scrollCalculator.isItemVisible(5, visibleRange))
    assertTrue(scrollCalculator.isItemVisible(10, visibleRange))
    
    // Items outside boundary should not be visible
    assertTrue(!scrollCalculator.isItemVisible(4, visibleRange))
    assertTrue(!scrollCalculator.isItemVisible(11, visibleRange))
}
```

## Pattern 8: Format Verification Testing

Test that output formats are correct.

```kotlin
@Test
fun `error messages follow expected format`() {
    val config = createNavBarConfig(tabCount = 1)
        .copy(tabs = emptyList())
    
    val result = validationEngine.validateNavBarConfig(config)
    
    assertTrue(result is ValidationResult.Invalid)
    val errors = (result as ValidationResult.Invalid).errors
    
    errors.forEach { error ->
        // Verify format
        assertTrue(error.parameter.isNotBlank())
        assertTrue(error.message.isNotBlank())
        
        val displayString = error.toDisplayString()
        assertTrue(displayString.contains(error.parameter))
        assertTrue(displayString.contains(error.message))
    }
}
```

## Test Data Generators

Use test data generators for consistent test data:

```kotlin
@Test
fun `valid config passes validation`() {
    val config = TestDataGenerators.createNavBarConfig(
        tabCount = 3,
        selectedTab = 1
    )
    
    val result = validationEngine.validateNavBarConfig(config)
    
    assertTrue(result is ValidationResult.Valid)
}
```

## Naming Conventions

Use descriptive test names with backticks:

```kotlin
// Good
@Test
fun `empty tab list produces validation error`() { }

@Test
fun `zero scroll offset produces center transform`() { }

// Avoid
@Test
fun testEmptyTabList() { }

@Test
fun test1() { }
```

## Assertion Best Practices

1. **Use specific assertions**
```kotlin
// Good
assertEquals(5, list.size)
assertTrue(value in 0.9f..1.1f)

// Avoid
assertTrue(list.size == 5)
assertTrue(value > 0.8f && value < 1.2f)
```

2. **Provide assertion messages when helpful**
```kotlin
assertEquals("Expected 5 items", 5, list.size)
assertTrue("Value should be positive", value > 0)
```

3. **Test one thing per test**
```kotlin
// Good - focused test
@Test
fun `empty tab list produces error`() {
    // Test only empty tab list
}

// Avoid - testing multiple things
@Test
fun `validation works`() {
    // Test empty tabs, invalid index, etc.
}
```

## Test Organization

Organize tests by feature or component:

```
src/test/java/com/prauga/pvot/designsystem/
├── domain/
│   ├── transform/
│   │   └── TransformEngineTest.kt
│   ├── cache/
│   │   └── TextMeasurementCacheTest.kt
│   └── validation/
│       └── ValidationEngineTest.kt
├── components/
│   ├── navigation/
│   │   └── PvotNavBarTest.kt
│   └── picker/
│       └── WheelEngineTest.kt
└── examples/
    ├── EdgeCaseTestExample.kt
    ├── ErrorConditionTestExample.kt
    └── IntegrationTestExample.kt
```

## Running Unit Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "EdgeCaseTestExample"

# Run specific test method
./gradlew test --tests "EdgeCaseTestExample.zero scroll offset*"

# Run with verbose output
./gradlew test --info
```

## Common Pitfalls

1. **Don't test implementation details** - Test observable behavior
2. **Don't make tests brittle** - Use ranges instead of exact values when appropriate
3. **Don't ignore test failures** - Fix or update tests, don't disable them
4. **Don't write tests that depend on each other** - Each test should be independent
5. **Don't forget to test error cases** - Error handling is as important as happy paths

## Test Coverage

Aim for:
- Domain layer: 90%+ coverage
- Abstraction layer: 85%+ coverage
- Presentation layer: 70%+ coverage
- Overall: 80%+ coverage

Check coverage with:
```bash
./gradlew test jacocoTestReport
```

## Further Reading

- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)
- Property Test Patterns: `PROPERTY_TEST_PATTERNS.md`
