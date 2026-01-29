package com.opensort;
import com.opensort.controller.Controller;
import com.opensort.controller.IController;
import com.opensort.utils.InputException;
import com.opensort.utils.InputHelper;
import com.opensort.view.IView;
import com.opensort.view.ConsoleView;
import com.opensort.testing.TestEngine;
import com.opensort.view.SortingGUI;

class Main{

    // Print help for application usage
    private static void printHelp(){
        System.out.println("""
Usage: java -jar opensort.jar [COMMAND] [ARRAY | LENGTH]

Commands:
If no command is provided, the GUI will be launched.

    test
        Run testcases for all available sorting algorithms and print the result to the command line.
        This command uses the second LENGTH parameter.
        This parameter indicates how long the test array should be. The default length is 100.
    cui
        Launch the CUI.
        Uses the ARRAY if provided.
    gui
        Launch the GUI.
        Uses the ARRAY if provided.
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

    public static void main(String[] args){

        // Exit the software if too many arguments where provided
        if(args.length > 2){
            System.out.println("Too many arguments provided.");
            System.out.println("Use the 'help' command to see how to use this cli.");
            return;
        }

        IView view = null;

        // Initial array the user can provide in the command
        int[] initialArray = null;

        if(args.length > 0) {
            try {
                // Try to interpret the last argument as an array
                initialArray = InputHelper.tryGetArrayFromString(args[args.length - 1]);
            } catch (InputException _) {}
        }

        // Check the subcommand if either:
        // a) The array was not initialized, but there is an argument available
        // b) There is more than one argument given
        if((args.length == 1 && initialArray == null) || args.length > 1){

            // Subcommands are always the first argument
            String subCommand = args[0];

            // Check the sub command
            switch (subCommand){
                case "test" -> {
                    TestEngine testEngine = new TestEngine();
                    TestEngine.TestResult[] results;

                    int N = 100;
                    // Try to get the array length from the command line
                    try{
                        N = Integer.parseInt(args[1]);
                    } catch (NumberFormatException _){
                        System.out.printf("Error: '%s' is not a valid length.\n", args[1]);
                        return;
                    } catch (ArrayIndexOutOfBoundsException _){
                        // Ignore if there was no length parameter provided
                    }

                    results = testEngine.runAll(N);

                    testEngine.printTestResults(results);
                    return;
                }
                case "cui" -> {
                    view = new ConsoleView();
                }
                case "gui" -> {
                    view = new SortingGUI();
                }
                case "help" -> {
                    // Print the help message and exit
                    printHelp();
                    return;
                }
                default -> {
                    System.out.printf("The command '%s' is not valid.\n", subCommand);
                    System.out.println("Use the 'help' command to get a list of valid commands.");
                    return;
                }
            }
        }

        IController controller;

        // Set default behavior
        // Currently launches the cui
        if(view == null){
            view = new SortingGUI();
        }

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