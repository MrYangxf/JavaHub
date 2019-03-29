package top.yangxf.interest.datastructure.tree.heap;

import org.junit.Test;
import top.yangxf.interest.datastructure.tree.Heap;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MinHeapTest {

    @Test
    public void test() {
        Heap<Integer> h = new BinaryHeap<>(0);
        assertTrue(h.isEmpty());

        h.push(1);

        assertEquals(h.size(), 1);

        assertEquals(h.pop(), (Integer) 1);
        assertTrue(h.isEmpty());

        h.push(3);
        h.push(1);
        h.push(4);
        assertEquals(h.pop(), (Integer) 1);
        h.push(3);
        assertEquals(h.pop(), (Integer) 3);
        assertEquals(h.pop(), (Integer) 3);
        assertEquals(h.pop(), (Integer) 4);

        h = new BinaryHeap<>(new Integer[]{-1, -2, -22, -3, -4}, null);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < 100; i++) {
            h.push(random.nextInt(100));
        }

        int p = Integer.MIN_VALUE;
        while (!h.isEmpty()) {
            int c = h.pop();
            assertTrue("c=" + c + ", p=" + p, c >= p);
            p = c;
        }

        List<Integer> sortedList = new ArrayList<>();
        
        for (int i = 0; i < 100; i++) {
            int anInt = random.nextInt(100);
            h.push(anInt);
            sortedList.add(anInt);
        }

        Heap<Integer> h2 = new BinaryHeap<>(0);
        for (int i = 0; i < 100; i++) {
            int anInt = random.nextInt(100);
            h2.push(anInt);
            sortedList.add(anInt);
        }

        sortedList.sort(Comparator.comparingInt(i -> i));
        
        h.pushAll(h2);

        for (Integer i : sortedList) {
            assertEquals(i, h.pop());
        }
        

    }


}