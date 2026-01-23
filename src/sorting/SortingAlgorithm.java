package com.opensort.sorting;

import com.opensort.sorting.events.*;
import com.opensort.view.IView;

import java.util.ArrayList;
import java.util.List;

// Abstract class to represent a sorting algorithm
// This is the model of the software
public abstract class SortingAlgorithm{
    
    // Array of unsorted numbers
    int[] numbers;

    // Implementations of IView to notify when a sorting event is fired
    List<IView> eventListeners = new ArrayList<IView>();

    public SortingAlgorithm(int[] numbers){
        this.numbers = numbers;
    }

    // Implement the sorting algorithm
    public abstract  int[] sort();

    public synchronized void addEventListener(IView listener){
        eventListeners.add(listener);
    }

    public synchronized void removeEventListener(IView listener){
        eventListeners.remove(listener);
    }

    // Broadcast an event to all current event listeners
    private synchronized void fireEvent(SortEvent event){
        for(IView listener : eventListeners){
            listener.onSortEvent(event);
        }
    }

    // Fire a swap event for indices a and b
    void swap(int a, int b){
        fireEvent(new SwapEvent(a, b));
    }

    // Mark the element at the index a
    void mark(int a, MarkEventType type, String message){
        fireEvent(new MarkEvent(a, type, message));
    }

    // Mark the element at index a as sorted
    void sorted(int a){
        mark(a, MarkEventType.Sorted, String.format("%d is now sorted", numbers[a]));
    }

    void highlight(int a, String message){
        mark(a, MarkEventType.Highlight, message);
    }

    // Fire a compare event for indices a and b
    // Returns a boolean so that it can be used in if and loop conditions
    // Always returns true
    boolean compare(int a, int b){
        fireEvent(new CompareEvent(a, b));
        return true;
    }
}