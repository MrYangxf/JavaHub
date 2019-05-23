package top.yangxf.interest.datastructure.tree;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.StringJoiner;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;
import static top.yangxf.interest.util.common.ObjectUtil.compare;

/**
 * 一个纯内存的B-Tree实现，没有磁盘操作
 *
 * @author yangxf
 */
public class BTree<K, V> implements Tree<K, V> {

    /**
     * 分支因子，B树节点的最小出度数，
     * 表示每个节点最少拥有factor个子节点，
     * 每个节点最多有 2 * factor - 1 个key 和 2 * factor 个子节点，
     * factor越大，B-Tree的高度越低，
     * factor必须大于等于2，当等于2时，就是一颗2-3-4树。
     */
    private final int factor;

    private Node<K, V> root;

    private int size;

    private int height;

    private Comparator<K> comparator;

    public BTree(int factor) {
        this(factor, null);
    }

    public BTree(int factor, Comparator<K> comparator) {
        if (factor < 2) {
            throw new IllegalArgumentException("factor must be >= 2");
        }
        this.factor = factor;
        this.comparator = comparator;
    }

    @Override
    public void put(K key, V value) {
        checkNotNull(key, "key");
        checkNotNull(value, "value");
        if (root == null) {
            root = new Node<>(factor);
            insertToNode(root, key, value);
        } else {
            insert(key, value);
        }
    }

    @Override
    public V remove(K key) {
        checkNotNull(key, "key");
        if (root == null) {
            return null;
        }

        int cIndex = 0;
        Node<K, V> cNode = root, pNode = null;

        for (; ; ) {
            // 保证向下搜索的路径中的每一个节点都至少有factor个key
            if (cNode != root &&
                cNode.size == factor - 1) {
                cIndex = nodeBalance(pNode, cNode, cIndex);
                cNode = pNode.children[cIndex];
                continue;
            }

            int idx = binarySearch(cNode.keys, 0, cNode.size, key, comparator);
            // 当前节点没有搜索到key，向下继续搜索
            if (idx < 0) {
                if (cNode.isLeaf()) {
                    break;
                }

                pNode = cNode;
                cNode = cNode.children[cIndex = -1 - idx];
                continue;
            }

            // 当前节点搜索到了key

            if (cNode.isLeaf()) {
                // 直接删除，返回旧value
                V oldValue = getValue(cNode, idx);
                removeAt(cNode, idx);
                size--;
                return oldValue;
            }

            /*
             * 1. 如果左子节点至少有factor个key，
             *    从左子节点找到直接前驱，用直接前驱替代要删除的key
             * 2. 否则，如果右子节点至少有factor个key，
             *    从右子节点找到直接后继，用直接后继替代要删除的key
             * 3. 否则，合并这两个节点
             */
            cIndex = idx;
            pNode = cNode;
            Node<K, V> leftChild = cNode.children[idx],
                    rightChild = cNode.children[idx + 1];
            if (leftChild.size >= factor) {
                Node<K, V> preNode = leftChild;
                while (!preNode.isLeaf()) {
                    preNode = preNode.children[preNode.size];
                }

                V rmValue = getValue(cNode, idx);
                cNode.keys[idx] = preNode.keys[preNode.size - 1];
                cNode.values[idx] = preNode.values[preNode.size - 1];

                preNode.keys[preNode.size - 1] = key;
                preNode.values[preNode.size - 1] = rmValue;

                // 从leftChild开始继续向下搜索，为什么不从preNode开始呢？因为两者之间可能有需要合并的节点
                cNode = leftChild;
            } else if (rightChild.size >= factor) {
                Node<K, V> succNode = rightChild;
                while (!succNode.isLeaf()) {
                    succNode = succNode.children[0];
                }

                V rmValue = getValue(cNode, idx);
                cNode.keys[idx] = succNode.keys[0];
                cNode.values[idx] = succNode.values[0];

                succNode.keys[0] = key;
                succNode.values[0] = rmValue;

                cIndex++;
                cNode = rightChild;
            } else {
                merge(pNode, cIndex);
                cNode = pNode.children[cIndex];
            }
        }

        return null;
    }

    @Override
    public V replace(K key, V newValue) {
        checkNotNull(key, "key");
        checkNotNull(newValue, "value");

        if (root == null) {
            return null;
        }

        V oldValue = null;
        Node<K, V> cNode = root;
        for (; ; ) {
            int idx = binarySearch(cNode.keys, 0, cNode.size, key, comparator);
            if (idx >= 0) {
                oldValue = getValue(cNode, idx);
                cNode.values[idx] = newValue;
                break;
            }
            if (cNode.isLeaf()) {
                break;
            }
            cNode = cNode.children[-1 - idx];
        }

        return oldValue;
    }

