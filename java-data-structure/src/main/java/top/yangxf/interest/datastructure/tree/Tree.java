package top.yangxf.interest.datastructure.tree;

import top.yangxf.interest.datastructure.core.Countable;

/**
 * @author yangxf
 */
public interface Tree<K, V> extends Countable {

    void put(K key, V value);

    V remove(K key);

    V replace(K key, V newValue);

    V get(K key);

    int height();
}
