package com.opensort.controller;

import com.opensort.view.events.ViewEvent;

// Interface to generalize the controller
public interface IController extends Runnable {

    // Allow the controller to react to events from the view
    public void onViewEvent(ViewEvent event);
}
