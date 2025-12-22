package com.opensort.view;

import com.opensort.sorting.events.SortEvent;

// Interface to generalize the view
public interface IView {

    // Allow the view to react to events from the currently used sorting algorithm
    public void onSortEvent(SortEvent event);

    // Tell the view what array it should display
    public void setArray(int[] array);
}
