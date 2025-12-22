package com.opensort.view;

import com.opensort.sorting.events.CompareEvent;
import com.opensort.sorting.events.MarkEvent;
import com.opensort.sorting.events.SortEvent;
import com.opensort.sorting.events.SwapEvent;

// Simple console view
public class ConsoleView implements IView{

    private int[] array;

    // Print the current state of the array
    private void printArray(){
        for(int element : array){
            System.out.printf("%d ", element);
        }
        System.out.println();
    }

    // Handle a mark event
    private void handleMarkEvent(MarkEvent event){
        switch (event.getType()){
            case Sorted -> {
                System.out.printf("Sorted %d\n", array[event.getA()]);
            }
            default -> System.out.println("Unknown event");
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        // Handle the different types of sorting events
        switch (event){
            case SwapEvent swap -> {
                int a = swap.getA();
                int b = swap.getB();

                System.out.printf("Swapped %d and %d:\t", array[a], array[b]);

                int temp = array[a];
                array[a] = array[b];
                array[b] = temp;

                printArray();
            }
            case CompareEvent compare -> {
                System.out.printf("Comparing %d and %d\n", array[compare.getA()], array[compare.getB()]);
            }
            case MarkEvent mark -> {
                handleMarkEvent(mark);
            }
            default -> System.out.println("Unknown event");
        }
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
        System.out.print("Initial array:\t\t");
        printArray();
    }
}
