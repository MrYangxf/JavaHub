package top.yangxf.interest.util.common;

import java.util.Arrays;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

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

    public static String toString(Object[][] matrixArray) {
        checkNotNull(matrixArray, "matrixArray");
        int row = matrixArray.length,
                col = row == 0 ? 0 : matrixArray[0].length;
        StringBuilder buf = new StringBuilder();
        buf.append("row ").append(row)
           .append(", column ").append(col)
           .append('\n').append('[').append('\n');
        for (int i = 0; i < row; i++) {
            buf.append(' ')
               .append(Arrays.toString(matrixArray[i]))
               .append('\n');
        }
        buf.append(']');
        return buf.toString();
    }

    public static void show(Object[][] matrixArray) {
        System.out.println(toString(matrixArray));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[][] transposed(T[][] matrixArray) {
        checkNotNull(matrixArray, "matrixArray");
        int row = matrixArray.length, col = 0;
        if (row == 0 || (col = matrixArray[0].length) == 0) {
            throw new IllegalStateException("matrixArray is empty.");
        }

        Object[][] reversed = new Object[col][row];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                reversed[j][i] = matrixArray[i][j];
            }
        }
        return (T[][]) reversed;
    }

}
