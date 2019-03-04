package top.yangxf.interest.datastructure.tree.heap;

import org.junit.Test;
import top.yangxf.interest.datastructure.tree.Heap;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.Assert.*;

public class MaxHeapTest {

    @Test
    public void test() {
        Integer[] integers = {2, 4, 2, 7, 2, 3, 6, 78, 22, 3, 4};
        Heap<Integer> h = new MaxHeap<>(integers, Comparator.comparingInt(o -> o));

        assertEquals(h.size(), integers.length);

        Arrays.sort(integers);
        for (int i = integers.length - 1; i >= 0; --i) {
            assertEquals(h.poll(), integers[i]);
        }

        assertTrue(h.isEmpty());
    }

}