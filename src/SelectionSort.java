package com.opensort.sorting;

public class SelectionSort extends SortingAlgorithm {

    public SelectionSort(int[] numbers) {
        super(numbers);
    }

    public int[] sort() {
        int[] array = numbers.clone();

        // Loop over the array boundary
        for (int i = 0; i < array.length - 1; i++) {
            // Find the minimum element in the unsorted array
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                if (array[j] < array[minIndex]) {
                    minIndex = j;
                }
            }

            // Swap the found minimum element with the first element
            int temp = array[minIndex];
            array[minIndex] = array[i];
            array[i] = temp;
        }

        return array;
    }
}