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
        public boolean[] testsPassed;
        public long[] executionTimes;
        public int[] compareCounts;
        public int[] swapCounts;

        public TestResult(String algorithmName, boolean[] testsPassed, long[] executionTimes, int[] compareCounts, int[] swapCounts){
            this.algorithmName = algorithmName;
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

    public boolean runTest(SortingAlgorithm algorithm){
        int[] arr = algorithm.sort();
        for(int i = 0; i < arr.length-1; i++){
            if (arr[i] > arr[i+1]){
                return false;
            }
        }
        return true;
    }

    public TestResult runTestsForAlgorithm(int algorithmIndex, TestData[] testCases){
        String algorithmName = AlgorithmList. algorithms[algorithmIndex].name;
        boolean[] results = new boolean[testCases.length];
        long[] executionTimes = new long[testCases.length];
        int[] compareCounts = new int[testCases.length];
        int[] swapCounts = new int[testCases. length];
        EventCounter counter = new EventCounter();

        for (int j = 0; j < testCases.length; j++) {
            try {
                counter.reset();
                SortingAlgorithm algorithm = AlgorithmList.build(algorithmIndex, testCases[j].data.clone());
                algorithm.addEventListener(counter);

                long startTime = System.nanoTime();
                boolean result = runTest(algorithm);
                long endTime = System.nanoTime();

                results[j] = result;
                executionTimes[j] = endTime - startTime;
                compareCounts[j] = counter.compareCount;
                swapCounts[j] = counter.swapCount;
            } catch (Exception e) {
                results[j] = false;
                executionTimes[j] = 0;
                compareCounts[j] = 0;
                swapCounts[j] = 0;
            }
        }

        return new TestResult(algorithmName, results, executionTimes, compareCounts, swapCounts);
    }

    public TestResult[] runAll(){
        return runAll(10);
    }

    public TestResult[] runAll(int arraySize){
        TestData[] testCases = {
                new TestData("Sorted", generator.generateSorted(arraySize)),
                new TestData("Reverse", generator.generateReverseSorted(arraySize)),
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

    private static class TestData{
        String label;
        int[] data;

        TestData(String label, int[] data){
            this.label = label;
            this.data = data;
        }
    }
}