    @Override
    public V get(K key) {
        checkNotNull(key, "key");
        if (root == null) {
            return null;
        }

        V value = null;
        // 从root向下搜索key
        Node<K, V> cNode = root;
        for (; ; ) {
            // 每个节点中采用二分搜索
            int idx = binarySearch(cNode.keys, 0, cNode.size, key, comparator);
            // idx>=0 表示当前节点搜索到了关键字key，返回key对应的value即可
            if (idx >= 0) {
                value = getValue(cNode, idx);
                break;
            }

            // idx<0 当前节点没搜索到

            // 当前节点是叶子节点，跳出循环，返回null
            if (cNode.isLeaf()) {
                break;
            }

            // 当前节点不是叶子节点，向下搜索
            cNode = cNode.children[-1 - idx];
        }

        return value;
    }

    @Override
    public int size() {
        return size;
    }

    public int height() {
        return height;
    }

    @Override
    public String toString() {
        if (root == null) {
            return "B-Tree is empty.";
        }

        StringBuilder buf = new StringBuilder();
        LinkedList<Node<K, V>> q1 = new LinkedList<>(),
                q2 = new LinkedList<>();
        q1.addLast(root);
        for (; ; ) {
            Node<K, V> first = q1.pollFirst();
            if (first == null) {
                if (q2.isEmpty()) {
                    break;
                }
                LinkedList<Node<K, V>> t = q1;
                q1 = q2;
                q2 = t;
                buf.append('\n');
                continue;
            }
            StringJoiner joiner = new StringJoiner(", ");
            for (int i = 0; i < first.size; i++) {
                joiner.add(String.valueOf(first.keys[i]));
                if (!first.isLeaf()) {
                    q2.addLast(first.children[i]);
                }
            }
            if (!first.isLeaf()) {
                q2.addLast(first.children[first.size]);
            }

            buf.append('[').append(joiner).append(']').append(' ');
        }

        return buf.toString();
    }

    /**
     * 从root向下，查找插入点，并且分裂途中遇到的所有满节点
     * 遇到key相同的节点，更新value后直接返回
     * 否则，向叶子节点插入一个key
     */
    private void insert(K key, V value) {
        Node<K, V> cNode = root, pNode = null;
        int cIndex = 0;
        while (cNode != null) {
            if (cNode.isFull()) {
                // root满了，需要分裂root，树高度+1
                if (pNode == null) {
                    pNode = new Node<>(factor);
                    pNode.newChildren();
                    pNode.children[0] = cNode;
                    root = pNode;
                    height++;
                }

                // 分裂节点，然后继续向下查找插入点
                split(pNode, cIndex);
                int c = compare(comparator, key, pNode.keys[cIndex]);
                if (c > 0) {
                    cNode = pNode.children[++cIndex];
                } else if (c < 0) {
                    cNode = pNode.children[cIndex];
                } else {
                    pNode.values[cIndex] = value;
                    return;
                }

                continue;
            }

            int idx = binarySearch(cNode.keys, 0, cNode.size, key, comparator);
            // update
            if (idx >= 0) {
                cNode.values[idx] = value;
                return;
            }

            // cNode是叶子节点，跳出循环，执行插入
            if (cNode.isLeaf()) {
                break;
            }

            pNode = cNode;
            cNode = cNode.children[cIndex = -1 - idx];
        }

        assert cNode != null;

        insertToNode(cNode, key, value);
    }

