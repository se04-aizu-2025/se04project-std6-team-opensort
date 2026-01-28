package com.opensort.sorting;

public class QuickSort extends SortingAlgorithm{

    public QuickSort(int[] numbers){
        super(numbers);
    }

    public int[] sort(){
        quickSort(0, numbers.length - 1);
        return numbers;
    }

    private void quickSort(int low, int high){
        if (low < high){
            comment(String.format("Sorting from %d to %d", numbers[low], numbers[high]));
            int pi = partition(low, high);
            sorted(pi);

            quickSort(low, pi - 1);
            quickSort(pi + 1, high);
        } else if (low == high) {
            sorted(low);
        }
    }

    private void swap_numbers(int a, int b){
        if(a != b){
            swap(a, b);
            int temp = numbers[a];
            numbers[a] = numbers[b];
            numbers[b] = temp;
        }
    }

    private int partition(int low, int high){
        int pivot = numbers[high];
        highlight(high, String.format("Chose %d as the pivot element", pivot));
        int i = low - 1;

        for (int j = low; j < high; j++){
            if (compare(j, high) && numbers[j] < pivot) {
                i++;
                swap_numbers(i, j);
            }
        }
        swap_numbers(i + 1, high);

        return i + 1;
    }
}