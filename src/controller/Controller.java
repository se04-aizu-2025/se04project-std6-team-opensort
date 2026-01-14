package com.opensort.controller;

import com.opensort.sorting.BubbleSort;
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

    private BlockingQueue<ViewEvent> eventsToProcess = new LinkedBlockingQueue<>();
    private boolean running = true;
    private int[] currentArray;

    private final IView view;
    private int algorithmID = -1;
    private SortingAlgorithm algorithm;

    public Controller(IView view){
        this.view = view;
        view.addEventListener(this);
        view.setAlgorithms(AlgorithmList.getStrings());
    }

    public Controller(IView view, int[] initialArray){
        this.currentArray = initialArray;
        this(view);
    }

    @Override
    public void onViewEvent(ViewEvent event) {
        eventsToProcess.add(event);

    }

    private void relaunchAlgorithm(){

        if (algorithm != null){
            algorithm.removeEventListener(view);
        }

        // Create new Algorithm, abandon old, still running algorithm
        // Since the view is now unsubscribed, this should be no issue
        try {
            algorithm = AlgorithmList.build(algorithmID, currentArray);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        // Reset the array of the view
        // This should also empty the event queue
        view.setArray(currentArray);

        algorithm.addEventListener(view);

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
                        this.currentArray = e.getArray();

                        if(algorithmID >= 0){
                            relaunchAlgorithm();
                        }
                    }
                    case ExitEvent e -> {
                        running = false;
                    }
                    default -> {
                        break;
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        view.removeEventListener(this);
    }
}
