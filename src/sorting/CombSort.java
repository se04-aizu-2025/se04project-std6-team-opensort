package com.opensort.sorting;

public class CombSort extends SortingAlgorithm{


    public CombSort(int[] numbers) {
        super(numbers);
    }

    @Override
    public int[] sort() {
        int n = numbers.length;
        int gap = n;
        double shrinkFactor = 1.3;
        boolean swapped = true;
        int sortedCount = 0;

        while (gap != 1 || swapped) {
            gap = (int) (gap / shrinkFactor);
            if (gap < 1) gap = 1;

            swapped = false;

            // Use sortedCount to shorten the pass when gap is 1
            int limit = (gap == 1) ? (n - gap - sortedCount) : (n - gap);

            for (int i = 0; i < limit; i++) {
                if (compare(i, i + gap) && numbers[i] > numbers[i + gap]) {
                    swap(i, i + gap);
                    int temp = numbers[i];
                    numbers[i] = numbers[i + gap];
                    numbers[i + gap] = temp;
                    swapped = true;
                }
            }

            // Only mark elements as 'sorted' when we are in the final gap=1 phase
            if (gap == 1) {
                // In Bubble Sort, the element at the end of the pass is guaranteed sorted
                sorted(n - 1 - sortedCount);
                sortedCount++;

                // If no swaps happened, mark all remaining elements as sorted
                if (!swapped) {
                    for (int i = 0; i < n - sortedCount; i++) sorted(i);
                }
            }
        }

        return numbers;
    }
}