    /**
     * 该方法保证在删除节点时，
     * 向下搜索关键字的路径上所有节点都至少有factor的key，
     * 这样才能保证该节点的子节点能够合并
     */
    private int nodeBalance(Node<K, V> pNode, Node<K, V> cNode, int cIndex) {
        assert cNode.size == factor - 1;

        /*
         * 1. 如果邻近的兄弟节点有富余的key，
         *    此时，先将父节点key移到cNode，
         *    然后将兄弟节点的第一个或最后一个key移到父节点。
         * 2. 如果邻近的兄弟节点都只有factor-1个节点，
         *    优先和左兄弟合并，如果左兄弟为null，则和右兄弟合并
         */

        int rtIndex = cIndex;
        Node<K, V> leftSib = null, rightSib = null;
        if (cIndex != 0) {
            leftSib = pNode.children[cIndex - 1];
        }
        if (cIndex != pNode.size) {
            rightSib = pNode.children[cIndex + 1];
        }

        if (leftSib != null &&
            leftSib.size >= factor) {
            cIndex--;
            insertToNode(cNode, 0, getKey(pNode, cIndex), getValue(pNode, cIndex));
            pNode.keys[cIndex] = leftSib.keys[leftSib.size - 1];
            pNode.values[cIndex] = leftSib.values[leftSib.size - 1];
            if (!leftSib.isLeaf()) {
                insertChild(cNode, 0, leftSib.children[leftSib.size]);
                leftSib.children[leftSib.size] = null;
            }
            removeAt(leftSib, leftSib.size - 1);
        } else if (rightSib != null &&
                   rightSib.size >= factor) {
            insertToNode(cNode, cNode.size, getKey(pNode, cIndex), getValue(pNode, cIndex));
            pNode.keys[cIndex] = rightSib.keys[0];
            pNode.values[cIndex] = rightSib.values[0];
            if (!rightSib.isLeaf()) {
                insertChild(cNode, cNode.size, rightSib.children[0]);
                System.arraycopy(rightSib.children, 1, rightSib.children, 0, rightSib.size);
                rightSib.children[rightSib.size] = null;
            }
            removeAt(rightSib, 0);
        } else if (leftSib != null) {
            merge(pNode, rtIndex = cIndex - 1);
        } else {
            merge(pNode, cIndex);
        }

        return rtIndex;
    }

    /**
     * 分裂一个满的节点
     *
     * @param parent          待分裂节点的父节点
     * @param splitChildIndex 待分裂节点在其父节点children数组中的下标
     */
    private void split(Node<K, V> parent, int splitChildIndex) {
        /*
         *   factor = 2
         *
         *       +---+                            +-------+
         *       | G |                            | G | I |
         *       +---+                            +-------+
         *      /     \             split ->     /    |    \
         *     /       \                        /     |     \
         *  +---+    +-----------+           +---+  +---+  +---+
         *  | C |    | H | I | J |           | C |  | H |  | J |
         *  +---+    +-----------+           +---+  +---+  +---+
         *
         */
        Node<K, V> splitNode = parent.children[splitChildIndex];

        assert splitNode.isFull();

        int mid = splitNode.size >>> 1;

        // 将中间key提到父节点上
        K midKey = getKey(splitNode, mid);
        V midValue = getValue(splitNode, mid);
        insertToNode(parent, splitChildIndex, midKey, midValue);

        // 创建兄弟节点，拷贝一半key和value
        Node<K, V> sibNode = new Node<>(factor);
        int sibSize = splitNode.size - mid - 1;
        sibNode.size = sibSize;
        System.arraycopy(splitNode.keys, mid + 1, sibNode.keys, 0, sibSize);
        System.arraycopy(splitNode.values, mid + 1, sibNode.values, 0, sibSize);
        Arrays.fill(splitNode.keys, mid, splitNode.size, null);
        Arrays.fill(splitNode.values, mid, splitNode.size, null);

        // 将兄弟节点插入到父节点的children数组
        insertChild(parent, splitChildIndex + 1, sibNode);

        // 如果是内部节点，将一半子节点转移给兄弟节点
        if (!splitNode.isLeaf()) {
            Node<K, V>[] sibChildren = sibNode.newChildren();
            System.arraycopy(splitNode.children, mid + 1, sibChildren, 0, sibSize + 1);
            Arrays.fill(splitNode.children, mid + 1, splitNode.size + 1, null);
        }

        // 修改size
        splitNode.size = mid;
    }

