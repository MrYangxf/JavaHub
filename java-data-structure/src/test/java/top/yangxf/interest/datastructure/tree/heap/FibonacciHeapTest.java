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

        assertEquals((Integer) 1, fh.poll());

        fh.push(5);
        fh.push(1);
        assertEquals((Integer) 1, fh.poll());
        assertEquals((Integer) 5, fh.poll());

        fh.push(8);
        fh.push(3);
        fh.push(1);
        fh.push(7);
        fh.push(-2);
        assertEquals((Integer) (-2), fh.poll());
        assertEquals((Integer) 1, fh.poll());
        assertEquals((Integer) 3, fh.poll());
        assertEquals((Integer) 7, fh.poll());
        assertEquals((Integer) 8, fh.poll());
        assertTrue(fh.isEmpty());

    }

}