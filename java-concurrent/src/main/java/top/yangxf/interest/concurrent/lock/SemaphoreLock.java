package top.yangxf.interest.concurrent.lock;

import top.yangxf.interest.concurrent.theory.Semaphore;

/**
 * @author yangxf
 */
public class SemaphoreLock {

    private Semaphore semaphore = new Semaphore(1);

    public void lock() {
        semaphore.p(1);
    }

    public void unlock() {
        semaphore.v(1);
    }

}
