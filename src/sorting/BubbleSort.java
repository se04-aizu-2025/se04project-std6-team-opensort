package com.opensort.sorting;

public class BubbleSort extends SortingAlgorithm{

    public BubbleSort(int[] numbers){
        super(numbers);
    }

    public int[] sort(){
        // Loop as often as the array has numbers
        for(int i = 0; i < numbers.length; i++){
            // Loop over all numbers in the array, except for the last one
            // Do not loop over the i last numbers, since they will already be sorted
            for(int j = 0; j < numbers.length - i - 1; j++){
                // If the next number is smaller than the current number
                if(compare(j, j + 1) && numbers[j + 1] < numbers[j]){
                    // Swap them
                    int temp = numbers[j + 1];
                    numbers[j + 1] = numbers[j];
                    numbers[j] = temp;
                    swap(j, j + 1);
                }
            }
            sorted(numbers.length - i - 1);
        }

        return numbers;
    }

}