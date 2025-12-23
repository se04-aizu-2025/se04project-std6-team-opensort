package com.opensort.testing;

import com.opensort.sorting.SortingAlgorithm;

public class TestEngine {

    public int[] runTest(SortingAlgorithm algorithm) {
        return algorithm.sort();
    }

    public long measureTime(SortingAlgorithm algorithm) {
        long start = System.nanoTime();
        algorithm.sort();
        long end = System.nanoTime();
        return end - start;
    }
}