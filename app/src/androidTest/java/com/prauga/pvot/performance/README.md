# Performance Testing

This directory contains performance measurement tests for the app API upgrade.

## Baseline Performance Test

The `BaselinePerformanceTest` captures performance metrics using the deprecated PvotNavBar API before migration.

### Running the Test

```bash
./gradlew :app:connectedDebugAndroidTest
```

### Captured Metrics

The test measures:
- **Recomposition Count**: Number of times PvotNavBar recomposes during tab switches
- **Average Calculation Time**: Average time spent in explicitly tracked calculations (ms)
- **Max Calculation Time**: Maximum time spent in a single tracked calculation (ms)

### Baseline Results

Latest baseline metrics (captured on Medium_Phone_API_36.0 AVD):
- **Recomposition Count: 12** (during 3 cycles of 4 tab switches each)
- **Average Calculation Time: 16-17ms** (text measurement operations)
- **Max Calculation Time: 66-67ms** (worst-case text measurement)
- **Average Frame Time: 28ms** (time per interaction cycle in test environment)
- **Max Frame Time: 166-183ms** (worst-case interaction, likely initial composition)
- **Dropped Frames: 11** (interactions exceeding 33ms threshold)
- **Test Duration: ~4.2 seconds**

**Performance Analysis:**
The baseline reveals performance characteristics that can be improved:
- **Text Measurement**: Average 16-17ms per measurement is expensive, especially when repeated
- **Interaction Timing**: Average 28ms per interaction cycle shows room for optimization
- **Slow Interactions**: 11 out of 12 interactions exceeded the 33ms threshold
- **Worst Case**: Maximum interaction time of 166-183ms indicates expensive initial composition
- **Calculation Impact**: Max calculation time of 66-67ms directly contributes to slow interactions

These metrics establish a clear baseline for measuring the impact of the API migration optimizations:
- Text measurement caching should reduce average calculation time significantly
- Animation coordination should improve interaction consistency
- Overall interaction times should improve with reduced recomposition overhead

**Important Note on Frame Timing:**
The frame timing metrics in this test measure the time between test interaction cycles (click + animation completion + idle), not individual animation frames during rendering. This provides a useful baseline for comparing overall interaction performance before and after migration, but should not be interpreted as per-frame rendering metrics. For detailed frame-by-frame analysis, use the Macrobenchmark tests (Task 9) which measure actual UI rendering performance.

**Test Methodology:**
- 3 complete cycles through all 4 tabs (Home → Apps → Catalog → About)
- Total of 12 tab clicks
- Uses deprecated PvotNavBar API with performance monitoring enabled
- Frame timing measured using Android Choreographer API
  - **Note**: Measures time between test interaction cycles (click + animation + idle), not individual animation frames
  - Represents overall interaction performance in test environment
- Text measurement timing tracked via TextMeasurementCache instrumentation
- Dropped frame threshold: 33ms (2x the 60fps target, adjusted for test environment overhead)

### Storage Locations

Metrics are saved to:
- External: `/storage/emulated/0/Android/data/com.prauga.pvot/files/baseline_metrics.txt`
- Cache: `/data/user/0/com.prauga.pvot/cache/baseline_metrics.txt`

### Viewing Results

Check the test output in logcat:
```bash
adb logcat -d | grep "Baseline Performance Metrics" -A 5
```

Or view the HTML test report:
```
app/build/reports/androidTests/connected/debug/index.html
```

## Next Steps

After completing the API migration, run the post-migration performance test to compare metrics and validate improvements.
