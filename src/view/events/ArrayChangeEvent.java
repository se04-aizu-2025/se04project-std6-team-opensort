package com.opensort.view.events;

public class ArrayChangeEvent extends ViewEvent{

    private final int[] newArray;

    public ArrayChangeEvent(int[] newArray){
        this.newArray = newArray;
    }

    public int[] getArray(){
        return newArray;
    }

}
