package top.yangxf.interest.datastructure.tree;

import top.yangxf.interest.datastructure.core.Countable;

/**
 * @author yangxf
 */
public interface Heap<E> extends Countable {

    void push(E element);

    E poll();

    E peek();
    
}
