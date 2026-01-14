package com.opensort.controller;

import com.opensort.sorting.BubbleSort;
import com.opensort.sorting.SortingAlgorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

// This class contains a list of all algorithms currently implemented
public class AlgorithmList {

    // List of all implemented algorithms
    // To add a new algorithm to  the software, append it to this array
    // The ID of the algorithm referenced in the software is the index of the algorithm in this array
    public static final AlgorithmData[] algorithms = {
        new AlgorithmData(){{
            name = "Bubble sort";
            classType = BubbleSort.class;
        }}
    };

    // Get the names of all algorithms as a string array
    public static String[] getStrings(){
        return Arrays.stream(algorithms).map(algorithm -> algorithm.name).toArray(String[]::new);
    }

    // Create a new instance of a sorting algorithm with the given array
    public static SortingAlgorithm build(int id, int[] array) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return (SortingAlgorithm) algorithms[id].classType
                .getDeclaredConstructors()[0].newInstance((Object)array);
    }

    // Get the index of an algorithm from its name
    // Returns -1 if there is no algorithm with the given name
    public static int getIndex(String name){
        for(int i = 0; i < algorithms.length; i++){
            if(algorithms[i].name.equals(name)){
                return i;
            }
        }

        return -1;
    }
}
