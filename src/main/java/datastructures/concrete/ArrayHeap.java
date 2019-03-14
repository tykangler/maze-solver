package datastructures.concrete;

import datastructures.interfaces.IPriorityQueue;
import misc.exceptions.EmptyContainerException;

/**
 * @see IPriorityQueue for details on what each method must do.
 */
public class ArrayHeap<T extends Comparable<T>> implements IPriorityQueue<T> {
    // See spec: you must implement a implement a 4-heap.
    private static final int NUM_CHILDREN = 4;
    private static final int INIT_CAPACITY  = 21;

    // You MUST use this field to store the contents of your heap.
    // You may NOT rename this field: we will be inspecting it within
    // our private tests.
    private T[] heap;
    private int size;

    // Feel free to add more fields and constants.

    public ArrayHeap() {
        heap = makeArrayOfT(INIT_CAPACITY);
        size = 0;
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain elements of type T.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private T[] makeArrayOfT(int arraySize) {
        // This helper method is basically the same one we gave you
        // in ArrayDictionary and ChainedHashDictionary.
        //
        // As before, you do not need to understand how this method
        // works, and should not modify it in any way.
        return (T[]) (new Comparable[arraySize]);
    }

    @Override
    public T removeMin() {
        if (size == 0) {
            throw new EmptyContainerException();
        }
        T ret = heap[0];
        size--;
        heap[0] = heap[size];
        int parent = 0;
        int minChildIndex = getMinChildIndex(parent);
        while (minChildIndex != parent) {
            T temp = heap[parent];
            heap[parent] = heap[minChildIndex];
            heap[minChildIndex] = temp;
            parent = minChildIndex;
            minChildIndex = getMinChildIndex(minChildIndex);
        }
        return ret;
    }

    /* returns the index of the child with the smallest value, given a parent. 
    If the parent has a smaller value than all of its children, return parent index. */
    private int getMinChildIndex(int minIndex) {
        int lastChild = minIndex * NUM_CHILDREN + 4;
        for (int i = minIndex * NUM_CHILDREN + 1; i < size && i <= lastChild; i++) {
            if (heap[minIndex].compareTo(heap[i]) > 0) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    @Override
    public T peekMin() {
        if (size == 0) {
            throw new EmptyContainerException();
        }
        return heap[0];
    }

    @Override
    public void insert(T item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
        if (size >= heap.length) {
            increaseCapacity();
        }
        heap[size] = item;
        size++;
        int compareIndex = size - 1;
        int parent = (compareIndex - 1) / NUM_CHILDREN;
        while (heap[parent].compareTo(heap[compareIndex]) > 0) { // while parent is bigger than child
            T temp = heap[compareIndex];
            heap[compareIndex] = heap[parent];
            heap[parent] = temp;
            compareIndex = parent;
            parent = (parent - 1) / NUM_CHILDREN;
        }
    }

    private void increaseCapacity() {
        T[] replace = makeArrayOfT(2 * heap.length);
        for (int i = 0; i < heap.length; i++) {
            replace[i] = heap[i];
        }
        heap = replace;
    }

    @Override
    public int size() {
        return size;
    }
}