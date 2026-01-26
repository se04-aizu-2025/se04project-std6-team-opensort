package com.opensort.sorting;

public class SelectionSort extends SortingAlgorithm {

    public SelectionSort(int[] numbers) {
        super(numbers);
    }

    public int[] sort() {
        // Loop over the array boundary
        for (int i = 0; i < numbers.length - 1; i++) {
            // Find the minimum element in the unsorted array
            int minIndex = i;
            for (int j = i + 1; j < numbers.length; j++) {
                if (compare(j, minIndex) && numbers[j] < numbers[minIndex]) {
                    minIndex = j;
                }
            }

            // Swap the found minimum element with the first element
            swap(minIndex, i);
            int temp = numbers[minIndex];
            numbers[minIndex] = numbers[i];
            numbers[i] = temp;

            sorted(i);
        }

        // At the end the last element is also sorted
        sorted(numbers.length - 1);

        return numbers;
    }
}