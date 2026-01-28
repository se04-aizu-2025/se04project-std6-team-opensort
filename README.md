# Team OpenSort

The objective of the engineering topic is to develop software that provides a variety of sorting algorithms,
intended as a programming or algorithmic teaching tool.

## Prerequisites
The project was developed and tested using JDK 25.
An installation of this version of java or newer is required in order to build and run the software.
To check the currently installed java version, run ```java --version``` and ```javac --version```.

# Getting started
This section describes how to build and launch the software.

## Building
Depending on your platform, run the __build.sh__, __build.bat__ or __build.ps1__ file.  
This will create a jar file called __opensort.jar__.

## Running
The software is distributed as a .jar file.  
It can be run from the command line using the following command: ``java -jar opensort.jar``   
By providing the subcommand _help_, the usage of the software will be explained.
```
user@pc % java -jar opensort.jar help
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
```

# Documentation
Documentation on how the software works can be found in the [doc](./doc) folder.