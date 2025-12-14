package com.opensort.sorting;

public class HeapSort extends SortingAlgorithm{

    public HeapSort(int[] numbers){
        super(numbers);
    }

    // Get the index of the left child of the current node
    private int leftChildNode(int node){
        return 2 * node + 1;
    }

    // Get the index of the right child of the current node
    private int rightChildNode(int node){
        return 2 * node + 2;
    }

    // Get the index of the parent node of the current node
    private int parentNode(int node){
        return (int)Math.floor((node - 1) / 2);
    }

    // Convert the given array from the first to the last index into a heap
    // Heapifies the array from the given root node downwards
    public int[] heapify(int[] array, int first, int last, int root)
    {
        int largest = Integer.MIN_VALUE; 
        
        // Get the child nodes of the root
        int left = leftChildNode(root);
        int right = rightChildNode(root);

        // Check if the left child exists and is larger than the root
        if (left <= last && array[left] > array[root]){
            largest = left;
        }
        else{
            largest = root;
        }

        // Check if the right child exists and is larger than the previous largest value
        if (right <= last && array[right] > array[largest]){
            largest = right;
        }

        // Swap the largest value with the root
        if (largest != root){
            int temp = array[root];
            array[root] = array[largest];
            array[largest] = temp;

            // After the swap, make sure the heap where the larges value previously was is still  in order
            return heapify(array, first, last, largest);
        }

        return array;
    }

    public int[] sort(){
        return null;
    }

}