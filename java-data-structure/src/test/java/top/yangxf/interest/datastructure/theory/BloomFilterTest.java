package top.yangxf.interest.datastructure.theory;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static top.yangxf.interest.util.common.StringUtil.randomString;

public class BloomFilterTest {

    @Test
    public void test() {
        int count = 100000;
        int low = 50, up = 100;
        BloomFilter filter = BloomFilter.builder(10000).build();
        String[] data = new String[count];
        Set<String> dataSet = new HashSet<>();
        for (int i = 0; i < count; i++) {
            String str = randomString(low, up);
            data[i] = str;
            if (i % 2 == 0) {
                dataSet.add(str);
                filter.add(str);
            }
        }

        int error = 0, total = 0;
        for (int i = 0; i < count; i++) {
            String str = data[i];
            if (filter.contains(str)) {
                total++;
                if (!dataSet.contains(str)) {
                    error++;
                }
            } else {
                assertFalse(dataSet.contains(str));
            }
        }
        System.out.println("error: " + error);
        System.out.println("total: " + total);
        System.out.println("布隆过滤器误判率(判断包含，实际未包含)： " + (double) error / total);
    }

}