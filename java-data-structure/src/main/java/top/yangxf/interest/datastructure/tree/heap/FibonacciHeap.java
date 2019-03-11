package top.yangxf.interest.datastructure.tree.heap;

import top.yangxf.interest.datastructure.tree.Heap;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * 斐波那契堆
 * <p>
 * 理论上的斐波那契堆比二叉堆有更好的时间复杂度
 * <p>
 * 但是实际上，由于大量的指针操作，导致斐波那契堆的效率远低于二叉堆（二叉堆可以用数组实现）
 * 
 * @author yangxf
 */
public class FibonacciHeap<E> implements Heap<E> {

    private final Comparator<E> comparator;
    private Node<E> minimum;
    private int size;

    private Map<Integer, Node<E>> degreeMap = new HashMap<>();

    public FibonacciHeap() {
        this.comparator = null;
    }

    public FibonacciHeap(Comparator<E> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void push(E element) {
        checkNotNull(element, "element");

        size++;
        Node<E> newNode = new Node<>(element);
        if (minimum == null) {
            minimum = newNode;
            return;
        }

        Node<E> oldRight = minimum.right;
        minimum.right = newNode;
        newNode.left = minimum;
        newNode.right = oldRight;
        oldRight.left = newNode;
        if (cmp(newNode, minimum) < 0) {
            minimum = newNode;
        }
    }

    @Override
    public E poll() {
        if (minimum == null) {
            return null;
        }

        Node<E> minNode = removeMin();
        size--;
        return minNode.data;
    }

    @Override
    public E peek() {
        return isEmpty() ? null : minimum.data;
    }

    public FibonacciHeap<E> union(FibonacciHeap<E> heap) {
        checkNotNull(heap, "heap");

        link(minimum, heap.minimum);
        if (cmp(heap.minimum, minimum) < 0) {
            minimum = heap.minimum;
        }
        heap.minimum = null;
        return this;
    }

    @Override
    public int size() {
        return size;
    }

    private void removeFromList(Node rm) {
        rm.left.right = rm.right;
        rm.right.left = rm.left;
    }

    /**
     *
     */
    private Node<E> removeMin() {
        Node<E> oldMin = minimum,
                child = oldMin.child;

        // 淘汰掉待删除节点的度的缓存
        if (degreeMap.get(oldMin.degree) == oldMin) {
            degreeMap.remove(oldMin.degree);
        }

        if (child == null &&
            oldMin == oldMin.right) {
            minimum = null;
            return oldMin;
        }

        removeFromList(oldMin);


        if (child == null) {
            minimum = oldMin.right;
        } else {
            if (oldMin != oldMin.right) {
                link(oldMin.right, child);
            }
            minimum = child;
        }

        resetMinPointer();
        consolidate();

        return oldMin;
    }

    private void resetMinPointer() {
        Node<E> curr = minimum,
                endOf = minimum;
        do {
            curr.parent = null;
            if (cmp(curr, minimum) < 0) {
                minimum = curr;
            }
            curr = curr.right;
        } while (curr != endOf);
    }

    private void consolidate() {
        Node<E> curr = minimum;
        do {
            int currDegree = curr.degree;
            Node<E> existsNode = degreeMap.get(currDegree);
            if (existsNode == null) {
                degreeMap.put(currDegree, curr);
                curr = curr.right;
            } else if (curr == existsNode) {
                curr = curr.right;
            } else {
                degreeMap.remove(currDegree);
                if (cmp(curr, existsNode) < 0) {
                    removeFromList(existsNode);
                    curr.addChild(existsNode);
                } else {
                    removeFromList(curr);
                    existsNode.addChild(curr);
                    curr = existsNode;
                }
            }
        } while (curr != minimum);
    }

    /**
     * 将slave和他的兄弟节点全部链接到master节点上
     */
    private void link(Node<E> master, Node<E> slave) {
        Node<E> succ = master.right;
        master.right = slave;
        succ.left = slave.left;
        slave.left.right = succ;
        slave.left = master;
    }

    private int cmp(Node left, Node right) {
        return cmp(left.data, right.data);
    }

    @SuppressWarnings("unchecked")
    private int cmp(Object left, Object right) {
        return comparator == null ?
                ((Comparable) left).compareTo(right) :
                comparator.compare((E) left, (E) right);
    }

    static class Node<E> {
        Node<E> parent, left, right, child;
        E data;
        int degree;
        boolean mark;

        Node(E data) {
            this.data = data;
            left = right = this;
        }

        @SuppressWarnings("unchecked")
        void addChild(Node node) {
            if (child == null) {
                child = node;
                node.left = node.right = node;
            } else {
                Node<E> oldChSucc = child.right;
                child.right = node;
                node.left = child;
                node.right = oldChSucc;
                oldChSucc.left = node;
            }
            node.parent = this;
            node.mark = false;
            degree++;
        }

        void clean() {
            parent = child = null;
            left = right = this;
            degree = 0;
            mark = false;
        }

    }

}
