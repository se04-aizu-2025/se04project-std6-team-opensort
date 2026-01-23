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

    // Keep track of visual marks
    private char[] marks;

    // Visual mark types
    private final char SORTED_MARK_CHAR = '*';
    private final char COMPARE_MARK_CHAR = '?';
    private final char SWAP_MARK_CHAR = '^';
    private final char EMPTY_MARK_CHAR = ' ';

    // List of algorithms the user can choose from
    private String[] algorithms = {};

    // Events that need to be processed
    private final ConcurrentLinkedQueue<SortEvent> eventsToProcess = new ConcurrentLinkedQueue<>();

    private boolean running = true;
    private Scanner scanner;

    private final List<IController> viewEventListeners = new ArrayList<>();

    // Print the current state of the array
    private void printArray(){

        String[] numbers = Arrays.stream(array).mapToObj(String::valueOf).toArray(String[]::new);

        System.out.println(String.join(" ", numbers));

        for(int i = 0; i < numbers.length; i++){
            char mark = marks[i];
            // Print the mark of the current number
            System.out.printf("%s ", String.valueOf(mark).repeat(numbers[i].length()));
        }
        System.out.println();
    }

    // Handle a mark event
    private void handleMarkEvent(MarkEvent event){
        int a = event.getA();
        switch (event.getType()){
            case Sorted -> {
                marks[a] = SORTED_MARK_CHAR;
            }
        }
        System.out.println(event.getMessage());
    }

    @Override
    public void onSortEvent(SortEvent event) {
        eventsToProcess.add(event);
    }

    // Function to handle a given sort event
    private void handleEvent(SortEvent event){

        // When a new event is processed, remove all non permanent marks
        for(int i = 0; i < marks.length; i++){
            // Only the 'sorted' mark is permananet
            if(marks[i] != SORTED_MARK_CHAR)
                marks[i] = EMPTY_MARK_CHAR;
        }

        // Handle the different types of sorting events
        switch (event){
            case SwapEvent swap -> {
                int a = swap.getA();
                int b = swap.getB();

                marks[a] = SWAP_MARK_CHAR;
                marks[b] = SWAP_MARK_CHAR;
                System.out.printf("Swapped %d and %d\n", array[a], array[b]);

                int temp = array[a];
                array[a] = array[b];
                array[b] = temp;
            }
            case CompareEvent compare -> {
                int a = compare.getA();
                int b = compare.getB();

                marks[a] = COMPARE_MARK_CHAR;
                marks[b] = COMPARE_MARK_CHAR;

                System.out.printf("Comparing %d and %d\n", array[a], array[b]);
            }
            case MarkEvent mark -> {
                handleMarkEvent(mark);
            }
            default -> System.out.println("Unknown event");
        }

        printArray();
    }

    @Override
    public void setArray(int[] array) {
        this.array = array;

        // Initialize empty mark array
        this.marks = new char[array.length];
        Arrays.fill(marks, EMPTY_MARK_CHAR);

        // Clear all remaining events to process, since they are related to the old state of the array
        this.eventsToProcess.clear();
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

        // In case no initial array was provided, request it from the user
        if(array == null){
            fireEvent(new ArrayChangeEvent(getArrayFromUser()));
        }

        // Request the sorting algorithm from the user
        fireEvent(new AlgorithmChangeEvent(getAlgorithmFromUser()));

        // Remember the last input
        String lastInput = "";

        while (running){
            // Get user input
            System.out.print("(h for help)>_: ");
            String userInput = scanner.nextLine();

            // If the input is blank, replay the last input
            if (userInput.isBlank()) userInput = lastInput;
            lastInput = userInput;

            // Check the command provided by the user
            switch (userInput.strip()){
                // Print the current state of the array
                case "p":
                case "print":
                    printArray();
                    break;
                // Quit the application
                case "q":
                case "quit":
                case "e":
                case "exit":
                    running = false;
                    fireEvent(new ExitEvent());
                    break;
                // Perform the next algorithm step
                case "n":
                case "next":
                    // Try to get the next event from the queue and process it
                    SortEvent nextEvent = eventsToProcess.poll();
                    if(nextEvent != null){
                        handleEvent(nextEvent);
                    } else {
                        System.out.println("Nothing to do...");
                    }
                    break;
                // Print the user help
                case "h":
                case "help":
                case "?":
                    printHelp();;
                    break;
                // Change the current algorithm
                case "algo":
                    int algorithm = getAlgorithmFromUser();
                    if(algorithm >= 0){
                        fireEvent(new AlgorithmChangeEvent(algorithm));
                    }
                    break;
                // Change the current array
                case "arr":
                    int[] newArray = getArrayFromUser();
                    fireEvent(new ArrayChangeEvent(newArray));
                    break;
                // In case the input was invalid, print the user help
                default:
                    printHelp();
                    break;
            }
        }
    }
}
