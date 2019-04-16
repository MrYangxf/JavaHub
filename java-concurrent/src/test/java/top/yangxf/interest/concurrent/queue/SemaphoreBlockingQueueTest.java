package top.yangxf.interest.concurrent.queue;

import org.junit.Test;

public class SemaphoreBlockingQueueTest {

    @Test
    public void test() {
        SemaphoreBlockingQueue<Integer> queue = new SemaphoreBlockingQueue<>(10);

        int loop = 20, total = loop * 2;

        new Thread(() -> {
            try {
                for (int i = 0; i < loop; i++) {
                    queue.add(i);
                    System.out.println(queue);
                    Thread.sleep(500);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                for (int i = 10000; i < loop + 10000; i++) {
                    queue.add(i);
                    System.out.println(queue);
                    Thread.sleep(600);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < total; i++) {
                try {
                    Integer poll = queue.poll();
                    System.out.println(poll);
                    Thread.sleep(300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}