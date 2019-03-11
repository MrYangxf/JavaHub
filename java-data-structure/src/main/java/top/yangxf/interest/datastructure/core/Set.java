package top.yangxf.interest.datastructure.core;

/**
 * @author yangxf
 */
public interface Set<E> extends Countable {

    boolean contains(Object element);

    Set<E> union(Set<E> set);

    Set<E> intersection(Set<E> set);
    
}