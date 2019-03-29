package top.yangxf.interest.datastructure.tree;

import top.yangxf.interest.datastructure.core.Countable;

/**
 * @author yangxf
 */
public interface Heap<E> extends Countable {

    /**
     * 添加元素
     */
    void push(E element);

    /**
     * 移除堆顶元素
     */
    E pop();

    /**
     * 查看堆顶元素
     */
    E peek();

    /**
     * 替换堆元素
     *
     * @param index      元素下标
     * @param newElement 新元素
     * @return 旧的元素
     */
    E replace(int index, E newElement);

    /**
     * 合并一个堆到当前堆
     */
    void pushAll(Heap<E> heap);

}
