package top.yangxf.interest.datastructure.theory.cache;

/**
 * @author yangxf
 */
public interface Cache<K, V> {

    void put(K key, V value);

    void remove(K key);

    V update(K key, V newValue);

    V get(K key);

    default boolean contains(K key) {
        return get(key) != null;
    }

}
