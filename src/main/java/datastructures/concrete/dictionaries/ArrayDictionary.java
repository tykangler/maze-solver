package datastructures.concrete.dictionaries;

import datastructures.concrete.KVPair;
import datastructures.interfaces.IDictionary;
import misc.exceptions.NoSuchKeyException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayDictionary<K, V> implements IDictionary<K, V> {
    private Pair<K, V>[] pairs;
    private static final int INIT_CAPACITY = 16;
    private int size;

    public ArrayDictionary() {
        pairs = makeArrayOfPairs(INIT_CAPACITY);
        size = 0;
    }

    @SuppressWarnings("unchecked")
    private Pair<K, V>[] makeArrayOfPairs(int arraySize) {
        return (Pair<K, V>[]) (new Pair[arraySize]);
    }

    @Override
    public V get(K key) {
        int keyIndex = indexOf(key);
        if (keyIndex < 0) {
            throw new NoSuchKeyException();
        }
        return pairs[keyIndex].value;
    }

    @Override
    public void put(K key, V value) {
        if (size >= pairs.length) {
            increaseCapacity();
        }
        int keyIndex = indexOf(key);
        if (keyIndex < 0) {
            pairs[size] = new Pair<K, V>(key, value);
            size++;
        } else {
            pairs[keyIndex].value = value;
        }
    }

    @Override
    public V remove(K key) {
        int targetIndex = indexOf(key);
        if (targetIndex < 0) {
            throw new NoSuchKeyException();
        }
        V removeValue = pairs[targetIndex].value;
        size--;
        if (targetIndex < size) {
            pairs[targetIndex] = pairs[size];
        }
        return removeValue;
    }

    @Override
    public boolean containsKey(K key) {
        return indexOf(key) >= 0;
    }

    @Override
    public int size() {
        return size;
    }

    private int indexOf(K key) {
        for (int i = 0; i < size; i++) {
            K currKey = pairs[i].key;
            if (key != null ? key.equals(currKey) : key == currKey) {
                return i;
            }
        }
        return -1;
    }

    private void increaseCapacity() {
        Pair<K, V>[] newArray = makeArrayOfPairs(pairs.length * 2);
        for (int i = 0; i < pairs.length; i++) {
            newArray[i] = pairs[i];
        }
        pairs = newArray;
    }

    @Override
    public Iterator<KVPair<K, V>> iterator() {
        return new ArrayDictionaryIterator<K, V>(pairs, size);
    }

    private static class ArrayDictionaryIterator<K, V> implements Iterator<KVPair<K, V>> {

        private int index;
        private Pair<K, V>[] pairs;
        private int size;

        public ArrayDictionaryIterator(Pair<K, V>[] pairs, int size) {
            this.index = 0;
            this.pairs = pairs;
            this.size = size;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return index < size;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public KVPair<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            index++;
            return new KVPair<K, V>(pairs[index - 1].key, pairs[index - 1].value);
        }
    }

    private static class Pair<K, V> {
        public K key;
        public V value;

        // You may add constructors and methods to this class as necessary.
        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return this.key + "=" + this.value;
        }
    }
}
