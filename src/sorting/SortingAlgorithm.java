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

    public void addEventListener(IView listener){
        eventListeners.add(listener);
    }

    public void removeEventListener(IView listener){
        eventListeners.remove(listener);
    }

    // Broadcast an event to all current event listeners
    private void fireEvent(SortEvent event){
        for(IView listener : eventListeners){
            listener.onSortEvent(event);
        }
    }

    // Fire a swap event for indices a and b
    public void swap(int a, int b){
        fireEvent(new SwapEvent(a, b));
    }

    // Mark the element at the index a
    public void mark(int a, MarkEventType type, String message){
        fireEvent(new MarkEvent(a, type, message));
    }

    // Mark the element at index a as sorted
    public void sorted(int a){
        mark(a, MarkEventType.Sorted, String.format("%d is now sorted", numbers[a]));
    }

    // Fire a compare event for indices a and b
    // Returns a boolean so that it can be used in if and loop conditions
    // Always returns true
    public boolean compare(int a, int b){
        fireEvent(new CompareEvent(a, b));
        return true;
    }
}