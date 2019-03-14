package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @see IDictionary and the assignment page for more details on what each method should do
 */
public class ChainedHashDictionary<K, V> implements IDictionary<K, V> {
    // You may not change or rename this field: we will be inspecting
    // it using our private tests.
    private IDictionary<K, V>[] chains;
    private static final int INIT_CAPACITY = 10;
    private int currSize;

    // You're encouraged to add extra fields (and helper methods) though!

    public ChainedHashDictionary() {
        chains = makeArrayOfChains(INIT_CAPACITY);
        currSize = 0;
        fillArrayOfChains(chains);
    }

    /**
     * This method will return a new, empty array of the given size
     * that can contain IDictionary<K, V> objects.
     *
     * Note that each element in the array will initially be null.
     */
    @SuppressWarnings("unchecked")
    private IDictionary<K, V>[] makeArrayOfChains(int size) {
        return (IDictionary<K, V>[]) new IDictionary[size];
    }

    @Override
    public V get(K key) {
        int keyIndex = 0;
        if (key != null) {
            keyIndex = Math.abs(key.hashCode() % chains.length);
        }
        return chains[keyIndex].get(key);
    }

    @Override
    public void put(K key, V value) {
        int loadFactor = currSize / chains.length;
        if (loadFactor > 1) { // if load factor, n/c, is larger than 1, then resize the array
            increaseCapacity();
        }
        if (!containsKey(key)) { // if the key is not already contained, increase size
            currSize++;
        }
        int hashCode = 0;
        if (key != null) {
            hashCode = Math.abs(key.hashCode() % chains.length);
        }
        chains[hashCode].put(key, value);
    }

    @Override
    public V remove(K key) {
        int keyIndex = 0;
        if (key != null) {
            keyIndex = Math.abs(key.hashCode() % chains.length);
        }
        currSize--;
        return chains[keyIndex].remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        int hashCode = 0;
        if (key != null) {
            hashCode = Math.abs(key.hashCode() % chains.length);
        }
        return chains[hashCode].containsKey(key);
    }

    @Override
    public int size() {
        return currSize;
    }

    private void increaseCapacity() {
        IDictionary<K, V>[] replace = makeArrayOfChains(2 * chains.length);
        fillArrayOfChains(replace);
        for (int i = 0; i < chains.length; i++) {
            for (KVPair<K, V> next : chains[i]) {
                int hashCode = 0;
                if (next.getKey() != null) {
                    hashCode = Math.abs(next.getKey().hashCode() % replace.length);
                }
                replace[hashCode].put(next.getKey(), next.getValue());
            }
        }
        chains = replace;
    }

    private void fillArrayOfChains(IDictionary<K, V>[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            dictionary[i] = new ArrayDictionary<K, V>();
        }
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        // Note: you do not need to change this method
        return new ChainedIterator<>(this.chains);
    }

    /*
     * Hints:
     *
     * 1. You should add extra fields to keep track of your iteration
     *    state. You can add as many fields as you want. If it helps,
     *    our reference implementation uses three (including the one we
     *    gave you).
     *
     * 2. Before you try and write code, try designing an algorithm
     *    using pencil and paper and run through a few examples by hand.
     *
     *    We STRONGLY recommend you spend some time doing this before
     *    coding. Getting the invariants correct can be tricky, and
     *    running through your proposed algorithm using pencil and
     *    paper is a good way of helping you iron them out.
     *
     * 3. Think about what exactly your *invariants* are. As a
     *    reminder, an *invariant* is something that must *always* be
     *    true once the constructor is done setting up the class AND
     *    must *always* be true both before and after you call any
     *    method in your class.
     *
     *    Once you've decided, write them down in a comment somewhere to
     *    help you remember.
     *
     *    You may also find it useful to write a helper method that checks
     *    your invariants and throws an exception if they're violated.
     *    You can then call this helper method at the start and end of each
     *    method if you're running into issues while debugging.
     *
     *    (Be sure to delete this method once your iterator is fully working.)
     *
     * Implementation restrictions:
     *
     * 1. You **MAY NOT** create any new data structures. Iterators
     *    are meant to be lightweight and so should not be copying
     *    the data contained in your dictionary to some other data
     *    structure.
     *
     * 2. You **MAY** call the `.iterator()` method on each IDictionary
     *    instance inside your 'chains' array, however.
     */
    private static class ChainedIterator<K, V> implements Iterator<KVPair<K, V>> {
        private IDictionary<K, V>[] chains;
        private int currIndex;
        private Iterator<KVPair<K, V>> dictIterator;

        public ChainedIterator(IDictionary<K, V>[] chains) {
            this.chains = chains;
            currIndex = 0;
            dictIterator = chains[0].iterator();
        }

        @Override
        public boolean hasNext() {
            return dictIterator.hasNext() || hasNextIterator();
        }

        @Override
        public KVPair<K, V> next() {
            if (this.hasNext()) {
                return dictIterator.next();
            }
            throw new NoSuchElementException();
        }

        private boolean hasNextIterator() {
            for (int i = currIndex + 1; i < chains.length; i++) {
                Iterator<KVPair<K, V>> iter = chains[i].iterator();
                if (iter.hasNext()) {
                    currIndex = i;
                    dictIterator = chains[i].iterator();
                    return true;
                }
            }
            return false;
        }
    }
}
