package top.yangxf.interest.util.common;

/**
 * @author yangxf
 */
public final class ArrayUtil {

    private ArrayUtil() {
        throw new InstantiationError("ArrayUtil can't be instantiated");
    }

    /**
     * Included fromIndex but excluded toIndex.
     */
    public static void checkIndexRange(int index, int fromIndex, int toIndex) {
        if (index < fromIndex || index >= toIndex) {
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }
    }
    
}
