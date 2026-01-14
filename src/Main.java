package com.opensort;
import com.opensort.controller.Controller;
import com.opensort.controller.IController;
import com.opensort.view.IView;
import com.opensort.view.ConsoleView;

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

""");
    }

    public static void main(String[] args){

        // Set default behavior
        // Currently launches the cui
        IView view = new ConsoleView();

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
                    // Print the help message and exit
                    System.out.printf("Invalid command '%s'\n", subCommand);
                    printHelp();
                    return;
                }
            }

        }
        
        IController controller = new Controller(view);
        new Thread(controller).start();
        new Thread(view).start();

    }
}