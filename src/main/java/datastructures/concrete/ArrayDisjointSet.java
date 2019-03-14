package datastructures.concrete;

import java.util.Arrays;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.interfaces.IDictionary;
import datastructures.interfaces.IDisjointSet;

/**
 * @see IDisjointSet for more details.
 */
public class ArrayDisjointSet<T> implements IDisjointSet<T> {
    // Note: do NOT rename or delete this field. We will be inspecting it
    // directly within our private tests.
    private static final int INIT_CAPACITY = 10;
    private int[] pointers;
    IDictionary<T, Integer> intReps;
    int size;

    // However, feel free to add more methods and private helper methods.
    // You will probably need to add one or two more fields in order to
    // successfully implement this class.

    public ArrayDisjointSet() {
        pointers = new int[INIT_CAPACITY];
        intReps = new ChainedHashDictionary<T, Integer>();
        size = 0;
    }

    @Override
    public void makeSet(T item) {
        if (intReps.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        if (size >= pointers.length) {
            increaseCapacity();
        }
        intReps.put(item, size);
        pointers[size] = -1;
        size++;
    }

    private void increaseCapacity() {
        pointers = Arrays.copyOf(pointers, pointers.length * 2);
    }

    @Override
    public int findSet(T item) {
        if (!intReps.containsKey(item)) {
            throw new IllegalArgumentException();
        }
        int location = intReps.get(item);
        return exploreAndUpdateRoots(location);
    }

    private int exploreAndUpdateRoots(int location) {
        if (pointers[location] < 0) {
            return location;
        } else {
            pointers[location] = exploreAndUpdateRoots(pointers[location]);
            return pointers[location];
        }
    }

    @Override
    public void union(T item1, T item2) {
        if (!intReps.containsKey(item1) || !intReps.containsKey(item2)) {
            throw new IllegalArgumentException();
        }
        int root1 = this.findSet(item1);
        int root2 = this.findSet(item2);
        if (root1 != root2) {
            if (pointers[root1] < pointers[root2]) { // root 1 greater rank
                pointers[root2] = root1;
            } else if (pointers[root1] > pointers[root2]) { // root 2 greater rank
                pointers[root1] = root2;
            } else { // root1 and root2 ranks equal
                pointers[root1] = root2;
                pointers[root2]--;
            }
        }
    }
}
