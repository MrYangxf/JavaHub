package top.yangxf.interest.algorithm.math;

import top.yangxf.interest.algorithm.exception.MatrixOperationException;

import java.util.function.BiFunction;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * @author yangxf
 */
public class MatrixHelper {

    public static <T> Matrix<T> add(Matrix<T> left, Matrix<T> right,
                                    BiFunction<T, T, T> addFunction) {
        checkNotNull(left, "matrix");
        checkNotNull(right, "matrix");
        checkNotNull(addFunction, "addFunction");

        int row = left.numberOfRow(),
                col = left.numberOfColumn();
        if (row != right.numberOfRow() ||
            col != right.numberOfColumn()) {
            throw new MatrixOperationException("add operation must be same type");
        }

        Matrix<T> matSum = new Matrix<>(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                T sum = addFunction.apply(left.get(i, j), right.get(i, j));
                matSum.set(i, j, sum);
            }
        }
        return matSum;
    }

    public static <S, T> Matrix<T> scalarMultiply(S scalar, Matrix<T> matrix,
                                                  BiFunction<S, T, T> scalarMultiplyFunction) {
        checkNotNull(scalar, "scalar");
        checkNotNull(matrix, "matrix");
        checkNotNull(scalarMultiplyFunction, "scalarMultiplyFunction");

        int row = matrix.numberOfRow(),
                col = matrix.numberOfColumn();

        Matrix<T> result = new Matrix<>(row, col);
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                result.set(i, j, scalarMultiplyFunction.apply(scalar, matrix.get(i, j)));
            }
        }
        return result;
    }

    public static <T> Matrix<T> multiply(Matrix<T> left, Matrix<T> right,
                                         BiFunction<T, T, T> multiplyFunction,
                                         BiFunction<T, T, T> reduceFunction) {
        checkNotNull(left, "matrix");
        checkNotNull(right, "matrix");
        checkNotNull(multiplyFunction, "multiplyFunction");
        checkNotNull(reduceFunction, "reduceFunction");

        int lRow = left.numberOfRow(), lCol = left.numberOfColumn(),
                rRow = right.numberOfRow(), rCol = right.numberOfColumn();
        if (lCol != rRow) {
            throw new MatrixOperationException("the number of left columns must be " +
                                               "same as the number of right rows");
        }

        Matrix<T> matrix = new Matrix<>(lRow, rCol);
        for (int i = 0; i < lRow; i++) {
            for (int j = 0; j < rCol; j++) {
                T sum = null;
                for (int k = 0; k < lCol; k++) {
                    T mul = multiplyFunction.apply(left.get(i, k), right.get(k, j));
                    sum = sum == null ? mul : reduceFunction.apply(sum, mul);
                }
                matrix.set(i, j, sum);
            }
        }
        return matrix;
    }

}
