package top.yangxf.interest.datastructure.theory;

import top.yangxf.interest.datastructure.linear.List;

import java.util.Arrays;
import java.util.StringJoiner;

import static top.yangxf.interest.util.common.ArrayUtil.checkIndexRange;
import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;
import static top.yangxf.interest.util.common.ObjectUtil.nonNull;

/**
 * <p> 基于数组的双向链表结构。
 * <p> 采用3数组结构：
 * <p>  prevIndices[] : 存储前驱节点的下标
 * <p>  nextIndices[] : 存储后继节点的下标
 * <p>  data[]        : 存储节点数据
 * <p>  使用一个单链表保存所有可分配的下标
 * 
 * @author yangxf
 */
public class MultiArrayLinkedList<E> implements List<E> {

    private static final int DEFAULT_INITIAL_CAP = 8;

    private int headIndex = -1;
    private int tailIndex = -1;
    private int freeHeadIndex = -1;

    private int[] prevIndices;
    private int[] nextIndices;
    private Object[] data;

    private int size;
    private int capacity;

    public MultiArrayLinkedList() {
        this(DEFAULT_INITIAL_CAP);
    }

    public MultiArrayLinkedList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }

        capacity = initialCapacity;
        prevIndices = new int[capacity];
        nextIndices = new int[capacity];
        data = new Object[capacity];
        resetFreeNextIndices(0, capacity - 1);
    }

    @Override
    public void add(E element) {
        insert(size, element);
    }

    @Override
    public void insert(int index, E element) {
        checkIndexRange(index, 0, size + 1);
        checkNotNull(element);
        ensureCapacity();

        int storeIdx = setElement(element);
        if (size == 0) {
            headIndex = tailIndex = storeIdx;
            prevIndices[storeIdx] = nextIndices[storeIdx] = -1;
        } else if (index == 0) {
            prevIndices[storeIdx] = -1;
            nextIndices[storeIdx] = headIndex;
            prevIndices[headIndex] = storeIdx;
            headIndex = storeIdx;
        } else if (index == size) {
            prevIndices[storeIdx] = tailIndex;
            nextIndices[storeIdx] = -1;
            nextIndices[tailIndex] = storeIdx;
            tailIndex = storeIdx;
        } else {
            int nextDataIndex = getDataIndex(index),
                    prevDataIndex = prevIndices[nextDataIndex];
            prevIndices[storeIdx] = prevDataIndex;
            nextIndices[prevDataIndex] = storeIdx;
            prevIndices[nextDataIndex] = storeIdx;
            nextIndices[storeIdx] = nextDataIndex;
        }
        size++;
    }

    @Override
    public int remove(E element) {
        int index = indexOf(element);
        if (index != -1) {
            removeAt(index);
        }
        return index;
    }

    @Override
    public E removeAt(int index) {
        checkIndexRange(index, 0, size);
        int dataIndex = getDataIndex(index);
        E element = getElement(dataIndex);
        data[dataIndex] = null;
        if (--size == 0) {
            clear();
            return element;
        }

        int prev = prevIndices[dataIndex],
                next = nextIndices[dataIndex];
        if (dataIndex == headIndex) {
            headIndex = next;
            prevIndices[next] = -1;
        } else if (dataIndex == tailIndex) {
            tailIndex = prev;
            nextIndices[prev] = -1;
        } else {
            nextIndices[prev] = next;
            prevIndices[next] = prev;
        }

        prevIndices[dataIndex] = -1;
        nextIndices[dataIndex] = freeHeadIndex;
        freeHeadIndex = dataIndex;

        return element;
    }

    @Override
    public E replace(int index, E newElement) {
        checkNotNull(newElement);
        int dataIndex = getDataIndex(index);
        E oldElement = getElement(dataIndex);
        data[dataIndex] = newElement;
        return oldElement;
    }

    @Override
    public void clear() {
        Arrays.fill(data, null);
        resetFreeNextIndices(0, capacity - 1);
        headIndex = tailIndex = -1;
        size = 0;
    }

    @Override
    public E get(int index) {
        return getElement(getDataIndex(index));
    }

    @Override
    public int indexOf(Object element) {
        if (nonNull(element)) {
            int curr = headIndex;
            while (curr != -1) {
                if (element.equals(data[curr])) {
                    return curr;
                }
                curr = nextIndices[curr];
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        if (nonNull(element)) {
            int curr = tailIndex;
            while (curr != -1) {
                if (element.equals(data[curr])) {
                    return curr;
                }
                curr = prevIndices[curr];
            }
        }
        return -1;
    }

    @Override
    public boolean contains(Object element) {
        return indexOf(element) != -1;
    }

    @Override
    public int size() {
        return size;
    }

    public int freeSize() {
        return capacity - size;
    }

    public int[] freeDataIndices() {
        int curr = freeHeadIndex, i = 0;
        int[] remainingIndices = new int[freeSize()];
        while (curr != -1) {
            remainingIndices[i++] = curr;
            curr = nextIndices[curr];
        }
        return remainingIndices;
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(" -> ");
        int cur = headIndex;
        while (cur != -1) {
            joiner.add(data[cur].toString());
            cur = nextIndices[cur];
        }
        return joiner.toString();
    }

    private int setElement(E element) {
        ensureCapacity();
        int addIdx = freeHeadIndex;
        freeHeadIndex = nextIndices[addIdx];
        data[addIdx] = element;
        return addIdx;
    }

    private int getDataIndex(int index) {
        checkIndexRange(index, 0, size);

        int dataIndex, middle = size >>> 1;
        if (index > middle) {
            dataIndex = tailIndex;
            for (int i = 0; i < size - index - 1; i++) {
                dataIndex = prevIndices[dataIndex];
            }
        } else {
            dataIndex = headIndex;
            for (int i = 0; i < index; i++) {
                dataIndex = nextIndices[dataIndex];
            }
        }
        return dataIndex;
    }

    @SuppressWarnings("unchecked")
    private E getElement(int index) {
        checkIndexRange(index, 0, capacity);
        return (E) data[index];
    }

    /**
     * index included from and to
     */
    private void resetFreeNextIndices(int from, int to) {
        if (to < 0 || from > to) {
            return;
        }

        freeHeadIndex = from;
        for (int i = from; i < to; i++) {
            nextIndices[i] = i + 1;
        }
        nextIndices[to] = -1;
        Arrays.fill(prevIndices, from, to + 1, -1);
    }

    private void ensureCapacity() {
        if (freeHeadIndex == -1) {
            int oldCap = capacity;
            int[] oldPrevIndices = prevIndices,
                    oldNextIndices = nextIndices;
            Object[] oldData = data;

            capacity = oldCap < DEFAULT_INITIAL_CAP ? 16 : oldCap << 1;
            prevIndices = new int[capacity];
            nextIndices = new int[capacity];
            data = new Object[capacity];
            System.arraycopy(oldPrevIndices, 0, prevIndices, 0, oldCap);
            System.arraycopy(oldNextIndices, 0, nextIndices, 0, oldCap);
            System.arraycopy(oldData, 0, data, 0, oldCap);

            resetFreeNextIndices(oldCap, capacity - 1);
        }
    }
}
