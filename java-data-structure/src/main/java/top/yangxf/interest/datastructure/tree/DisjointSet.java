package top.yangxf.interest.datastructure.tree;

import java.io.Serializable;
import java.util.*;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * 并查集，是由多个集合树组成的森林，这是一个简单的实现
 * <p>
 * 1. 并，快速合并两个集合
 * <p>
 * 2. 查，快速查询两个元素是否在同一集合内
 *
 * @author yangxf
 */
public class DisjointSet<T> implements Serializable {
    private static final long serialVersionUID = 3134700471905625636L;

    private Map<T, Node<T>> nodeMap = new HashMap<>();

    /**
     * 创建一个只包含element的集合，并且添加到森林中
     */
    public void makeSet(T element) {
        checkNotNull(element, "element");
        nodeMap.putIfAbsent(element, new Node<>());
    }

    /**
     * 合并left和right两个元素所在的集合
     * <p>
     * 使用rank启发式的合并，rank是集合树的最大高度
     */
    public void union(T left, T right) {
        checkNotNull(left, "element");
        checkNotNull(right, "element");

        Node<T> leftNode = nodeMap.get(left),
                rightNode = nodeMap.get(right);

        if (leftNode == null) {
            throw new NoSuchElementException(left.toString());
        }

        if (rightNode == null) {
            throw new NoSuchElementException(right.toString());
        }

        Node<T> leftSet = findSet(leftNode),
                rightSet = findSet(rightNode);

        if (leftSet == rightSet) {
            return;
        }

        if (leftSet.rank < rightSet.rank) {
            leftSet.parent = rightSet;
        } else {
            rightSet.parent = leftSet;
            if (leftSet.rank == rightSet.rank) {
                leftSet.rank++;
            }
        }
    }

    /**
     * 查看left和right两个元素是否在同一集合内
     */
    public boolean isConnected(T left, T right) {
        if (left == null || right == null) {
            return false;
        }

        Node<T> leftNode = nodeMap.get(left);
        if (leftNode == null) {
            return false;
        }

        Node<T> rightNode = nodeMap.get(right);
        if (rightNode == null) {
            return false;
        }

        if (leftNode == rightNode) {
            return true;
        }

        return findSet(leftNode) == findSet(rightNode);
    }

    public Collection<Set<T>> toSets() {
        Map<Node<T>, Set<T>> setMap = new HashMap<>();
        for (Map.Entry<T, Node<T>> entry : nodeMap.entrySet()) {
            setMap.computeIfAbsent(findSet(entry.getValue()), k -> new HashSet<>())
                  .add(entry.getKey());
        }
        return setMap.values();
    }

    public void show() {
        toSets().forEach(System.out::println);
    }

    /**
     * 查询node所在的集合（实际上是集合的代表，即第一个元素节点）
     * <p>
     * 递归查询，回溯的时候会压缩路径
     */
    private Node<T> findSet(Node<T> node) {
        if (node != node.parent) {
            node.parent = findSet(node.parent);
        }
        return node.parent;
    }

    static class Node<T> {
        int rank;
        Node<T> parent;

        Node() {
            parent = this;
        }
    }

}
