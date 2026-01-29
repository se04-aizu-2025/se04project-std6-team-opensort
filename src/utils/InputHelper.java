package com.opensort.utils;

public class InputHelper {
    // Try to extract an integer array from a given string
    // The integers need to be ',' separated
    public static int[] tryGetArrayFromString(String s) throws InputException{
        String[] list = s.split(",");

        // Try to convert the individual numbers
        int[] newArray = new int[list.length];
        for (int i = 0; i < list.length; i++) {
            String number = list[i];
            try {
                newArray[i] = Integer.parseInt(number);
            } catch (NumberFormatException _) {
                // Return error in case the input is invalid
                throw new InputException(String.format("Error at index %d: '%s' is not a number.\n", i, number));
            }
        }

        // Return the array of numbers
        return newArray;
    }
}
