package misc;

import datastructures.concrete.ArrayHeap;
import datastructures.concrete.DoubleLinkedList;
import datastructures.interfaces.IList;
import datastructures.interfaces.IPriorityQueue;

public class Sorter {
    /**
     * This method takes the input list and returns the top k elements
     * in sorted order.
     *
     * So, the first element in the output list should be the "smallest"
     * element; the last element should be the "largest".
     *
     * If the input list contains fewer than 'k' elements, return
     * a list containing all input.length elements in sorted order.
     *
     * This method must not modify the input list.
     *
     * @throws IllegalArgumentException  if k < 0
     * @throws IllegalArgumentException  if input is null
     */
    public static <T extends Comparable<T>> IList<T> topKSort(int k, IList<T> input) {
        if (k < 0 || input == null) {
            throw new IllegalArgumentException();
        }
        IList<T> sortedList = new DoubleLinkedList<T>();
        IPriorityQueue<T> heap = new ArrayHeap<T>();
        if (k == 0) {
            return sortedList;
        }
        if (k > input.size()) {
            k = input.size();
        } else if (k == 0) {
            return sortedList;
        }
        int i = 0;
        for (T val : input) {
            if (i < k) {
                heap.insert(val);
            } else {
                if (heap.peekMin().compareTo(val) < 0) {
                    heap.removeMin();
                    heap.insert(val);
                }
            }
            i++;
        }
        for (int j = 0; j < k; j++) {
            sortedList.add(heap.removeMin());
        }
        return sortedList;
    }
}
