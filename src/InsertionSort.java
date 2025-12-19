package com.opensort.sorting;

public class InsertionSort extends SortingAlgorithm {

    public InsertionSort(int[] numbers) {
        super(numbers);
    }

    public int[] sort() {
        int[] array = numbers.clone();

        // Start from the second element
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;

            // Move elements of array that are greater than key
            // to one position ahead of their current position
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            
            // Insert the key
            array[j + 1] = key;
        }

        return array;
    }
}