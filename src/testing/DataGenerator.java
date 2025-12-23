package com.opensort.testing;

import java.util.Random;

public class DataGenerator{

    private Random random;

    public DataGenerator(){
        this.random = new Random();
    }

    public int[] generateRandom(int size, int min, int max){
        int[] arr = new int[size];

        for (int i = 0; i < size; i++){
            arr[i] = random.nextInt(max - min + 1) + min;
        }
        return arr;
    }

    public int[] generateSorted(int size){
        int[] arr = new int[size];

        for (int i = 0; i < size; i++){
            arr[i] = i;
        }
        return arr;
    }


    public int[] generateReverseSorted(int size){
        int[] arr = new int[size];

        for (int i = 0; i < size; i++){
            arr[i] = size - i;
        }
        return arr;
    }


    public int[] generateWithDuplicates(int size, int uniqueValues){
        return null;
    }

    public int[] generateAllSame(int size, int value){
        return null;
    }

    public int[] generateNearlySorted(int size, int swaps){
        return null;
    }
}
