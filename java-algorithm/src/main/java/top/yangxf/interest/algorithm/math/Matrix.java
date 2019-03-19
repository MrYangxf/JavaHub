package top.yangxf.interest.algorithm.math;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

import static top.yangxf.interest.util.common.ArrayUtil.checkIndexRange;
import static top.yangxf.interest.util.common.ArrayUtil.transposed;
import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * @author yangxf
 */
public class Matrix<E> implements Serializable {
    private static final long serialVersionUID = -6873042746406927689L;

    private final Object[][] data;
    private final int numberOfRow;
    private final int numberOfColumn;

    public Matrix(E[][] elements) {
        checkNotNull(elements, "elements");
        numberOfRow = elements.length;
        numberOfColumn = elements.length == 0 ?
                0 : elements[0].length;
        data = elements;
    }

    public Matrix(int numberOfRow, int numberOfColumn) {
        this.numberOfRow = numberOfRow;
        this.numberOfColumn = numberOfColumn;
        data = new Object[numberOfRow][numberOfColumn];
    }

    public void set(int row, int column, E element) {
        rangeCheck(row, column);
        data[row][column] = element;
    }

    public E get(int row, int column) {
        rangeCheck(row, column);
        return elementAt(row, column);
    }

    public int numberOfRow() {
        return numberOfRow;
    }

    public int numberOfColumn() {
        return numberOfColumn;
    }

    public E[] rowVector(int rowIndex) {
        checkIndexRange(rowIndex, 0, numberOfRow);
        return rowAt(rowIndex);
    }

    public E[] columnVector(int colIndex) {
        checkIndexRange(colIndex, 0, numberOfColumn);
        return columnAt(colIndex);
    }

    @SuppressWarnings("unchecked")
    public Matrix<E> transpose() {
        return new Matrix<>((E[][]) transposed(data));
    }

    public void show() {
        String buf = "Matrix(" +
                     numberOfRow +
                     ", " +
                     numberOfColumn +
                     ")\n" +
                     toString();
        System.out.println(buf);
    }

    @Override
    public String toString() {
        if (numberOfRow == 0) {
            return "[ ]";
        }

        StringBuilder buf = new StringBuilder();
        if (numberOfRow == 1) {
            return Arrays.toString(data[0]);
        }

        int[] maxLens = new int[numberOfColumn];
        for (int i = 0; i < numberOfColumn; i++) {
            maxLens[i] = Stream.of(columnVector(i))
                               .map(String::valueOf)
                               .mapToInt(String::length)
                               .max().orElse(1);
        }

        for (int i = 0; i < numberOfRow; i++) {
            if (i == 0) {
                buf.append('╭');
            } else if (i == numberOfRow - 1) {
                buf.append('╰');
            } else {
                buf.append('┆');
            }
            buf.append(' ');
            for (int j = 0; j < numberOfColumn; j++) {
                String val = String.valueOf(data[i][j]);
                for (int k = 0; k < maxLens[j] - val.length(); k++) {
                    buf.append(' ');
                }
                buf.append(val)
                   .append(' ');
            }
            if (i == 0) {
                buf.append('╮');
            } else if (i == numberOfRow - 1) {
                buf.append('╯');
            } else {
                buf.append('┆');
            }
            buf.append('\n');
        }

        return buf.toString();
    }

    private void rangeCheck(int row, int column) {
        checkIndexRange(row, 0, numberOfRow);
        checkIndexRange(column, 0, numberOfColumn);
    }

    @SuppressWarnings("unchecked")
    private E elementAt(int row, int column) {
        return (E) data[row][column];
    }

    @SuppressWarnings("unchecked")
    private E[] rowAt(int rowIndex) {
        return (E[]) data[rowIndex];
    }

    @SuppressWarnings("unchecked")
    private E[] columnAt(int colIndex) {
        Object[] cols = new Object[numberOfRow];
        for (int i = 0; i < numberOfRow; i++) {
            cols[i] = data[i][colIndex];
        }
        return (E[]) cols;
    }

}
