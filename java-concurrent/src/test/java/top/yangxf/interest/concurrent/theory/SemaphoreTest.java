package top.yangxf.interest.concurrent.theory;

import org.junit.Test;

public class SemaphoreTest {

    @Test
    public void test() {
        Semaphore sp = new Semaphore(8);
        int nThreads = 1000;
        Thread[] threads = new Thread[nThreads];
        for (int i = 0; i < nThreads; i++) {
            threads[i] = new Thread(() -> {
                sp.p(1);
                try {
                    Thread.sleep(500);
                    System.out.println(Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    sp.v(1);
                }
            }, "t" + i);
        }

        for (int i = 0; i < nThreads; i++) {
            threads[i].start();
        }
    }
}