# Test Utilities

This package contains test utilities and helpers for the Pvot design system test suite.

## Contents

### MockFactories.kt
Factory methods for creating mock domain layer dependencies with sensible default behaviors.

**Usage:**
```kotlin
val mockEngine = MockFactories.createMockTransformEngine()
val mockCache = MockFactories.createMockTextCache()
val mockCoordinator = MockFactories.createMockAnimationCoordinator(shouldAnimate = true)
```

### TestDataGenerators.kt
Factory methods for creating valid test data objects.

**Usage:**
```kotlin
val config = TestDataGenerators.createNavBarConfig(tabCount = 3, selectedTab = 1)
val transform = TestDataGenerators.createItemTransform(rotationX = 30f)
val error = TestDataGenerators.createValidationError(parameter = "tabs")
```

### PropertyTestGenerators.kt
Kotest Arb generators for property-based testing.

**Usage:**
```kotlin
checkAll(
    PropertyTestGenerators.itemIndex(),
    PropertyTestGenerators.scrollOffset()
) { index, offset ->
    // Test with random values
}
```

### PropertyTestConfig.kt
Global configuration for property-based tests.

**Constants:**
- `DEFAULT_ITERATIONS = 100` - Standard iteration count
- `CRITICAL_ITERATIONS = 500` - For critical properties
- `QUICK_ITERATIONS = 20` - For smoke tests

## Examples

See the `examples/` package for:
- `TransformEnginePropertyTestExample.kt` - Property test examples
- `TextMeasurementCachePropertyTestExample.kt` - Caching test examples
- `ValidationEnginePropertyTestExample.kt` - Validation test examples
- `EdgeCaseTestExample.kt` - Edge case unit test examples
- `ErrorConditionTestExample.kt` - Error handling test examples
- `IntegrationTestExample.kt` - Integration test examples

## Documentation

- `PROPERTY_TEST_PATTERNS.md` - Patterns for writing property-based tests
- `UNIT_TEST_PATTERNS.md` - Patterns for writing unit tests

## Running Tests

```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "TransformEnginePropertyTestExample"

# Run with verbose output
./gradlew test --info
```
