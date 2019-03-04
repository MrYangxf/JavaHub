package top.yangxf.interest.datastructure.tree.heap;

import java.util.Comparator;

/**
 * 大顶堆
 *
 * @see MinHeap
 * @author yangxf
 */
public class MaxHeap<E> extends MinHeap<E> {

    public MaxHeap(E[] elements, Comparator<E> comparator) {
        super(elements, comparator);
    }

    public MaxHeap(Comparator<E> comparator) {
        super(comparator);
    }

    public MaxHeap(int initialCapacity) {
        super(initialCapacity);
    }

    public MaxHeap(int initialCapacity, Comparator<E> comparator) {
        super(initialCapacity, comparator);
    }

    @Override
    protected int compare(int x, int y) {
        return -super.compare(x, y);
    }
}
