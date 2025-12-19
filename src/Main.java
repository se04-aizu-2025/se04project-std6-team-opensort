package com.opensort;
import com.opensort.sorting.*;

class Main{
    public static void main(String[] args){
        
        int[] test = new int[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        SortingAlgorithm h = new BubbleSort(test);

        int[] result = h.sort();

        for(int i = 0; i < result.length; i++){
            System.out.println(result[i]);
        } 
    }
}