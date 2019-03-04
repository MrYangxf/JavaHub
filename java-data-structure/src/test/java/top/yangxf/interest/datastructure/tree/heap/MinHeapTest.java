package top.yangxf.interest.datastructure.tree.heap;

import org.junit.Test;
import top.yangxf.interest.datastructure.tree.Heap;

import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MinHeapTest {

    @Test
    public void test() {
        Heap<Integer> h = new MinHeap<>(0);
        assertTrue(h.isEmpty());

        h.push(1);

        assertEquals(h.size(), 1);

        assertEquals(h.poll(), (Integer) 1);
        assertTrue(h.isEmpty());

        h.push(3);
        h.push(1);
        h.push(4);
        assertEquals(h.poll(), (Integer) 1);
        h.push(3);
        assertEquals(h.poll(), (Integer) 3);
        assertEquals(h.poll(), (Integer) 3);
        assertEquals(h.poll(), (Integer) 4);

        h = new MinHeap<>(new Integer[]{-1, -2, -22, -3, -4}, null);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            h.push(random.nextInt(100));
        }

        int p = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int c = h.poll();
            assertTrue("c=" + c + ", p=" + p, c >= p);
            p = c;
        }
    }


}