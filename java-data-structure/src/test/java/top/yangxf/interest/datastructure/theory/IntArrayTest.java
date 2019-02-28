package top.yangxf.interest.datastructure.theory;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntArrayTest {
    @Test
    public void test() {
        IntArray intArray = new IntArray(10);
        for (int i = 0; i < intArray.length(); i++) {
            assertEquals(0, intArray.get(i));
            intArray.set(i, i);
            assertEquals(i, intArray.get(i));
        }
    }
}