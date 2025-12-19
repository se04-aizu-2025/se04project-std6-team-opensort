package com.opensort.sorting;

// Abstract class to represent a sorting algorithm
// This is the model of the software
public abstract class SortingAlgorithm{
    
    // Array of unsorted numbers
    int[] numbers;

    public SortingAlgorithm(int[] numbers){
        this.numbers = numbers;
    }

    // Implement the sorting algorithm
    public abstract  int[] sort();
}