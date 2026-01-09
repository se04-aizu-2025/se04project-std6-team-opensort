package com.opensort.view;

import com.opensort.controller.IController;
import com.opensort.sorting.events.CompareEvent;
import com.opensort.sorting.events.MarkEvent;
import com.opensort.sorting.events.SortEvent;
import com.opensort.sorting.events.SwapEvent;
import com.opensort.view.events.AlgorithmChangeEvent;
import com.opensort.view.events.ArrayChangeEvent;
import com.opensort.view.events.ExitEvent;
import com.opensort.view.events.ViewEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;

// Simple console view
public class ConsoleView implements IView{

    private int[] array;
    private String[] algorithms = {};
    private ConcurrentLinkedQueue<SortEvent> eventsToProcess = new ConcurrentLinkedQueue<>();
    private boolean running = true;
    private Scanner scanner;

    private final List<IController> viewEventListeners = new ArrayList<>();

    // Print the current state of the array
    private void printArray(){
        for(int element : array){
            System.out.printf("%d ", element);
        }
        System.out.println();
    }

    // Handle a mark event
    private void handleMarkEvent(MarkEvent event){
        switch (event.getType()){
            case Sorted -> {
                System.out.printf("Sorted %d\n", array[event.getA()]);
            }
            default -> System.out.println("Unknown event");
        }
    }

    @Override
    public void onSortEvent(SortEvent event) {
        eventsToProcess.add(event);
    }

    // Function to handle a given sort event
    private void handleEvent(SortEvent event){
        // Handle the different types of sorting events
        switch (event){
            case SwapEvent swap -> {
                int a = swap.getA();
                int b = swap.getB();

                System.out.printf("Swapped %d and %d:\t", array[a], array[b]);

                int temp = array[a];
                array[a] = array[b];
                array[b] = temp;

                printArray();
            }
            case CompareEvent compare -> {
                System.out.printf("Comparing %d and %d\n", array[compare.getA()], array[compare.getB()]);
            }
            case MarkEvent mark -> {
                handleMarkEvent(mark);
            }
            default -> System.out.println("Unknown event");
        }
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;
    }

    @Override
    public void setAlgorithms(String[] algorithms) {
        this.algorithms = algorithms;
    }

    @Override
    public void addEventListener(IController listener) {
        viewEventListeners.add(listener);
    }

    @Override
    public void removeEventListener(IController listener) {
        viewEventListeners.remove(listener);
    }

    private void fireEvent(ViewEvent event){
        for(IController listener : viewEventListeners){
            listener.onViewEvent(event);
        }
    }

    // Get a new array from user input
    private int[] getArrayFromUser(){
        int[] newArray;
        boolean inputOK;
        // Repeat until the user input is ok
        do {
            inputOK = true;

            System.out.print("Please enter a ',' separated list of integers: ");
            String[] list = scanner.nextLine().split(",");

            // Try to convert the individual numbers
            newArray = new int[list.length];
            for (int i = 0; i < list.length; i++) {
                String number = list[i];
                try {
                    newArray[i] = Integer.parseInt(number);
                } catch (NumberFormatException _) {
                    // Print conversion errors and mark the input as not ok
                    System.out.printf("Error at index %d. '%s' is not a number.\n", i, number);
                    inputOK = false;
                }
            }
        } while (!inputOK);

        return newArray;
    }

    // Get a new sorting algorithm from user input
    public int getAlgorithmFromUser(){
        if(algorithms.length == 0){
            System.out.println("No algorithms available.");
            return -1;
        }

        int algorithm = -1;

        // Repeat until a valid algorithm is selected
        do {
            // Print all options
            for (int i = 0; i < algorithms.length; i++) {
                System.out.printf("%d: %s\n", i, algorithms[i]);
            }
            System.out.printf("Select an algorithm (0-%d): \n", algorithms.length - 1);

            // Try to convert user input
            try {
                algorithm = Integer.parseInt(scanner.nextLine());
            } catch (Exception _) {
                System.out.println("Invalid input.");
            }
            // Check bounds of algorithm array
        } while(algorithm < 0 || algorithm > algorithms.length);

        return algorithm;
    }

    private void printHelp(){
        System.out.println("""
Available commands:
    - (n)ext:
        Perform next step
    - (p)rint:
        Print the array
    - (h)elp | ?:
        Print this help
    - (q)uit | (e)xit:
        Quit the program
    - algo:
        Change the sorting algorithm
    - arr:
        Change the array being sorted
                            """);
    }

    @Override
    public void run() {
        scanner = new Scanner(System.in);

        if(array == null){
            fireEvent(new ArrayChangeEvent(getArrayFromUser()));
        }

        fireEvent(new AlgorithmChangeEvent(getAlgorithmFromUser()));

        String lastInput = "";
        while (running){
            System.out.print("(h for help)>_: ");
            String userInput = scanner.nextLine();

            if (userInput.isBlank()) userInput = lastInput;
            lastInput = userInput;

            switch (userInput.strip()){
                case "p":
                case "print":
                    printArray();
                    break;
                case "q":
                case "quit":
                case "e":
                case "exit":
                    running = false;
                    fireEvent(new ExitEvent());
                    break;
                case "n":
                case "next":
                    SortEvent nextEvent = eventsToProcess.poll();
                    if(nextEvent != null){
                        handleEvent(nextEvent);
                    } else {
                        System.out.println("Nothing to do...");
                    }
                    break;
                case "h":
                case "help":
                case "?":
                    printHelp();;
                    break;
                case "algo":
                    int algorithm = getAlgorithmFromUser();
                    if(algorithm >= 0){
                        fireEvent(new AlgorithmChangeEvent(algorithm));
                    }
                    break;
                case "arr":
                    int[] newArray = getArrayFromUser();
                    fireEvent(new ArrayChangeEvent(newArray));
                    break;
                default:
                    printHelp();
                    break;
            }
        }
    }
}
