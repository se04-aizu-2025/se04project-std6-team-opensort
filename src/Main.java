package com.opensort;
import com.opensort.controller.Controller;
import com.opensort.controller.IController;
import com.opensort.view.IView;
import com.opensort.view.ConsoleView;
import com.opensort.view.events.ArrayChangeEvent;

import javax.swing.*;

class Main{

    // Print help for application usage
    private static void printHelp(){
        System.out.println("""
Usage: java -jar opensort.jar [COMMAND] [ARRAY]

Commands:
    test
        Run testcases for all available sorting algorithms and print the result to the command line.
        This command ignores the ARRAY parameter.
    cui
        Launch the CUI.
        Uses the ARRAY if provided.
    gui
        Launch the GUI.
        Uses the array if provided.
    help
        Display this help message.

Array:
    Provide an initial array to sort.
    The elements of the array need to be ',' separated.
    
Examples:
    java -jar opensort.jar
    java -jar opensort.jar cui
    java -jar opensort.jar cui 5,4,3,2,1

""");
    }

    // Try to extract an integer array from a given string
    // The integers need to be ',' separated
    private static int[] tryGetArrayFromString(String s) throws Exception{
        String[] list = s.split(",");

        // Try to convert the individual numbers
        int[] newArray = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            String number = list[i];
            try {
                newArray[i] = Integer.parseInt(number);
            } catch (NumberFormatException _) {
                // Return error in case the input is invalid
                throw new Exception(String.format("Error at index %d: '%s' is not a number.\n", i, number));
            }
        }

        // Return the array of numbers
        return newArray;
    }

    public static void main(String[] args){

        // Set default behavior
        // Currently launches the cui
        IView view = new ConsoleView();

        // Initial array the user can provide in the command
        int[] initialArray = null;

        if(args.length > 0){
            String subCommand = args[0];

            // Check the sub commands if provided
            switch (subCommand){
                case "test" -> {
                    // TODO launch test system
                    System.out.println("Currently not implemented.");
                    return;
                }
                case "cui" -> {
                    view = new ConsoleView();

                    // If an additional argument is provided
                    if(args.length > 1) {
                        // Try to extract the user provided array from the last argument.
                        try {
                            initialArray = tryGetArrayFromString(args[args.length - 1]);
                        } catch (Exception e) {
                            System.out.println("Error while processing provided array.");
                            System.out.println(e.getMessage());
                            return;
                        }
                    }
                }
                case "gui" -> {
                    // TODO launch gui
                    System.out.println("Currently not implemented.");
                    return;
                }
                case "help" -> {
                    // Print the help message and exit
                    printHelp();
                    return;
                }
                default -> {
                    // Check if the invalid subcommand is an array
                    try{
                        initialArray = tryGetArrayFromString(args[args.length - 1]);
                    } catch (Exception e) {
                        // Print error
                        System.out.printf("Error: '%s' is neither a valid command nor a valid initial array.\n", subCommand);
                        return;
                    }
                }
            }
        }

        IController controller;

        // Set the initial array of the view and the controller if the user provided it
        if(initialArray != null){
            view.setArray(initialArray);
            controller = new Controller(view, initialArray);
        } else {
            controller = new Controller(view);
        }

        new Thread(controller).start();
        new Thread(view).start();


    }
}