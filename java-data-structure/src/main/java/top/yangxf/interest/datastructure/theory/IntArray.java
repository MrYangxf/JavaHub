package top.yangxf.interest.datastructure.theory;

import sun.misc.Unsafe;
import top.yangxf.interest.util.unsafe.UnsafeUtil;

/**
 * 使用java实现一个int数组
 * 只是为了方便理解数组的原理
 *
 * @author yangxf
 */
public class IntArray {

    // 借助 sun.misc.Unsafe 直接分配内存
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    // int类型占用4个字节（byte）
    // 下标i的内存地址偏移量 == i * 4 == i << 2
    private static final int INDEX_SHIFT = 2;

    private final int length;

    // 该数组的内存起始地址
    private final long memoryAddress;

    /**
     * new int[capacity]
     */
    public IntArray(int length) {
        if (length < 0) {
            throw new IllegalArgumentException(String.valueOf(length));
        }

        this.length = length;
        // 分配一块连续的内存，保存这块内存的起始地址
        memoryAddress = UNSAFE.allocateMemory(length << INDEX_SHIFT);
        // 初始化这块内存
        UNSAFE.setMemory(memoryAddress, length << INDEX_SHIFT, (byte) 0);
    }

    /**
     * array[index] = data
     */
    public void set(int index, int data) {
        ensureIndexRange(index);
        // 根据 起始地址+数组下标*类型长度 计算出内存地址，然后将data保存到该地址
        UNSAFE.putInt(memoryAddress + (index << INDEX_SHIFT), data);
    }

    /**
     * array[index]
     */
    public int get(int index) {
        ensureIndexRange(index);
        // 根据 起始地址+数组下标*类型长度 计算出内存地址，然后获取该地址的int值
        return UNSAFE.getInt(memoryAddress + (index << INDEX_SHIFT));
    }

    /**
     * array.capacity
     */
    public int length() {
        return length;
    }

    private void ensureIndexRange(int index) {
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }

}
