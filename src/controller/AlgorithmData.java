package com.opensort.controller;

import com.opensort.sorting.SortingAlgorithm;

// Class to hold information about an algorithm
public class AlgorithmData {
    // The display name of the algorithm
    public String name;

    // The class type of the algorithm
    // Used to create an instance of it
    // Must inherit from the 'SortingAlgorithm' class
    public Class<? extends SortingAlgorithm> classType;
}
