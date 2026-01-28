package com. opensort.testing;

import com.opensort.controller.AlgorithmList;
import com.opensort.controller.IController;
import com.opensort.sorting. SortingAlgorithm;
import com. opensort.sorting.events.*;
import com.opensort.view. IView;

public class TestEngine {

    DataGenerator generator = new DataGenerator();

    public static class TestResult{
        public String algorithmName;
        public String[] labels;
        public boolean[] testsPassed;
        public long[] executionTimes;
        public int[] compareCounts;
        public int[] swapCounts;

        public TestResult(String algorithmName, String[] labels, boolean[] testsPassed, long[] executionTimes, int[] compareCounts, int[] swapCounts){
            this.algorithmName = algorithmName;
            this.labels = labels;
            this.testsPassed = testsPassed;
            this.executionTimes = executionTimes;
            this.compareCounts = compareCounts;
            this.swapCounts = swapCounts;
        }
    }

    private static class EventCounter implements IView{
        int compareCount = 0;
        int swapCount = 0;

        @Override
        public void onSortEvent(SortEvent event){
            if (event instanceof CompareEvent){
                compareCount++;
            }else if (event instanceof SwapEvent){
                swapCount++;
            }
        }

        @Override
        public void setArray(int[] array){}

        @Override
        public void setAlgorithms(String[] algorithms){}

        @Override
        public void addEventListener(IController listener){}

        @Override
        public void removeEventListener(IController listener){}

        public void reset(){
            compareCount = 0;
            swapCount = 0;
        }

        @Override
        public void run(){}
    }

    public boolean checkSorted(int[] result){
        for(int i = 0; i < result.length-1; i++){
            if (result[i] > result[i+1]){
                return false;
            }
        }
        return true;
    }

    public TestResult runTestsForAlgorithm(int algorithmIndex, TestData[] testCases){
        String algorithmName = AlgorithmList. algorithms[algorithmIndex].name;
        String[] labels = new String[testCases.length];
        boolean[] results = new boolean[testCases.length];
        long[] executionTimes = new long[testCases.length];
        int[] compareCounts = new int[testCases.length];
        int[] swapCounts = new int[testCases. length];
        EventCounter counter = new EventCounter();

        for (int j = 0; j < testCases.length; j++) {
            TestData testCase = testCases[j];
            labels[j] = testCase.label;
            try {
                counter.reset();
                SortingAlgorithm algorithm = AlgorithmList.build(algorithmIndex, testCase.data.clone());
                algorithm.addEventListener(counter);

                long startTime = System.nanoTime();
                int[] result = algorithm.sort();
                long endTime = System.nanoTime();
                boolean sorted = checkSorted(result);

                results[j] = sorted;
                executionTimes[j] = endTime - startTime;
                compareCounts[j] = counter.compareCount;
                swapCounts[j] = counter.swapCount;
            // Also catch StackOverflowError in case of recursive sorting algorithms
            } catch (Exception | StackOverflowError _) {
                results[j] = false;
                executionTimes[j] = 0;
                compareCounts[j] = 0;
                swapCounts[j] = 0;
            }
        }

        return new TestResult(algorithmName, labels, results, executionTimes, compareCounts, swapCounts);
    }

    public TestResult[] runAll(){
        return runAll(10);
    }

    public TestResult[] runAll(int arraySize){
        TestData[] testCases = {
                new TestData("Reverse", generator.generateReverseSorted(arraySize)),
                new TestData("Sorted", generator.generateSorted(arraySize)),
                new TestData("Random", generator. generateRandom(arraySize, 1, 50))
        };

        TestResult[] results = new TestResult[AlgorithmList. algorithms.length];

        for (int i = 0; i < AlgorithmList.algorithms.length; i++){
            results[i] = runTestsForAlgorithm(i, testCases);
        }

        return results;
    }

    public long measureTime(SortingAlgorithm algorithm){
        long start = System.nanoTime();
        algorithm.sort();
        long end = System.nanoTime();
        return end - start;
    }

    public void printTestResults(TestResult[] results){
        System.out.println("Test Results: ");

        for(TestResult result : results){
            System.out.println("\nAlgorithm: " + result.algorithmName);
            System.out.println("-".repeat(90));

            for(int i = 0; i < result.testsPassed.length; i++){
                String status = result.testsPassed[i] ? "True" : "False";
                double timeMs = result.executionTimes[i] / 1_000_000.0;

                System.out.printf("  %-12s %s  |  Time: %8.3f ms  |  Compares: %7d  |  Swaps: %7d%n",
                        result.labels[i] + ":",
                        status,
                        timeMs,
                        result.compareCounts[i],
                        result.swapCounts[i]);
            }
        }

        System.out.println("\n" + "=".repeat(90));
    }

    private static class TestData{
        String label;
        int[] data;

        TestData(String label, int[] data){
            this.label = label;
            this.data = data;
        }
    }
}