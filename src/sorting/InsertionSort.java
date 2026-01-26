package com.opensort.sorting;

public class InsertionSort extends SortingAlgorithm {

    public InsertionSort(int[] numbers) {
        super(numbers);
    }

    public int[] sort() {
        for (int i = 1; i < numbers.length; i++) {
            int j = i;

            while (j > 0 && (compare(j-1, j) && numbers[j - 1] > numbers[j])) {
                // Perform the swap
                swap(j, j - 1);
                int temp = numbers[j];
                numbers[j] = numbers[j - 1];
                numbers[j - 1] = temp;

                // Move the pointer to the left to continue checking
                j--;
            }
            
            sorted(j);
        }

        sorted(numbers.length - 1);

        return numbers;
    }
}