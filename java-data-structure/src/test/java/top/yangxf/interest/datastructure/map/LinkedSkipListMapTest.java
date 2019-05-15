package top.yangxf.interest.datastructure.map;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LinkedSkipListMapTest {

    @Test
    public void test() {
        Map<Integer, String> map = new LinkedSkipListMap<>();

        for (int i = 0; i < 100; i++) {
            int key = ThreadLocalRandom.current().nextInt(0, 100);
            map.put(key, key + ".");
        }

        map.put(10, "hello");
        assertEquals("hello", map.get(10));
        System.out.println(map);
        map.remove(10);
        for (int i = 0; i < 100; i++) {
            int key = ThreadLocalRandom.current().nextInt(0, 100);
            map.remove(key);
        }
        assertNull(map.get(10));
        System.out.println(map);

        Map<Integer, String> skipMap = new LinkedSkipListMap<>();
        Map<Integer, String> safeSkipMap = new ConcurrentSkipListMap<>();
        Map<Integer, String> treeMap = new TreeMap<>();
        int num = 100000;

        List<Integer> keyList = new ArrayList<>(num);
        List<String> valList = new ArrayList<>(num);
        for (int i = 0; i < num; i++) {
            int key = ThreadLocalRandom.current().nextInt(0, num);
            String value = key + ".";
            keyList.add(key);
            valList.add(value);
        }

        long t1 = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            treeMap.put(keyList.get(i), valList.get(i));
        }
        long t2 = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            skipMap.put(keyList.get(i), valList.get(i));
        }
        long t3 = System.currentTimeMillis();
        for (int i = 0; i < num; i++) {
            safeSkipMap.put(keyList.get(i), valList.get(i));
        }
        long t4 = System.currentTimeMillis();

        System.out.println("PUT");
        System.out.println("TreeMap " + (t2 - t1));
        System.out.println("SkipMap " + (t3 - t2));
        System.out.println("SafeSkipMap " + (t4 - t3));

        for (int i = 0; i < 100; i++) {
            int key = ThreadLocalRandom.current().nextInt(0, num);
            skipMap.remove(key);
            safeSkipMap.remove(key);
            treeMap.remove(key);
        }

        assertEquals(treeMap.size(), skipMap.size());

        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            assertEquals(value, skipMap.get(key));
        }

        t1 = System.currentTimeMillis();
        for (Integer key : keyList) {
            String s = treeMap.get(key);
        }
        t2 = System.currentTimeMillis();
        for (Integer key : keyList) {
            String s = skipMap.get(key);
        }
        t3 = System.currentTimeMillis();
        for (Integer key : keyList) {
            String s = safeSkipMap.get(key);
        }
        t4 = System.currentTimeMillis();

        System.out.println("GET");
        System.out.println("TreeMap " + (t2 - t1));
        System.out.println("SkipMap " + (t3 - t2));
        System.out.println("SafeSkipMap " + (t4 - t3));
    }
}