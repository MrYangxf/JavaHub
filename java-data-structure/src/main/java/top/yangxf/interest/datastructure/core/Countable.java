package top.yangxf.interest.datastructure.core;

/**
 * @author yangxf
 */
public interface Countable {

    int size();
    
    default boolean isEmpty() {
        return size() == 0;
    }
    
}
