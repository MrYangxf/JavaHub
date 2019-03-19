package top.yangxf.interest.datastructure.linear;

import top.yangxf.interest.datastructure.core.Countable;

/**
 * @author yangxf
 */
public interface List<E> extends Countable {

    // add

    void add(E element);

    void add(int index, E element);

    void addFirst(E element);

    void addLast(E element);

    // remove

    int remove(E element);

    E removeAt(int index);

    E removeFirst();

    E removeLast();

    // update

    E replace(int index, E newElement);

    E replaceFirst(E newElement);

    E replaceLast(E newElement);

    // query

    E get(int index);

    E getFirst();

    E getLast();

    int indexOf(Object element);

    int lastIndexOf(Object element);

    boolean contains(Object element);

    void clear();

    List<E> reversed();

}
