package top.yangxf.interest.datastructure.theory;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertSame;

public class BitMapTest {

    @Test
    public void test() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        int count = 100000, bound = 10000000;
        int[] data = new int[count];
        BitMap bitMap = new BitMap();
        Set<Integer> set = new HashSet<>();
        for (int i = 0; i < count; i++) {
            int num = random.nextInt(bound);
            data[i] = num;
            if (i % 2 == 1) {
                bitMap.add(num);
                set.add(num);
            }
        }

        for (int i = 0; i < count; i++) {
            int num = data[i];
            assertSame(bitMap.contains(num), set.contains(num));
        }

    }


}