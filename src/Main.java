package com.opensort;
import com.opensort.sorting.*;
import com.opensort.view.IView;
import com.opensort.view.ConsoleView;

class Main{
    public static void main(String[] args){
        
        int[] test = new int[] {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        SortingAlgorithm h = new BubbleSort(test);

        IView view = new ConsoleView();
        view.setArray(test);

        h.addEventListener(view);

        int[] result = h.sort();
    }
}