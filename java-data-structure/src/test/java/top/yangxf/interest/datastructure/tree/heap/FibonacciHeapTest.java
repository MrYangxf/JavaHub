package top.yangxf.interest.datastructure.tree.heap;

import org.junit.Test;

import static org.junit.Assert.*;

public class FibonacciHeapTest {

    @Test
    public void test() {
        FibonacciHeap<Integer> fh = new FibonacciHeap<>();
        assertTrue(fh.isEmpty());

        fh.push(1);
        assertFalse(fh.isEmpty());
        assertEquals((Integer) 1, fh.peek());

        assertEquals((Integer) 1, fh.pop());

        fh.push(5);
        fh.push(1);
        assertEquals((Integer) 1, fh.pop());
        assertEquals((Integer) 5, fh.pop());

        fh.push(8);
        fh.push(3);
        fh.push(1);
        fh.push(7);
        fh.push(-2);
        assertEquals((Integer) (-2), fh.pop());
        assertEquals((Integer) 1, fh.pop());
        assertEquals((Integer) 3, fh.pop());
        assertEquals((Integer) 7, fh.pop());
        assertEquals((Integer) 8, fh.pop());
        assertTrue(fh.isEmpty());

    }

}