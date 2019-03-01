package top.yangxf.interest.datastructure.theory;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MultiArrayLinkedListTest {

    @Test
    public void test() {
        MultiArrayLinkedList<String> list = new MultiArrayLinkedList<>(0);

        assertTrue(list.isEmpty());
        printFree(list);
        
        list.add("hello");
        assertEquals(list.get(0), "hello");
        printFree(list);

        list.remove("hello");
        assertTrue(list.isEmpty());
        printFree(list);
        
        list.add("hello");
        list.add("word");
        assertEquals(list.get(0), "hello");
        assertEquals(list.get(1), "word");
        printFree(list);

        list.removeAt(0);
        assertEquals(list.get(0), "word");
        printFree(list);

        list.insert(0, "hello");
        assertEquals(list.get(0), "hello");
        printFree(list);

        list.remove("hello");
        assertEquals(list.get(0), "word");
        printFree(list);

        list.removeAt(0);
        assertTrue(list.isEmpty());
        printFree(list);
        
        list.add("this");
        list.add("test");
        list.insert(1, "is");
        assertEquals(list.toString(), "this -> is -> test");
        printFree(list);
        
        list.insert(2, "a");
        assertEquals(list.toString(), "this -> is -> a -> test");
        printFree(list);

        list.removeAt(2);
        list.replace(2, "ok");
        assertEquals(list.toString(), "this -> is -> ok");
        printFree(list);
    }

    private void printFree(MultiArrayLinkedList<String> list) {
        System.out.println("Indices of free : " + Arrays.toString(list.freeDataIndices()));
    }

}