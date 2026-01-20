package com.opensort.testing;

import com.opensort.controller.AlgorithmList;
import com.opensort.sorting.SortingAlgorithm;

public class TestEngine {

    DataGenerator generator = new DataGenerator();

    public boolean runTest(SortingAlgorithm algorithm){
        int[] arr = algorithm.sort();
        for(int i = 0; i < arr.length-1; i++){
            if (arr[i] > arr[i+1]){
                return false;
            }
        }
        return true;
    }

    public void runAll(){
        TestData[] testCases = {
                new TestData("Sorted", generator.generateSorted(10)),
                new TestData("Reverse", generator.generateReverseSorted(10)),
                new TestData("Random", generator.generateRandom(10, 1, 50))
        };

        System.out.printf("%-15s %-8s %-8s %-8s\n",
                "Algorithm", "Sorted", "Reverse", "Random");
        System.out.println("-".repeat(40));

        for(int i = 0; i < AlgorithmList.algorithms.length; i++){
            System.out.printf("%-15s ", AlgorithmList.algorithms[i].name);

            for(TestData testCase : testCases){
                try {
                    SortingAlgorithm algorithm = AlgorithmList.build(i, testCase.data.clone());
                    boolean result = runTest(algorithm);
                    System.out.printf("%-8s ", result);
                } catch (Exception e) {
                    System.out.printf("%-8s ", "ERROR");
                }
            }
            System.out.println();
        }
    }

    public long measureTime(SortingAlgorithm algorithm){
        long start = System.nanoTime();
        algorithm.sort();
        long end = System.nanoTime();
        return end - start;
    }

    public EventCounts countEvents(SortingAlgorithm algorithm){

        return new EventCounts(0, 0);
    }

    public static class EventCounts{
        public final int compareCount;
        public final int swapCount;

        public EventCounts(int compareCount, int swapCount){
            this.compareCount = compareCount;
            this.swapCount = swapCount;
        }

        @Override
        public String toString(){
            return String.format("Compares: %d, Swaps: %d", compareCount, swapCount);
        }
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