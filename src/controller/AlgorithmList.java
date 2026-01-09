package com.opensort.controller;

import com.opensort.sorting.BubbleSort;
import com.opensort.sorting.SortingAlgorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class AlgorithmList {
    public static final AlgorithmData[] algorithms = {
        new AlgorithmData(){{
            name = "Bubble sort";
            classType = BubbleSort.class;
        }}
    };

    public static String[] getStrings(){
        return Arrays.stream(algorithms).map(algorithm -> algorithm.name).toArray(String[]::new);
    }

    public static SortingAlgorithm build(int id, int[] array) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        return (SortingAlgorithm) algorithms[id].classType
                .getDeclaredConstructors()[0].newInstance((Object)array);
    }

    public static int getIndex(String name){
        for(int i = 0; i < algorithms.length; i++){
            if(algorithms[i].name.equals(name)){
                return i;
            }
        }

        return -1;
    }
}
