package top.yangxf.interest.datastructure.theory.cache;

import java.util.HashMap;
import java.util.Map;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * @author yangxf
 */
public class LRUCache<K, V> implements Cache<K, V> {

    private final Map<K, Node<K, V>> dataMap;
    private final int capacity;
    private int size;
    private Node<K, V> head;
    private Node<K, V> tail;

    /**
     * @param capacity if the capacity is exceeded, the least recently used data is removed
     */
    public LRUCache(int capacity) {
        if (capacity < 1) {
            throw new IllegalStateException("capacity must be > 0");
        }
        this.capacity = capacity;
        dataMap = new HashMap<>(capacity);
    }

    @Override
    public void put(K key, V value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");
        Node<K, V> newNode = new Node<>(key, value);
        if (dataMap.putIfAbsent(key, newNode) == null) {
            if (size == capacity) {
                dataMap.remove(unlink(head).key);
            } else {
                size++;
            }
            linkToTail(newNode);
        }
    }

    @Override
    public void remove(K key) {
        checkNotNull(key, "key");
        Node<K, V> rmNode = dataMap.remove(key);
        if (rmNode != null) {
            unlink(rmNode);
            size--;
        }
    }

    @Override
    public V update(K key, V newValue) {
        checkNotNull(key, "key");
        checkNotNull(newValue, "value");
        V oldValue = null;
        Node<K, V> node = dataMap.get(key);
        if (node != null) {
            oldValue = node.data;
            node.data = newValue;
            linkToTail(unlink(node));
        }
        return oldValue;
    }

    @Override
    public V get(K key) {
        checkNotNull(key, "key");
        V value = null;
        Node<K, V> node = dataMap.get(key);
        if (node != null) {
            value = node.data;
            linkToTail(unlink(node));
        }
        return value;
    }

    private void linkToTail(Node<K, V> node) {
        if (tail == null) {
            head = tail = node;
        } else {
            tail.next = node;
            node.prev = tail;
            tail = node;
        }
    }

    private Node<K, V> unlink(Node<K, V> node) {
        Node<K, V> p = node.prev,
                n = node.next;
        if (p != null) {
            p.next = n;
        }

        if (n != null) {
            n.prev = p;
        }

        if (p == null) {
            head = n;
        }

        if (n == null) {
            tail = p;
        }

        node.prev = null;
        node.next = null;
        return node;
    }

    static class Node<K, V> {
        Node<K, V> prev, next;
        K key;
        V data;

        Node(K key, V data) {
            this.key = key;
            this.data = data;
        }
    }
}
