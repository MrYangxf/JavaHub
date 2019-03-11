package top.yangxf.interest.datastructure.theory;

import top.yangxf.interest.datastructure.hash.HashFunction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static top.yangxf.interest.util.common.MathUtil.nextPowerOf2;
import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * 布隆过滤器的简单实现版本
 * <p>
 * 布隆过滤器存在一定的误判
 * <p>
 * 即布隆过滤器判断不存在的元素，一定不存在
 * <p>
 * 布隆过滤器判断存在的元素，不一定存在
 * <p>
 * 误判的几率跟过滤器的容量和hash函数有关
 *
 * @author yangxf
 */
public class BloomFilter {
    private static final int LONG_SHIFT = 6;
    private static final int LONG_MASK = 63;

    /**
     * hash函数列表
     */
    private final List<HashFunction<String>> hashFunctions;

    private final long[] table;
    private final int tableMask;
    private int size;

    /**
     * @param capacity      位图数组的容量
     * @param hashFunctions hash函数列表
     * @see Builder
     */
    private BloomFilter(int capacity, List<HashFunction<String>> hashFunctions) {
        this.hashFunctions = hashFunctions;
        int cap = nextPowerOf2(capacity);
        tableMask = (cap << LONG_SHIFT) - 1;
        table = new long[cap];
    }

    public static Builder builder(int capacity) {
        if (capacity < 1) {
            throw new IllegalStateException("capacity must be > 0");
        }

        return new Builder(capacity);
    }

    public void add(String element) {
        checkNotNull(element, "element");

        // 分别求hash值，然后利用位图的思想设置hash值
        for (HashFunction<String> hashFunction : hashFunctions) {
            int key = hashFunction.hash(element) & tableMask;
            table[key >>> LONG_SHIFT] |= (1 << (key & LONG_MASK));
        }
        size++;
    }

    public boolean contains(String element) {
        if (element == null) {
            return false;
        }

        for (HashFunction<String> hashFunction : hashFunctions) {
            int key = hashFunction.hash(element) & tableMask;
            // 只要有一个hash值未命中位图，element就肯定不存在
            // 如果所有hash值都命中了，也不能element确保一定存在，会有一定的误判
            if ((table[key >>> LONG_SHIFT] & (1 << (key & LONG_MASK))) == 0) {
                return false;
            }
        }
        return true;
    }

    public List<HashFunction<String>> getHashFunctions() {
        return hashFunctions;
    }

    public int size() {
        return size;
    }

    /**
     * 这里只是为了方便构建HashFunction
     */
    public static class Builder {
        private int capacity;
        private List<HashFunction<String>> hashFunctions = new ArrayList<>();

        private Builder(int capacity) {
            this.capacity = capacity;
        }

        public Builder addHashFunction(HashFunction<String> function) {
            hashFunctions.add(function);
            return this;
        }

        public BloomFilter build() {
            if (hashFunctions.isEmpty()) {
                addDefaultHashFunction();
            }
            return new BloomFilter(capacity, Collections.unmodifiableList(hashFunctions));
        }

        private void addDefaultHashFunction() {
            // Java String Hash Function
            hashFunctions.add(String::hashCode);

            // SDBM Hash Function
            hashFunctions.add(key -> {
                if (key == null || key.isEmpty()) {
                    return 0;
                }

                int hash = 0;
                for (int i = 0; i < key.length(); i++) {
                    hash = key.charAt(i) + (hash << 6) + (hash << 16) - hash;
                }
                hash &= 0x7ffffff;
                return hash;
            });

            // Robert Sedgwicks Hash Function
            hashFunctions.add(key -> {
                if (key == null || key.isEmpty()) {
                    return 0;
                }

                int hash = 0;
                int magic = 63689;
                for (int i = 0; i < key.length(); i++) {
                    hash = hash * magic + key.charAt(i);
                    magic *= 378551;
                }
                return hash;
            });

            // Arash Partow Hash Function
            hashFunctions.add(key -> {
                if (key == null || key.isEmpty()) {
                    return 0;
                }

                int hash = 0;
                for (int i = 0; i < key.length(); i++) {
                    char ch = key.charAt(i);
                    if ((i & 1) == 0) {
                        hash ^= ((hash << 7) ^ ch ^ (hash >> 3));
                    } else {
                        hash ^= (~((hash << 11) ^ ch ^ (hash >> 5)));
                    }
                }
                return hash;
            });
        }

    }

}
