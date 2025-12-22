package com.opensort.sorting.events;

// Event to mark a element in the array
public class MarkEvent extends SortEvent{

    private int a;
    private String message;

    // Mark the element at index a
    // Provide an additional message with further context like 'Element x has been sorted'
    public MarkEvent(int a, String message){
        this.a = a;
        this.message = message;
    }

    // Mark the element at index a
    public MarkEvent(int a){
        this(a, "");
    }

    public int getA(){
        return a;
    }

    public String getMessage(){
        return message;
    }

}
