package com.opensort.view.events;

public class AlgorithmChangeEvent extends ViewEvent{
    private final int algorithm;

    public AlgorithmChangeEvent(int algorithm){
        this.algorithm = algorithm;
    }

    public int getAlgorithm(){
        return algorithm;
    }
}
