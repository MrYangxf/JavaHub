package top.yangxf.interest.datastructure.linear;

import top.yangxf.interest.datastructure.core.Countable;

/**
 * @author yangxf
 */
public interface List<E> extends Countable {

    void add(E element);

    void insert(int index, E element);

    int remove(E element);

    E removeAt(int index);

    E replace(int index, E newElement);

    void clear();

    E get(int index);

    int indexOf(Object element);
    
    int lastIndexOf(Object element);

    boolean contains(Object element);
    
}
