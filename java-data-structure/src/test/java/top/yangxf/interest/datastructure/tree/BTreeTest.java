package top.yangxf.interest.datastructure.tree;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;

public class BTreeTest {

    private static final int factor = 100;
    private static final int n = 100000;
    private static final int b = 100000;

    private List<Integer> keys = new ArrayList<>();
    private List<String> values = new ArrayList<>();

    private BTree<Integer, String> bTree = new BTree<>(factor);
    private TreeMap<Integer, String> treeMap = new TreeMap<>();

    @Before
    public void init() {
        keys = new ArrayList<>();
        values = new ArrayList<>();
        bTree = new BTree<>(factor);
        treeMap = new TreeMap<>();
        for (int i = 0; i < n; i++) {
            int k = ThreadLocalRandom.current().nextInt(b);
            keys.add(k);
            values.add("key-" + k);
        }
    }

    @Test
    public void remove() {
        put();

        List<Integer> rmKeys = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            int k = ThreadLocalRandom.current().nextInt(b);
            rmKeys.add(k);
        }

        long t1 = System.currentTimeMillis();
        List<String> btreeRms = new ArrayList<>();
        for (Integer rmKey : rmKeys) {
            btreeRms.add(bTree.remove(rmKey));
        }
        long t2 = System.currentTimeMillis();

        List<String> treeMapRms = new ArrayList<>();
        for (Integer rmKey : rmKeys) {
            treeMapRms.add(treeMap.remove(rmKey));
        }
        long t3 = System.currentTimeMillis();

        for (int i = 0; i < btreeRms.size(); i++) {
            assertEquals(btreeRms.get(i), treeMapRms.get(i));
        }

        System.out.println();
        System.out.println("After delete");
        System.out.println("B-Tree height: " + bTree.height());
        System.out.println("B-Tree   size: " + bTree.size());
        System.out.println("TreeMap  size: " + treeMap.size());

        System.out.println();
        System.out.println("DELETE duration: ");
        System.out.println("B-Tree : " + (t2 - t1));
        System.out.println("TreeMap: " + (t3 - t2));
        
    }

    @Test
    public void put() {

        long t1 = System.currentTimeMillis();

        for (int i = 0; i < keys.size(); i++) {
            bTree.put(keys.get(i), values.get(i));
        }

        long t2 = System.currentTimeMillis();

        for (int i = 0; i < keys.size(); i++) {
            treeMap.put(keys.get(i), values.get(i));
        }

        long t3 = System.currentTimeMillis();

        System.out.println("B-Tree factor: " + factor);
        System.out.println("B-Tree height: " + bTree.height());
        System.out.println("B-Tree   size: " + bTree.size());
        System.out.println("TreeMap  size: " + treeMap.size());
        System.out.println();
        System.out.println("PUT duration: ");
        System.out.println("B-Tree : " + (t2 - t1));
        System.out.println("TreeMap: " + (t3 - t2));

        for (Map.Entry<Integer, String> entry : treeMap.entrySet()) {
            Integer key = entry.getKey();
            assertEquals(bTree.get(key), entry.getValue());
        }

        t1 = System.currentTimeMillis();
        for (Integer key : keys) {
            String s = bTree.get(key);
        }
        t2 = System.currentTimeMillis();
        for (Integer key : keys) {
            String s = treeMap.get(key);
        }
        t3 = System.currentTimeMillis();

        System.out.println();
        System.out.println("GET duration: ");
        System.out.println("B-Tree : " + (t2 - t1));
        System.out.println("TreeMap: " + (t3 - t2));

    }
}