package com.opensort.sorting.events;

// Event to tell the view that elements in the array have been swapped
public class SwapEvent extends SortEvent{

    private final int a;
    private final int b;

    // The elements with the indices a and b have been swapped
    public SwapEvent(int a, int b){
        this.a = a;
        this.b = b;
    }

    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }
}
