package top.yangxf.interest.datastructure.tree.heap;

import top.yangxf.interest.datastructure.tree.Heap;

import java.util.Comparator;

import static top.yangxf.interest.util.common.MathUtil.nextPowerOf2;
import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * <pre>
 *     
 * 小顶堆
 * 基于数组存储的完全二叉树
 * 堆顶总是最小的元素
 * 
 * </pre>
 *
 * @author yangxf
 */
public class MinHeap<E> implements Heap<E> {

    private static final int DEFAULT_INITIAL_CAP = 8;

    private final Comparator<E> comparator;
    private Object[] table;
    private int capacity;
    private int size;

    public MinHeap(E[] elements, Comparator<E> comparator) {
        checkNotNull(elements);
        this.comparator = comparator;
        size = elements.length;
        capacity = nextPowerOf2(size) + 1;
        table = new Object[capacity];
        System.arraycopy(elements, 0, table, 1, size);
        heapify();
    }

    public MinHeap(Comparator<E> comparator) {
        this(DEFAULT_INITIAL_CAP, comparator);
    }

    public MinHeap(int initialCapacity) {
        this(initialCapacity, null);
    }

    public MinHeap(int initialCapacity, Comparator<E> comparator) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }

        capacity = nextPowerOf2(initialCapacity) + 1;
        this.comparator = comparator;
        table = new Object[capacity];
    }

    @Override
    public void push(E element) {
        checkNotNull(element);
        ensureCapacity();
        table[++size] = element;
        shiftUp(size);
    }

    @Override
    public E poll() {
        if (isEmpty()) {
            return null;
        }

        E top = getElement(1);
        swap(1, size);
        table[size--] = null;
        shiftDown(1);
        return top;
    }

    @Override
    public E peek() {
        return isEmpty() ? null : getElement(1);
    }

    @Override
    public int size() {
        return size;
    }

    protected int compare(int x, int y) {
        return compare(getElement(x), getElement(y));
    }

    /**
     * <pre>
     *  heapify 操作能够快速的将线性表转换为堆，
     *  原理是，找到最后一个包含叶子节点的节点，
     *  然后从这个节点向前遍历，做shiftDown操作。
     *  虽然heapfiy和循环push操作的时间复杂度都是 O (n * log n)
     *  但是，很明显，heapfiy循环次数更少。
     * </pre>
     */
    private void heapify() {
        int lastParent = size >>> 1;
        for (int i = lastParent; i >= 1; i--) {
            shiftDown(i);
        }
    }

    /**
     * <pre>
     * shiftUp操作
     * 比较当前节点和父节点，
     * 如果不符合堆的性质（小顶堆子节点必须比父节点大），交换当前节点和父节点
     * 然后继续向上比较
     * 直到符合堆的性质或者到达堆顶部为止
     * </pre>
     */
    private void shiftUp(final int i) {
        for (int c = i; ; ) {
            int p = c >>> 1;
            if (p > 0 &&
                    compare(c, p) < 0) {
                swap(c, p);
                c = p;
            } else {
                break;
            }
        }
    }

    /**
     * <pre>
     * 与shiftUp相反的操作
     * 不同的是，每次只和最小（对小顶堆来说）子节点比较
     * </pre>
     */
    private void shiftDown(final int i) {
        for (int c = i; ; ) {
            int l = c << 1, r = l + 1;
            if (r <= size) {
                int ch = compare(l, r) < 0 ? l : r;
                if (compare(c, ch) > 0) {
                    swap(c, ch);
                    c = ch;
                } else {
                    break;
                }
            } else if (l <= size &&
                    compare(c, l) > 0) {
                swap(c, l);
                c = l;
            } else {
                break;
            }
        }
    }

    private void ensureCapacity() {
        int oldCap = capacity - 1;
        if (size == oldCap) {
            Object[] oldTable = table;
            capacity = (oldCap < DEFAULT_INITIAL_CAP ? 16 : oldCap << 1) + 1;
            table = new Object[capacity];
            System.arraycopy(oldTable, 1, table, 1, oldCap);
        }
    }

    @SuppressWarnings("unchecked")
    private E getElement(int index) {
        return (E) table[index];
    }

    private void swap(int x, int y) {
        Object tmp = table[x];
        table[x] = table[y];
        table[y] = tmp;
    }

    @SuppressWarnings("unchecked")
    private int compare(E left, E right) {
        return comparator == null ?
                ((Comparable) left).compareTo(right) :
                comparator.compare(left, right);
    }

}
