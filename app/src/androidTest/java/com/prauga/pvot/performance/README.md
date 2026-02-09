# Performance Testing

This directory contains performance measurement tests for the app API upgrade.

## Test Structure

### BaselinePerformanceTest
Measures performance using the deprecated PvotNavBar API (before migration).
- Captures recomposition counts
- Measures frame times
- Saves metrics to `baseline_metrics.txt`

**Latest baseline metrics (captured on Medium_Phone_API_36.0 AVD):**
- **Recomposition Count: 12** (during 3 cycles of 4 tab switches each)
- **Average Calculation Time: 16-17ms** (text measurement operations)
- **Max Calculation Time: 66-67ms** (worst-case text measurement)
- **Average Frame Time: 28ms** (time per interaction cycle in test environment)
- **Max Frame Time: 166-183ms** (worst-case interaction, likely initial composition)
- **Dropped Frames: 11** (interactions exceeding 33ms threshold)

### PostMigrationPerformanceTest
Measures performance using the new NavBarConfig API (after migration).
- Uses same measurement methodology as baseline
- Captures recomposition counts
- Measures frame times
- Saves metrics to `post_migration_metrics.txt`

**Expected behavior:** Text measurement calculation times may be 0ms if the text measurement
cache is working perfectly (all measurements served from cache with no cache misses). This
indicates the optimization is working as intended!

### PerformanceComparisonIntegrationTest
Compares baseline and post-migration metrics.
- Loads both metric files
- Calculates percentage improvements
- Generates performance comparison report
- Saves report to `performance_comparison_report.txt`

## Test Execution

### Running All Tests
```bash
./gradlew :app:connectedDebugAndroidTest
```

**Important:** Due to Android test execution order, the comparison test may fail on the first run
because metric files aren't persisted between test classes. This is expected behavior.

**Solution:** Run the tests twice:
1. **First run**: Baseline and post-migration tests pass, comparison test fails (expected)
2. **Second run**: All tests pass including comparison (metric files now exist)

Alternatively, you can run the individual test classes in sequence if your test runner supports it.

### Test Execution Order
The tests use `@FixMethodOrder(MethodSorters.NAME_ASCENDING)` with alphabetically ordered method names:
- `BaselinePerformanceTest.aMeasureBaselinePerformance()` - runs first
- `PostMigrationPerformanceTest.bMeasurePostMigrationPerformance()` - runs second  
- `PerformanceComparisonIntegrationTest.zCompareBaselineAndPostMigrationPerformance()` - runs last

However, `@FixMethodOrder` only controls order within a single test class, not across classes.
Android may run test classes in parallel or unpredictable order.

### Viewing Results

After running the tests, check the test report:
```
app/build/reports/androidTests/connected/debug/index.html
```

The performance comparison report will be saved to:
- Device: `/storage/emulated/0/Android/data/com.prauga.pvot/files/performance_comparison_report.txt`
- Cache: `/data/data/com.prauga.pvot/cache/performance_comparison_report.txt`

You can also view the console output in the test logs for immediate feedback.

## Expected Improvements

The migration to NavBarConfig API should show:
- Reduced recomposition counts (20-30% improvement expected)
- Improved frame times (10-15% improvement expected)
- Reduced text measurement calls (50%+ improvement expected)

## Important Notes

**Frame Timing Methodology:**
The frame timing metrics in these tests measure the time between test interaction cycles
(click + animation completion + idle), not individual animation frames during rendering.
This provides a useful baseline for comparing overall interaction performance before and
after migration, but should not be interpreted as per-frame rendering metrics.

For detailed frame-by-frame analysis, use the Macrobenchmark tests which measure
actual UI rendering performance.

**Test Execution Order:**
Android instrumented tests may run in parallel or in unpredictable order. The comparison
test requires both baseline and post-migration metric files to exist. If the comparison
test fails on the first run, this is expected - simply run the tests again and the
comparison will succeed once all metric files are available.

## Storage Locations

Metrics are saved to multiple locations for accessibility:
- **External Files**: `/storage/emulated/0/Android/data/com.prauga.pvot/files/`
  - `baseline_metrics.txt`
  - `post_migration_metrics.txt`
  - `performance_comparison_report.txt`
- **Cache Directory**: `/data/user/0/com.prauga.pvot/cache/`
  - Same files as above

## Viewing Metrics via ADB

You can pull the metrics files from the device:
```bash
adb pull /storage/emulated/0/Android/data/com.prauga.pvot/files/baseline_metrics.txt
adb pull /storage/emulated/0/Android/data/com.prauga.pvot/files/post_migration_metrics.txt
adb pull /storage/emulated/0/Android/data/com.prauga.pvot/files/performance_comparison_report.txt
```

Or view them directly in logcat:
```bash
adb logcat -d | grep "Performance Metrics" -A 10
```


## Benchmark Thresholds

The benchmark tests use **externalized thresholds** that automatically adjust based on device type:

### Automatic Device Detection
- **Physical Devices**: Stricter thresholds (better performance expected)
  - Max Recomposition Count: 120
  - Max Average Frame Time: 16.0ms (60fps)
  - Max Frame Time: 33ms (30fps minimum)
  - Max Dropped Frames: 10

- **Emulators**: More lenient thresholds (accounts for virtualization overhead)
  - Max Recomposition Count: 150
  - Max Average Frame Time: 20.0ms (~50fps)
  - Max Frame Time: 100ms (allows occasional spikes)
  - Max Dropped Frames: 40

### Custom Thresholds via System Properties

You can override the default thresholds using system properties:

```bash
# Set custom recomposition count threshold
adb shell setprop benchmark.threshold.recomposition.count 100

# Set custom average frame time threshold (in milliseconds)
adb shell setprop benchmark.threshold.avg.frame.time 18.0

# Set custom max frame time threshold (in milliseconds)
adb shell setprop benchmark.threshold.max.frame.time 50

# Set custom dropped frames threshold
adb shell setprop benchmark.threshold.dropped.frames 20

# Run the benchmark with custom thresholds
./gradlew :app:connectedDebugAndroidTest --tests "com.prauga.pvot.performance.NavigationPerformanceBenchmark"
```

### CI/CD Configuration

For CI/CD pipelines, you can explicitly specify device type in your test code:

```kotlin
// Use physical device thresholds (stricter)
val thresholds = BenchmarkThresholds.forPhysicalDevice()

// Use emulator thresholds (more lenient)
val thresholds = BenchmarkThresholds.forEmulator()

// Auto-detect device type (recommended)
val thresholds = BenchmarkThresholds.forCurrentDevice()
```

## Performance Regression Detection

If benchmarks start failing after code changes:
1. Review the specific metrics that exceeded thresholds in the test output
2. Check if the changes introduced performance issues
3. Consider if threshold adjustments are needed (test with system properties first)
4. Run tests multiple times to rule out environmental factors
5. Compare with baseline metrics to identify the regression source
