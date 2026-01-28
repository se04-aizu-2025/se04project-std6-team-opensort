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
        return (int)Math.floor((double) (node - 1) / 2);
    }

    // Convert the given numbers from the first to the last index into a heap
    // Heapifies the numbers from the given root node downwards
    public int[] heapify(int[] numbers, int first, int last, int root)
    {
        highlight(root, String.format("Checking heap with root %d", numbers[root]));

        int largest = Integer.MIN_VALUE; 
        
        // Get the child nodes of the root
        int left = leftChildNode(root);
        int right = rightChildNode(root);

        // Check if the left child exists and is larger than the root
        if (left <= last && (compare(left, root) && numbers[left] > numbers[root])){
            largest = left;
        }
        else{
            largest = root;
        }

        // Check if the right child exists and is larger than the previous largest value
        if (right <= last && (compare(right, largest) && numbers[right] > numbers[largest])){
            largest = right;
        }

        // Swap the largest value with the root
        if (largest != root){
            swap(root, largest);
            int temp = numbers[root];
            numbers[root] = numbers[largest];
            numbers[largest] = temp;

            // After the swap, make sure the heap where the larges value previously was is still  in order
            return heapify(numbers, first, last, largest);
        }

        return numbers;
    }

    public int[] sort(){
        // Get the last non leaf node element of the heap
        int root = parentNode(numbers.length);
        int first = 0;
        int last = numbers.length - 1;

        // Loop over all non leaf node elements and build the heap
        for (int i = root; i >= 0; i--){
            heapify(numbers, first, last, i);
        }

        // Loop over all elements of the heap
        for (int i = last; i>first;i--){
            // Swap the current root with the last element of the non sorted heap
            swap(first, i);
            int temp = numbers[first];
            numbers[first] = numbers[i];
            numbers[i] = temp;

            sorted(i);
            // Rebuild the heap but ignore the new last value
            // This element is already sorted
            heapify(numbers, first, i-1, first);
        }
        sorted(0);
        return numbers;
    }

}