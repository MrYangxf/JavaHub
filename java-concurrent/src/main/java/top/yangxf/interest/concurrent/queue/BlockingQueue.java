package top.yangxf.interest.concurrent.queue;

/**
 * @author yangxf
 */
public interface BlockingQueue<T> {

    void add(T t);

    T poll();

}
