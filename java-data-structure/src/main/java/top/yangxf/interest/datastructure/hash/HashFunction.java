package top.yangxf.interest.datastructure.hash;

/**
 * @author yangxf
 */
@FunctionalInterface
public interface HashFunction<T> {

    int hash(T object);
    
}
