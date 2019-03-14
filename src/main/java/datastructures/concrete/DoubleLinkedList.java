package datastructures.concrete;

import datastructures.interfaces.IList;
import misc.exceptions.EmptyContainerException;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleLinkedList<T> implements IList<T> {

    private Node<T> front;
    private Node<T> back;
    private int size;

    public DoubleLinkedList() {
        this.front = null;
        this.back = null;
        this.size = 0;
    }

    @Override
    public void add(T item) {
        Node<T> newNode = new Node<T>(back, item);
        if (size == 0) {
            front = newNode;
        } else {
            back.next = newNode;
        }
        back = newNode;
        size++;
    }

    @Override
    public T remove() {
        if (back == null) {
            throw new EmptyContainerException();
        } else {
            Node<T> curr = back;
            T item = curr.data;
            if (back.prev != null) {
                back = back.prev;
                back.next = null;
            } else {
                back = null;
                front = null;
            }
            size--;
            return item;
        }
    }

    // helper method, gets node at index
    // exceptions are handled in caller
    private Node<T> getNode(int index) {
        Node<T> curr = null;
        if (index < size) {
            if (index > size / 2) {
                curr = back;
                while (size - index - 1 > 0) {
                    index++;
                    curr = curr.prev;
                }
            } else {
                curr = front;
                while (index > 0) {
                    index--;
                    curr = curr.next;
                }
            }
        }
        return curr;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return getNode(index).data;
    }

    @Override
    public void set(int index, T item) {
        if (index < 0 || index >= this.size()) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> newNode = new Node<T>(item);
        Node<T> curr = front;
        if (front == null || front.next == null) { // setting on an empty list or list with only one element
            front = newNode;
            back = newNode;
        } else {
            if (index == 0) { // front of the list
                newNode.next = curr.next;
                curr.next.prev = newNode;
                front = newNode;
            } else if (index == size - 1) { // back of the list
                curr = back;
                newNode.prev = curr.prev;
                curr.prev.next = newNode;
                back = newNode;
            } else { // middle of the list
                while (index > 0) {
                    index--;
                    curr = curr.next;
                }
                newNode.prev = curr.prev;
                newNode.next = curr.next;
                curr.prev.next = newNode;
                curr.next.prev = newNode;
            }
        }


    }

    @Override
    public void insert(int index, T item) {
        if (index < 0 || index >= size + 1) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> newNode = new Node<T>(item);
        Node<T> nodeAtIndex = getNode(index);
        if (nodeAtIndex == null) { // inserting in empty list or past back
            if (index == 0) { // empty list
                front = newNode;
            } else { // past back
                newNode.prev = back;
                back.next = newNode;
            }
            back = newNode; // set new back pointer regardless
        } else { // non empty list or inserting in middle
            newNode.next = nodeAtIndex;
            if (nodeAtIndex == front) { // insert node at front
                front = newNode;
            } else { // insert node in middle
                newNode.prev = nodeAtIndex.prev;
                newNode.prev.next = newNode;
            }
            nodeAtIndex.prev = newNode;
        }
        size++;
    }

    @Override
    public T delete(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> nodeToDelete = getNode(index);
        if (nodeToDelete == front && nodeToDelete == back) {
            front = null;
            back = null;
        } else if (nodeToDelete == front) {
            front = nodeToDelete.next;
            front.prev = null;
        } else if (nodeToDelete == back) {
            back = nodeToDelete.prev;
            back.next = null;
        } else {
            nodeToDelete.next.prev = nodeToDelete.prev;
            nodeToDelete.prev.next = nodeToDelete.next;
        }
        size--;
        return nodeToDelete.data;
    }


    @Override
    public int indexOf(T item) {
        Node<T> curr = front;
        int index = 0;
        while (index < size) {
            if (item != null ? item.equals(curr.data) : item == curr.data) {
                return index;
            }
            index++;
            curr = curr.next;
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(T other) {
        return indexOf(other) >= 0;
    }

    @Override
    public Iterator<T> iterator() {
        // Note: we have provided a part of the implementation of
        // an iterator for you. You should complete the methods stubs
        // in the DoubleLinkedListIterator inner class at the bottom
        // of this file. You do not need to change this method.
        return new DoubleLinkedListIterator<>(this.front);
    }

    private static class Node<E> {
        // You may not change the fields in this node or add any new fields.
        public final E data;
        public Node<E> prev;
        public Node<E> next;

        public Node(Node<E> prev, E data, Node<E> next) {
            this.data = data;
            this.prev = prev;
            this.next = next;
        }

        public Node(Node<E> prev, E data) {
            this(prev, data, null);
        }

        public Node(E data) {
            this(null, data, null);
        }

        // Feel free to add additional constructors or methods to this class.
    }

    private static class DoubleLinkedListIterator<T> implements Iterator<T> {
        // You should not need to change this field, or add any new fields.
        private Node<T> current;

        public DoubleLinkedListIterator(Node<T> current) {
            // You do not need to make any changes to this constructor.
            this.current = current;
        }

        /**
         * Returns 'true' if the iterator still has elements to look at;
         * returns 'false' otherwise.
         */
        public boolean hasNext() {
            return current != null;
        }

        /**
         * Returns the next item in the iteration and internally updates the
         * iterator to advance one element forward.
         *
         * @throws NoSuchElementException if we have reached the end of the iteration and
         *         there are no more elements to look at.
         */
        public T next() {
            if (current == null) {
                throw new NoSuchElementException();
            }
            T data = current.data;
            current = current.next;
            return data;
        }
    }
}
