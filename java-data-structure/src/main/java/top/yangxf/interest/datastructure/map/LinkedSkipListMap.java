package top.yangxf.interest.datastructure.map;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;
import static top.yangxf.interest.util.common.StringUtil.fill;

/**
 * @author yangxf
 */
public class LinkedSkipListMap<K, V> extends AbstractMap<K, V> {

    /**
     * 用作头索引列的节点的哨兵对象
     */
    private static final Object HEAD_OBJ = new Object();

    private Comparator<K> comparator;

    /**
     * 头索引
     */
    private HeadIndex<K, V> headIndex = new HeadIndex<>(1, null);

    private int size;

    public LinkedSkipListMap() {
    }

    public LinkedSkipListMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return findNode(key) != null;
    }

    @Override
    public boolean containsValue(Object value) {
        checkNotNull(value, "value");
        Node<K, V> node = headIndex.node.next;
        while (node != null) {
            if (value.equals(node.value)) {
                return true;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public V get(Object key) {
        Node<K, V> node = findNode(key);
        return node == null ? null : node.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V put(K key, V value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");

        Node<K, V> prevNode = findPredecessor(key);
        // 如果已经存在key，覆盖原值，返回旧的值
        if (prevNode.next != null &&
            prevNode.next.key.equals(key)) {
            V oldVal = prevNode.next.value;
            prevNode.next.value = value;
            return oldVal;
        }

        // 创建新节点，链接到前驱节点
        Node<K, V> newNode = new Node<>(key, value);
        link(prevNode, newNode);
        size++;

        // 随机为一半的节点建立索引
        int rn = ThreadLocalRandom.current().nextInt();
        if ((rn & 1) == 0) {
            /*
             * 建立索引的等级level = rn末尾连续为1的位数
             * 第level级索引的数量 = 节点数量 / (2 ^ level)
             */
            int level = 1;
            while (((rn >>>= 1) & 1) != 0) {
                level++;
            }

            HeadIndex<K, V> h = headIndex;
            int maxLevel = h.level;
            // 如果大于跳表原来的最大层级，新建一个头索引
            if (level > maxLevel) {
                level = maxLevel + 1;
                headIndex = new HeadIndex<>(level, h);
            }

            // 为newNode创建一个新的索引列
            Index<K, V> prev = null;
            Index<K, V>[] indices = (Index<K, V>[]) new Index[level + 1];
            for (int i = 1; i <= level; i++) {
                indices[i] = prev = new Index<>(key, prev, newNode);
            }

            Index<K, V> cIndex = headIndex;
            maxLevel = headIndex.level;

            // 从头索引开始，查询每一层新索引列的前驱索引，然后链接
            for (int i = maxLevel; i >= 1; i--) {
                Index<K, V> prevIndex = cIndex,
                        rIndex = cIndex.right;

                // 当前层级向右查询
                while (rIndex != null &&
                       compare(key, rIndex.key) > 0) {
                    prevIndex = rIndex;
                    rIndex = rIndex.right;
                }

                // 当前层级小于等于新建索引层级时，链接到前驱索引上
                if (i <= level) {
                    link(prevIndex, indices[i]);
                }
                cIndex = prevIndex.down;
            }

        }
        return null;
    }

    @Override
    public V remove(Object key) {
        checkNotNull(key, "key");
        Index<K, V> cIndex = headIndex;
        Node<K, V> prevNode = cIndex.node;

        // 从头索引开始查询key
        while (cIndex != null) {
            Index<K, V> rIndex = cIndex.right;

            // 当前索引层向右查询
            if (rIndex != null) {
                int c = compare(key, rIndex.key);
                if (c > 0) {
                    cIndex = rIndex;
                    continue;
                } else if (c == 0) { // 索引层匹配到key，需要删除索引
                    unlink(cIndex, rIndex);
                    // 如果删除索引后，当前层只剩下头索引的话，需要删除该索引层
                    if (cIndex == headIndex &&
                        cIndex.right == null) {
                        headIndex = (HeadIndex<K, V>) cIndex.down;
                        cIndex.down = null;
                        cIndex = headIndex;
                        continue;
                    }
                }
            }
            prevNode = cIndex.node;
            cIndex = cIndex.down;
        }

        // 删除节点
        Node<K, V> cNode = prevNode.next;
        while (cNode != null) {
            int c = compare(key, cNode.key);
            if (c < 0) {
                break;
            }
            if (c == 0) {
                V value = cNode.value;
                unlink(prevNode, cNode);
                size--;
                return value;
            }
            prevNode = cNode;
            cNode = cNode.next;
        }

        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        checkNotNull(m, "map");
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        headIndex = new HeadIndex<>(1, null);
        size = 0;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> entrySet = new LinkedHashSet<>();
        Node<K, V> cNode = headIndex.node.next;
        while (cNode != null) {
            entrySet.add(cNode);
            cNode = cNode.next;
        }
        return entrySet;
    }

    @Override
    public String toString() {
        if (size > 128) {
            return super.toString();
        }

        StringBuilder buf = new StringBuilder();
        buf.append(" SIZE ").append(size)
           .append(" MAX_LEVEL ").append(headIndex.level).append('\n');

        int maxKeyLen = 0;
        Node<K, V> cNode = headIndex.node.next;
        while (cNode != null) {
            String key = String.valueOf(cNode.key);
            if (key.length() > maxKeyLen) {
                maxKeyLen = key.length();
            }
            cNode = cNode.next;
        }

        Map<K, Integer> offsetMap = new HashMap<>();
        cNode = headIndex.node.next;
        int offset = 0;
        while (cNode != null) {
            offsetMap.put(cNode.key, offset);
            offset += 4 + maxKeyLen;
            cNode = cNode.next;
        }

        Index<K, V> h = headIndex;
        while (h != null) {
            buf.append("INDEX ----");
            Index<K, V> r = h.right;
            StringBuilder line = new StringBuilder();
            while (r != null) {
                Integer off = offsetMap.get(r.key);
                if (off == null) {
                    throw new IllegalStateException("index error");
                }
                String source = String.valueOf(r.key);
                String keyStr = fill(source, maxKeyLen - source.length(), ' ', true);
                line.append(fill(keyStr, off - line.length(), '-', false));
                r = r.right;
            }
            buf.append(line).append('\n');
            h = h.down;
        }

        Node<K, V> n = headIndex.node.next;
        buf.append("  KEY ----");
        while (n != null) {
            String source = String.valueOf(n.key);
            String keyStr = fill(source, maxKeyLen - source.length(), ' ', true);
            buf.append(keyStr);
            n = n.next;
            if (n != null) {
                buf.append("----");
            }
        }

        n = headIndex.node.next;
        buf.append("\nVALUE     ");
        while (n != null) {
            String source = String.valueOf(n.value);
            int length = source.length(), maxValueLen = maxKeyLen + 4;
            String valStr = length >= maxValueLen ? source.substring(0, maxValueLen - 1) + ' ' :
                    fill(source, maxValueLen - length, ' ', true);
            buf.append(valStr);
            n = n.next;
        }

        return buf.toString();
    }

    @SuppressWarnings("unchecked")
    private Node<K, V> findNode(Object key) {
        checkNotNull(key, "key");
        Index<K, V> cIndex = headIndex;
        Node<K, V> prevNode = cIndex.node;
        // 从头索引开始往下一层一层搜索
        while (cIndex != null) {
            // 当前索引层向右搜索
            Index<K, V> rIndex = cIndex.right;
            if (rIndex != null) {
                int cp = compare(key, rIndex.key);
                if (cp > 0) {
                    cIndex = rIndex;
                    continue;
                } else if (cp == 0) { // 快速跳出
                    return rIndex.node;
                }
            }
            prevNode = cIndex.node;
            cIndex = cIndex.down;
        }

        Node<K, V> cNode = prevNode.next;
        while (cNode != null) {
            int c = compare(key, cNode.key);
            if (c < 0) {
                break;
            }
            if (c == 0) {
                return cNode;
            }
            cNode = cNode.next;
        }

        return null;
    }

    /**
     * 查找key的前驱节点
     */
    private Node<K, V> findPredecessor(K key) {
        Node<K, V> prevNode = null;
        Index<K, V> cIndex = headIndex;
        while (cIndex != null) {
            Index<K, V> rIndex = cIndex.right;
            if (rIndex != null) {
                int c = compare(key, rIndex.key);
                if (c > 0) {
                    cIndex = rIndex;
                    continue;
                } else if (c == 0) {
                    prevNode = cIndex.node;
                    break;
                }
            }
            prevNode = cIndex.node;
            cIndex = cIndex.down;
        }

        while (prevNode != null &&
               prevNode.next != null &&
               compare(key, prevNode.next.key) > 0) {
            prevNode = prevNode.next;
        }
        return prevNode;
    }

    @SuppressWarnings("unchecked")
    private int compare(Object k1, Object k2) {
        return comparator == null ?
                ((Comparable) k1).compareTo(k2) :
                comparator.compare((K) k1, (K) k2);
    }

    private static void link(Node prev, Node current) {
        current.next = prev.next;
        prev.next = current;
    }

    private static void link(Index prev, Index current) {
        current.right = prev.right;
        prev.right = current;
    }

    private static void unlink(Node prev, Node current) {
        prev.next = current.next;
        current.next = null;
    }

    private static void unlink(Index prev, Index current) {
        prev.right = current.right;
        current.right = null;
    }

    static class Node<K, V> implements Entry<K, V> {
        K key;
        V value;
        Node<K, V> next;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V oldVal = this.value;
            this.value = value;
            return oldVal;
        }
    }

    static class Index<K, V> {
        K key;
        Node<K, V> node;
        Index<K, V> right, down;

        Index(K key, Index<K, V> down, Node<K, V> node) {
            this.key = key;
            this.down = down;
            this.node = node;
        }
    }

    @SuppressWarnings("unchecked")
    static class HeadIndex<K, V> extends Index<K, V> {
        int level;

        HeadIndex(int level, Index<K, V> down) {
            super(null, down, down == null ? new Node<>(null, (V) HEAD_OBJ) : down.node);
            this.level = level;
        }
    }

}
