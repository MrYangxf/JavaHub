package top.yangxf.interest.datastructure.linear;

import org.junit.Test;

import static org.junit.Assert.*;

public class DynamicArrayListTest {

    @Test
    public void test() {
        int n = 100;
        List<String> list = new DynamicArrayList<>();

        assertTrue(list.isEmpty());

        for (int i = 0; i < n; i++) {
            list.add(String.valueOf(i));
            String old = list.replace(i, i + "*");
            assertEquals(old, String.valueOf(i));
            assertEquals(list.size(), i + 1);
        }

        int index = list.indexOf("50*");
        assertEquals(index, 50);
        String e = list.removeAt(index);
        assertFalse(list.contains(e));
        assertEquals(list.get(50), "51*");

        for (; !list.isEmpty(); ) {
            list.removeAt(0);
        }

        assertTrue(list.isEmpty());
        
    }
}