package top.yangxf.interest.datastructure.linear;

import java.util.Arrays;

import static top.yangxf.interest.util.common.ArrayUtil.checkIndexRange;
import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;
import static top.yangxf.interest.util.common.ObjectUtil.nonNull;

/**
 * @author yangxf
 */
public class DynamicArrayList<E> implements List<E> {

    private static final int DEFAULT_INITIAL_CAP = 8;

    private Object[] data;

    private int capacity;

    private int size;

    public DynamicArrayList() {
        this(DEFAULT_INITIAL_CAP);
    }

    public DynamicArrayList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("initialCapacity must be >= 0");
        }
        capacity = initialCapacity;
        data = new Object[capacity];
    }

    /**
     * O(1)
     */
    @Override
    public void add(E element) {
        checkNotNull(element);
        ensureCapacity();
        data[size++] = element;
    }

    /**
     * O(n)
     */
    @Override
    public void add(int index, E element) {
        checkIndexRange(index, 0, size + 1);
        checkNotNull(element);
        ensureCapacity();
        System.arraycopy(data, index, data,
                         index + 1, size - index);
        data[index] = element;
        size++;
    }

    @Override
    public void addFirst(E element) {
        add(0, element);
    }

    @Override
    public void addLast(E element) {
        add(element);
    }

    /**
     * @see #removeAt(int)
     */
    @Override
    public int remove(E element) {
        int rmIdx = indexOf(element);
        if (rmIdx != -1) {
            removeAt(rmIdx);
        }
        return rmIdx;
    }

    /**
     * O(n)
     */
    @Override
    public E removeAt(int index) {
        checkIndexRange(index, 0, size);
        E rmElement = getElement(index);
        System.arraycopy(data, index + 1,
                         data, index, size - index);
        data[size--] = null;
        return rmElement;
    }

    @Override
    public E removeFirst() {
        return removeAt(0);
    }

    @Override
    public E removeLast() {
        return removeAt(size - 1);
    }

    /**
     * O(1)
     */
    @Override
    public E replace(int index, E newElement) {
        checkIndexRange(index, 0, size);
        E oldElement = getElement(index);
        data[index] = newElement;
        return oldElement;
    }

    @Override
    public E replaceFirst(E newElement) {
        return replace(0, newElement);
    }

    @Override
    public E replaceLast(E newElement) {
        return replace(size - 1, newElement);
    }

    @Override
    public void clear() {
        Arrays.fill(data, 0, size, null);
        size = 0;
    }

    @Override
    public List<E> reversed() {
        List<E> reversed = new DynamicArrayList<>(capacity);
        for (int i = size - 1; i > 0; i--) {
            reversed.add(getElement(i));
        }
        return reversed;
    }

    /**
     * O(1)
     */
    @Override
    public E get(int index) {
        return getElement(index);
    }

    @Override
    public E getFirst() {
        return get(0);
    }

    @Override
    public E getLast() {
        return get(size - 1);
    }

    /**
     * O(n)
     */
    @Override
    public int indexOf(Object element) {
        if (nonNull(element)) {
            for (int i = 0; i < size; i++) {
                if (element.equals(data[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        if (nonNull(element)) {
            for (int i = size - 1; i >= 0; i--) {
                if (element.equals(data[i])) {
                    return i;
                }
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

    @SuppressWarnings("unchecked")
    private E getElement(int index) {
        checkIndexRange(index, 0, size);
        return (E) data[index];
    }

    private void ensureCapacity() {
        int currentSize = size,
                oldCapacity = capacity;

        if (currentSize == oldCapacity) {
            Object[] oldData = data;
            capacity = oldCapacity < DEFAULT_INITIAL_CAP ? 16 : oldCapacity << 1;
            data = new Object[capacity];
            System.arraycopy(oldData, 0, data, 0, currentSize);
        }
    }

}
