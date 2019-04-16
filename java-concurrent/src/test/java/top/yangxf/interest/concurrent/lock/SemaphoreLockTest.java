package top.yangxf.interest.concurrent.lock;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SemaphoreLockTest {

    @Test
    public void test() throws InterruptedException {
        SemaphoreLock lock = new SemaphoreLock();
        List<Integer> list = new ArrayList<>();
        int nThreads = 100, loop = 10000;
        CountDownLatch latch = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            new Thread(() -> {
                lock.lock();
                try {
                    for (int j = 0; j < loop; j++) {
                        list.add(j);
                    }
                    latch.countDown();
                } finally {
                    lock.unlock();
                }

            }).start();
        }

        latch.await();

        Assert.assertEquals(list.size(), nThreads * loop);

    }
}