    /**
     * 节点合并，将父节点中的一个key和这个key的左右两个子节点，合并到一起
     *
     * @param parent          父节点
     * @param mergeChildIndex 父节点中要合并的key的下标
     */
    private void merge(Node<K, V> parent, int mergeChildIndex) {
        /*
         * 合并key的左右子节点，将左右子节点和key合并到一起
         * factor = 2
         *
         *       +-----------+                     +-----------+
         *       | B | G | K |                     |  B  |  K  |
         *       +-----------+                     +-----------+
         *      /    |   |    \     merge ->      /      |      \
         *     /     |   |     \                 /       |       \
         *  +---+ +---+ +---+ +-------+      +---+ +-----------+ +---+
         *  | A | | C | | H | | L | M |      | A | | C | G | H | | L |
         *  +---+ +---+ +---+ +-------+      +---+ +-----------+ +---+
         *
         */
        assert parent == root || parent.size >= factor;

        Node<K, V> leftChild = parent.children[mergeChildIndex],
                rightChild = parent.children[mergeChildIndex + 1];

        int minSize = factor - 1;
        assert leftChild.size == minSize;
        assert rightChild.size == minSize;

        // 将右子节点数据迁移到左子节点
        System.arraycopy(rightChild.keys, 0, leftChild.keys, minSize + 1, minSize);
        System.arraycopy(rightChild.values, 0, leftChild.values, minSize + 1, minSize);
        leftChild.size += minSize;

        // 迁移子节点
        if (!leftChild.isLeaf()) {
            System.arraycopy(rightChild.children, 0, leftChild.children, minSize + 1, minSize + 1);
        }

        // 父节点key下降
        leftChild.keys[minSize] = parent.keys[mergeChildIndex];
        leftChild.values[minSize] = parent.values[mergeChildIndex];
        leftChild.size++;

        int oldSize = parent.size;

        removeAt(parent, mergeChildIndex);

        // 从parent中去除rightChild
        System.arraycopy(parent.children, mergeChildIndex + 2, parent.children, mergeChildIndex + 1, oldSize - mergeChildIndex - 1);
        parent.children[oldSize] = null;

        // 合并的过程中，可能将root节点置空，这是B-Tree唯一降低高度的方式
        if (parent == root &&
            parent.size == 0) {
            root = parent.children[0];
            height--;
        }
    }

    private int insertToNode(Node<K, V> node, K key, V value) {
        int idx = binarySearch(node.keys, 0, node.size, key, comparator);
        if (idx < 0) {
            int insertIndex = -1 - idx;
            insertToNode(node, insertIndex, key, value);
            size++;
            return insertIndex;
        }
        // update
        node.values[idx] = value;
        return -1;
    }

    private void insertToNode(Node<K, V> node, int index, K key, V value) {
        int cpSize = node.size - index;
        System.arraycopy(node.keys, index, node.keys, index + 1, cpSize);
        System.arraycopy(node.values, index, node.values, index + 1, cpSize);
        node.keys[index] = key;
        node.values[index] = value;
        node.size++;
    }

    private void insertChild(Node<K, V> parent, int index, Node<K, V> child) {
        // 该方法总是在insertToNode之后调用，所以parent.size会增加1，所以length = parent.size - index
        System.arraycopy(parent.children, index, parent.children, index + 1, parent.size - index);
        parent.children[index] = child;
    }

    private void removeAt(Node<K, V> node, int idx) {
        int cpSize = node.size - idx - 1;
        System.arraycopy(node.keys, idx + 1, node.keys, idx, cpSize);
        System.arraycopy(node.values, idx + 1, node.values, idx, cpSize);
        node.keys[node.size - 1] = null;
        node.values[node.size - 1] = null;
        node.size--;
    }

    @SuppressWarnings("unchecked")
    private K getKey(Node<K, V> node, int i) {
        return (K) node.keys[i];
    }

    @SuppressWarnings("unchecked")
    private V getValue(Node<K, V> node, int i) {
        return (V) node.values[i];
    }

    /**
     * 二分查找数组
     *
     * @param array      查询数组
     * @param fromIndex  开始下标，包含
     * @param toIndex    结束下标，不包含
     * @param key        查询关键字
     * @param comparator 比较器
     * @return 如果找到就返回下标，没找到就返回一个负数，表示 -(应该插入的位置 + 1)，所以 应该插入的位置 = -return - 1
     */
    private static int binarySearch(Object[] array, int fromIndex, int toIndex, Object key, Comparator comparator) {
        int from = fromIndex, to = toIndex;
        while (from < to) {
            int mid = (to + from) >>> 1; // 不会溢出
            int c = compare(comparator, key, array[mid]);
            if (c < 0) {
                to = mid;
            } else if (c > 0) {
                from = mid + 1;
            } else {
                return mid;
            }
        }
        return -(from + 1);
    }

    static class Node<K, V> {

        /**
         * 当前节点保存的关键字数量
         */
        int size;

        Object[] keys;
        
        Object[] values;

        /**
         * 当前节点的子节点数组，保存的子节点数 = size + 1
         */
        Node<K, V>[] children;

        Node(int factor) {
            int keyCap = factor * 2 - 1;
            keys = new Object[keyCap];
            values = new Object[keyCap];
        }

        @SuppressWarnings("unchecked")
        Node<K, V>[] newChildren() {
            return children = new Node[keys.length + 1];
        }

        boolean isLeaf() {
            return children == null;
        }

        boolean isFull() {
            return size == keys.length;
        }

    }

}
