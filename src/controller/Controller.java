package com.opensort.controller;

import com.opensort.sorting.SortingAlgorithm;
import com.opensort.view.IView;
import com.opensort.view.events.AlgorithmChangeEvent;
import com.opensort.view.events.ArrayChangeEvent;
import com.opensort.view.events.ExitEvent;
import com.opensort.view.events.ViewEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Controller implements IController{

    private final BlockingQueue<ViewEvent> eventsToProcess = new LinkedBlockingQueue<>();
    private boolean running = true;

    // The array currently being sorted
    private int[] currentArray;

    private final IView view;

    // The ID of the algorithm currently being used
    private int algorithmID = -1;
    // The current algorithm
    private SortingAlgorithm algorithm;

    // Create a new controller with the given view
    public Controller(IView view){
        this.view = view;

        // Listen to events from the view
        view.addEventListener(this);

        // Pass algorithm list to view
        view.setAlgorithms(AlgorithmList.getStrings());
    }

    // Create a new controller with the given view and initial array to sort
    public Controller(IView view, int[] initialArray){
        this.currentArray = initialArray;
        this(view);
    }

    @Override
    public void onViewEvent(ViewEvent event) {
        // Add the view event to the event queue
        eventsToProcess.add(event);
    }

    // Restart the sorting algorithm
    private void relaunchAlgorithm(){

        if (algorithm != null){
            // Remove the event listener so no new events reach the view
            algorithm.removeEventListener(view);
        }

        // Create new Algorithm, abandon old, still running algorithm
        try {
            algorithm = AlgorithmList.build(algorithmID, currentArray.clone());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // Reset the array of the view
        view.setArray(currentArray.clone());

        // Set the view to listen for events from the new algorithm
        algorithm.addEventListener(view);

        // Start the new sorting algorithm
        new Thread(new Runnable() {
            @Override
            public void run() {
                algorithm.sort();
            }
        }).start();
    }

    @Override
    public void run() {
        while (running){
            try {
                // Wait for a new view event
                ViewEvent nextEvent = eventsToProcess.take();
                switch (nextEvent){
                    case AlgorithmChangeEvent e -> {
                        // Set new algorithm ID
                        algorithmID = e.getAlgorithm();

                        // Only restart algorithm if array has been set
                        if(currentArray != null){
                            relaunchAlgorithm();
                        }
                    }
                    case ArrayChangeEvent e -> {
                        // Set new array
                        this.currentArray = e.getArray();

                        // Only restart algorithm if it has been set
                        if(algorithmID >= 0){
                            relaunchAlgorithm();
                        }
                    }
                    case ExitEvent e -> {
                        // Shut down the controller
                        running = false;
                    }
                    default -> {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                // Shut down the controller in case the thread got interrupted
                running = false;
            }
        }
        view.removeEventListener(this);
    }
}
