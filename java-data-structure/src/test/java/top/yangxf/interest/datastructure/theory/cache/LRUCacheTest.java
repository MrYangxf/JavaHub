package top.yangxf.interest.datastructure.theory.cache;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LRUCacheTest {

    @Test
    public void test() {
        LRUCache<String, String> cache = new LRUCache<>(1);

        cache.put("1", "a");
        assertEquals("a", cache.get("1"));

        assertEquals("a", cache.update("1", "aa"));

        assertEquals("aa", cache.get("1"));

        cache.remove("1");
        assertNull(cache.get("1"));

        cache.put("1", "a");
        cache.put("2", "b");
        assertNull(cache.get("1"));
        assertEquals("b", cache.get("2"));

        cache = new LRUCache<>(2);

        cache.put("1", "a");
        cache.put("2", "b");
        assertEquals("a", cache.get("1"));

        cache.put("3", "c");
        assertNull(cache.get("2"));

        cache.remove("1");
        cache.put("2", "b");
        assertEquals("b", cache.get("2"));

        cache.put("4", "d");
        assertNull(cache.get("3"));

    }

}