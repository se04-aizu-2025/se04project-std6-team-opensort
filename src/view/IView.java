package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.SortEvent;
import com.opensort.view.events.ViewEvent;

// Interface to generalize the view
public interface IView extends Runnable {

    // Allow the view to react to events from the currently used sorting algorithm
    public void onSortEvent(SortEvent event);

    // Tell the view what array it should display
    public void setArray(int[] array);

    public void setAlgorithms(String[] algorithms);

    public void addEventListener(IController listener);

    public void removeEventListener(IController listener);
}
