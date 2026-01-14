package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.SortEvent;

// Interface to generalize the view
public interface IView extends Runnable {

    // Allow the view to react to events from the currently used sorting algorithm
    public void onSortEvent(SortEvent event);

    // Tell the view what array it should display
    // This should also reset other thins related to the state of the array like event queues
    public void setArray(int[] array);

    // Set the list of algorithms the view should provide to the user
    // The index of the algorithm name in this index is the ID of the algorithm
    public void setAlgorithms(String[] algorithms);

    // Add an event listener to this view
    public void addEventListener(IController listener);

    // Remove an event listener from this view
    public void removeEventListener(IController listener);
}
