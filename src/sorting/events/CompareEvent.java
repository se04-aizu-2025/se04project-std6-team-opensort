package com.opensort.sorting.events;

// Event when two elements in an array are being compared
public class CompareEvent extends SortEvent{

    private final int a;
    private final int b;

    // Compare the elements at index a and be with each other
    public CompareEvent(int a, int b){
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
