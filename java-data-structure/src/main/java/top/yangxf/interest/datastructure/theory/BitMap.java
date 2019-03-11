package top.yangxf.interest.datastructure.theory;

import static top.yangxf.interest.util.common.MathUtil.nextPowerOf2;

/**
 * 位图的一个简单实现，未采用任何压缩算法，非常消耗空间
 *
 * @author yangxf
 */
public class BitMap {
    private static final int INT_SHIFT = 5;
    private static final int INT_MASK = 31;

    private int[] table;

    public BitMap() {
        this(1024);
    }

    /**
     * @param bound 位图中元素大小的上限
     */
    public BitMap(int bound) {
        if (bound < 1) {
            throw new IllegalStateException("bound must be > 0");
        }
        int cap = nextPowerOf2(bound) >>> INT_SHIFT;
        table = new int[cap < 8 ? 8 : cap];
    }

    public void add(int key) {
        // 计算到key应该在数组的的位置
        int index = key >>> INT_SHIFT;
        // 如果超出容量，扩容
        ensureCapacity(index);
        // key & INT_MASK 相当于 key % 32 ， 取模的结果为i，
        // 需要将table[index]这个整数的第i位置为1
        // 所以先计算 1 << i ，位移后的结果就是一个第i位为1，其他位都为0的整数
        // 然后再与table[index]按位或，即可将第i为置为1
        table[index] |= (1 << (key & INT_MASK));
    }

    public boolean contains(int key) {
        int index = key >>> INT_SHIFT;
        if (index >= table.length) {
            return false;
        }
        // 知道add()操作后，这里就很好理解了
        // (1 << (key & INT_MASK)) 是第i位为1，其他位都为0的整数
        // 与table[index]按位与之后，table[index]的第i为0，结果就为0，否则结果肯定不为零
        return (table[index] & (1 << (key & INT_MASK))) != 0;
    }

    private void ensureCapacity(int index) {
        if (index >= table.length) {
            int newCap = nextPowerOf2(index) << 1;
            int cap = newCap < 8 ? 8 : newCap;
            if (index >= cap) {
                throw new Error("the bit map is too large");
            }
            int[] oldTable = table;
            table = new int[cap];
            System.arraycopy(oldTable, 0, table, 0, oldTable.length);
        }
    }

}
