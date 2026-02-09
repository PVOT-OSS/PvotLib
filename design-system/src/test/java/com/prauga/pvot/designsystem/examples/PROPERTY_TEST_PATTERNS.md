# Property-Based Testing Patterns

This document describes common patterns for writing property-based tests in the Pvot design system.

## What is Property-Based Testing?

Property-based testing (PBT) validates that certain properties hold true for ALL valid inputs, not just specific examples. Instead of writing individual test cases, you define properties that should always be true and let the testing framework generate hundreds of random inputs to verify the property.

## When to Use Property Tests vs Unit Tests

**Use Property Tests for:**
- Universal invariants (e.g., "cache always returns same result for same input")
- Mathematical properties (e.g., "rotation is always within bounds")
- Validation logic (e.g., "invalid config is always rejected")
- Pure functions (e.g., "same input always produces same output")
- Boundary conditions across ranges

**Use Unit Tests for:**
- Specific examples and edge cases
- Integration between components
- Error message formatting
- Specific user scenarios
- Regression tests for known bugs

## Basic Pattern

```kotlin
class MyComponentTest : StringSpec({
    
    "Property: Description of what should always be true".config(
        invocations = PropertyTestConfig.DEFAULT_ITERATIONS
    ) {
        checkAll(
            PropertyTestGenerators.someInput(),
            PropertyTestGenerators.anotherInput()
        ) { input1, input2 ->
            // Arrange
            val component = MyComponent()
            
            // Act
            val result = component.doSomething(input1, input2)
            
            // Assert
            result shouldSatisfy { /* property check */ }
        }
    }
})
```

## Pattern 1: Pure Function Testing

Test that functions are referentially transparent (same input → same output).

```kotlin
"Function is pure (same inputs produce same outputs)".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(
        PropertyTestGenerators.itemIndex(),
        PropertyTestGenerators.scrollOffset()
    ) { index, offset ->
        val result1 = engine.calculate(index, offset)
        val result2 = engine.calculate(index, offset)
        
        result1 shouldBe result2
    }
}
```

## Pattern 2: Caching Behavior

Test that caching works correctly across all inputs.

```kotlin
"Cache returns same instance for same key".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(
        PropertyTestGenerators.labelText(),
        PropertyTestGenerators.textStyle()
    ) { text, style ->
        val cache = TextMeasurementCache()
        val measurer = mock<TextMeasurer>()
        
        whenever(measurer.measure(any(), any())).thenReturn(mockResult)
        
        val result1 = cache.measure(text, style, measurer)
        val result2 = cache.measure(text, style, measurer)
        
        result1 shouldBe result2
        verify(measurer, times(1)).measure(any(), any())
    }
}
```

## Pattern 3: Validation Testing

Test that validation rules apply consistently.

```kotlin
"Valid config always passes validation".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(PropertyTestGenerators.navBarConfig()) { config ->
        val result = engine.validateNavBarConfig(config)
        result shouldBe ValidationResult.Valid
    }
}

"Invalid config always fails with descriptive error".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(Arb.int(min = 1, max = 5)) { tabCount ->
        val config = createConfig(tabCount).copy(selectedTab = -1)
        
        val result = engine.validateNavBarConfig(config)
        
        result.shouldBeInstanceOf<ValidationResult.Invalid>()
        val errors = (result as ValidationResult.Invalid).errors
        errors.any { it.parameter == "selectedTab" } shouldBe true
    }
}
```

## Pattern 4: Boundary Testing

Test that values stay within valid ranges.

```kotlin
"Transform values are always within valid ranges".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(
        PropertyTestGenerators.itemIndex(),
        PropertyTestGenerators.scrollOffset()
    ) { index, offset ->
        val transform = engine.calculateTransform(index, offset)
        
        transform.rotationX.shouldBeBetween(-60f, 60f, 0.01f)
        transform.scale.shouldBeBetween(0.7f, 1f, 0.01f)
        transform.alpha.shouldBeBetween(0.3f, 1f, 0.01f)
    }
}
```

## Pattern 5: Invariant Testing

Test that certain relationships always hold.

```kotlin
"Cache size never exceeds maximum".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    val maxSize = 10
    val cache = TextMeasurementCache(maxSize = maxSize)
    
    checkAll(Arb.list(PropertyTestGenerators.labelText(), 1..20)) { texts ->
        texts.forEach { text ->
            cache.measure(text, style, measurer)
        }
        
        // Cache size should never exceed max
        cache.size() shouldBeLessThanOrEqual maxSize
    }
}
```

## Pattern 6: Idempotency Testing

Test that operations can be repeated without changing the result.

```kotlin
"Clearing cache twice has same effect as clearing once".config(
    invocations = PropertyTestConfig.DEFAULT_ITERATIONS
) {
    checkAll(PropertyTestGenerators.labelText()) { text ->
        val cache1 = TextMeasurementCache()
        val cache2 = TextMeasurementCache()
        
        cache1.measure(text, style, measurer)
        cache2.measure(text, style, measurer)
        
        cache1.clear()
        
        cache2.clear()
        cache2.clear()
        
        // Both caches should behave identically
        cache1.measure(text, style, measurer)
        cache2.measure(text, style, measurer)
        
        verify(measurer, times(3)).measure(text, style)
    }
}
```

## Custom Generators

Create custom generators for domain types:

```kotlin
object PropertyTestGenerators {
    fun wheelConfig(): Arb<WheelConfig> = arbitrary {
        val values = wheelValues().bind()
        val initialIndex = Arb.int(min = 0, max = values.size - 1).bind()
        
        WheelConfig(
            values = values,
            initialIndex = initialIndex,
            label = { it.toString() },
            suffix = ""
        )
    }
}
```

## Iteration Counts

Use appropriate iteration counts based on test criticality:

```kotlin
// Default for most properties
invocations = PropertyTestConfig.DEFAULT_ITERATIONS  // 100

// Critical properties that must never fail
invocations = PropertyTestConfig.CRITICAL_ITERATIONS  // 500

// Quick smoke tests during development
invocations = PropertyTestConfig.QUICK_ITERATIONS  // 20
```

## Documenting Properties

Always reference the design document property:

```kotlin
/**
 * Property 4: Text Measurement Caching
 * 
 * Feature: design-system-optimization
 * Property: For any label text and text style combination, measuring
 * the same combination multiple times should return the cached result
 * after the first measurement.
 * 
 */
"Property 4: Same text and style returns cached result".config(...)
```

## Common Pitfalls

1. **Don't test implementation details** - Test observable behavior, not internal state
2. **Don't make properties too specific** - Properties should be universal, not edge cases
3. **Don't ignore shrinking** - When a property fails, Kotest will try to find the minimal failing case
4. **Don't forget to test invalid inputs** - Generate both valid and invalid data
5. **Don't mix property and unit tests** - Keep them separate for clarity

## Running Property Tests

```bash
# Run all tests
./gradlew test

# Run only property tests
./gradlew test --tests "*PropertyTestExample"

# Run with verbose output
./gradlew test --info
```

## Further Reading

- [Kotest Property Testing Documentation](https://kotest.io/docs/proptest/property-based-testing.html)
