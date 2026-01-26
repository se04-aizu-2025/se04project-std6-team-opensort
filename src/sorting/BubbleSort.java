package com.opensort.sorting;

public class BubbleSort extends SortingAlgorithm{

    public BubbleSort(int[] numbers){
        super(numbers);
    }

    public int[] sort(){
        int[] array = numbers.clone();

        // Loop as often as the array has numbers
        for(int i = 0; i < array.length; i++){
            // Loop over all numbers in the array, except for the last one
            // Do not loop over the i last numbers, since they will already be sorted
            for(int j = 0; j < array.length - i - 1; j++){
                // If the next number is smaller than the current number
                if(compare(j, j + 1) && array[j + 1] < array[j]){
                    // Swap them
                    int temp = array[j + 1];
                    array[j + 1] = array[j];
                    array[j] = temp;
                    swap(j, j + 1);
                }
            }
            sorted(array.length - i - 1);
        }

        return array;
    }

}