package datastructures.sorting;

import static org.junit.Assert.assertTrue;

import misc.BaseTest;
import datastructures.concrete.ArrayHeap;
import datastructures.interfaces.IPriorityQueue;
import org.junit.Test;

/**
 * See spec for details on what kinds of tests this class should include.
 */
public class TestArrayHeapFunctionality extends BaseTest {
    protected <T extends Comparable<T>> IPriorityQueue<T> makeInstance() {
        return new ArrayHeap<>();
    }

    @Test(timeout=SECOND)
    public void testBasicSize() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        heap.insert(3);
        assertEquals(1, heap.size());
        assertTrue(!heap.isEmpty());
    }

    @Test(timeout=SECOND)
    public void testDuplicateElements() {
        IPriorityQueue<Integer> heap = this.makeInstance();
        Integer val = 4;
        heap.insert(val);
        val = 5;
        heap.insert(val);
        assertTrue(heap.removeMin() == 4);
        assertTrue(heap.removeMin() == 4);
    }
}
