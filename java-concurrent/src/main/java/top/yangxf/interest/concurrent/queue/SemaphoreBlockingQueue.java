package top.yangxf.interest.concurrent.queue;

import top.yangxf.interest.concurrent.lock.SemaphoreLock;
import top.yangxf.interest.concurrent.theory.Semaphore;

/**
 * @author yangxf
 */
public class SemaphoreBlockingQueue<T> implements BlockingQueue<T> {

    private Semaphore notFull;
    private Semaphore notEmpty;
    private SemaphoreLock lock = new SemaphoreLock();

    private Object[] table;
    private int size;

    public SemaphoreBlockingQueue(int cap) {
        if (cap < 1) {
            throw new IllegalArgumentException("capacity must be > 0");
        }
        notEmpty = new Semaphore(0);
        notFull = new Semaphore(cap);
        table = new Object[cap];
    }

    public void add(T t) {
        // 如果队列是满的就会阻塞
        notFull.p(1);
        // lock保证队列的原子添加
        lock.lock();
        table[size++] = t;
        lock.unlock();
        // 唤醒一个阻塞在notEmpty的线程
        notEmpty.v(1);
    }

    @SuppressWarnings("unchecked")
    public T poll() {
        T element;
        // 如果队列是空就会阻塞
        notEmpty.p(1);
        // lock保证队列的原子删除
        lock.lock();
        element = (T) table[--size];
        lock.unlock();
        // 唤醒一个阻塞在notFull的线程
        notFull.v(1);
        return element;
    }

}
