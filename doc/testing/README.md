# Testing

The software includes a comprehensive testing framework to validate sorting algorithms and measure their performance.

## Test Engine

The testing functionality is provided by the **TestEngine** class in the **testing** package. This class can automatically test all registered sorting algorithms against various test cases and collect performance metrics.

### Running Tests

The TestEngine provides several methods to run tests:
```
TestEngine engine = new TestEngine();

// Run all algorithms with default array size (10)
TestResult[] results = engine.runAll();

// Run all algorithms with custom array size
TestResult[] results = engine.runAll(100);

// Run tests for a specific algorithm
TestData[] testCases = {
    new TestData("Sorted", generator.generateSorted(50)),
    new TestData("Random", generator.generateRandom(50, 1, 100))
};
TestResult result = engine.runTestsForAlgorithm(algorithmIndex, testCases);
```

### Test Results

Each test returns a **TestResult** object containing:
- `algorithmName` - Name of the tested algorithm
- `testsPassed` - Boolean array indicating which tests passed
- `executionTimes` - Execution time in nanoseconds for each test
- `compareCounts` - Number of compare operations performed
- `swapCounts` - Number of swap operations performed

### Displaying Results

The TestEngine includes a built-in method to print formatted test results:
```
engine.printTestResults(results);
```

This outputs a formatted table showing pass/fail status, execution time in milliseconds, and operation counts for each test case.

## Data Generator

The **DataGenerator** class provides methods to create test data with different characteristics:

### generateRandom(size, min, max)
Generates an array of random integers within a specified range.
```
int[] randomData = generator.generateRandom(100, 1, 1000);
```

### generateSorted(size)
Generates a pre-sorted array containing sequential values from 0 to size-1.
```
int[] sortedData = generator.generateSorted(100);
```

### generateReverseSorted(size)
Generates an array sorted in descending order.
```
int[] reverseData = generator.generateReverseSorted(100);
```

## Creating Custom Tests

You can create custom test scenarios by combining the DataGenerator with the TestEngine:
```
DataGenerator generator = new DataGenerator();
TestEngine engine = new TestEngine();

TestData[] customTests = {
    new TestData("Small Random", generator.generateRandom(10, 1, 50)),
    new TestData("Large Sorted", generator.generateSorted(1000)),
    new TestData("Large Reverse", generator.generateReverseSorted(1000)),
    new TestData("Medium Random", generator.generateRandom(500, 1, 100))
};

// Run tests for specific algorithm
TestResult result = engine.runTestsForAlgorithm(0, customTests);
engine.printTestResults(new TestResult[]{result});
```

## Performance Measurement

The TestEngine automatically tracks performance metrics by using an internal **EventCounter** that listens to sorting events. This allows measurement of:
- Execution time (in nanoseconds)
- Number of comparison operations
- Number of swap operations

These metrics help analyze algorithm efficiency and complexity in practice.