package top.yangxf.interest.algorithm.sort;

import java.util.Arrays;
import java.util.function.Function;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * 计数排序
 *
 * @author yangxf
 */
public class CountingSorter<T> implements Sorter<T> {

    private static final int MAX_BOUND = Integer.MAX_VALUE - 9;

    private int bound;
    private Function<T, Integer> sortFieldGetter;

    public CountingSorter() {
    }

    public CountingSorter(int bound) {
        this(bound, null);
    }

    public CountingSorter(Function<T, Integer> sortFieldGetter) {
        this.sortFieldGetter = sortFieldGetter;
    }

    public CountingSorter(int bound, Function<T, Integer> sortFieldGetter) {
        checkBound(bound);
        this.bound = bound;
        this.sortFieldGetter = sortFieldGetter;
    }

    @Override
    public void sort(final T[] array) {
        checkNotNull(array, "array");
        int len = array.length;
        if (len == 0 || len == 1) {
            return;
        }

        int[] countArray = new int[bound == 0 ? max(array) + 1 : bound];
        T[] cpArray = Arrays.copyOf(array, len);
        for (T t : cpArray) {
            countArray[getInt(t)]++;
        }

        for (int i = 1; i < countArray.length; i++) {
            countArray[i] += countArray[i - 1];
        }

        for (int i = cpArray.length - 1; i >= 0; i--) {
            T t = cpArray[i];
            array[--countArray[getInt(t)]] = t;
        }
    }

    private int max(T[] array) {
        int maxBound = Integer.MIN_VALUE;
        for (int i = 0; i < array.length; i++) {
            int anInt = getInt(array[i]);
            if (anInt > maxBound) {
                maxBound = anInt;
            }
        }
        checkBound(maxBound);
        return maxBound;
    }

    private int getInt(T t) {
        if (sortFieldGetter != null) {
            return sortFieldGetter.apply(t);
        } else if (t instanceof Integer) {
            return (Integer) t;
        }
        throw new IllegalArgumentException("the sort type must be integer or provide a integer field getter.");
    }

    private static void checkBound(int bound) {
        if (bound < 1 || bound > MAX_BOUND) {
            throw new IllegalArgumentException(String.format("bount [1, %s]", MAX_BOUND));
        }
    }

}