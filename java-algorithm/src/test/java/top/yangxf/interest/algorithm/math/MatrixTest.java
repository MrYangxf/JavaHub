package top.yangxf.interest.algorithm.math;

import org.junit.Test;

public class MatrixTest {

    @Test
    public void test() {
        Integer[][] integers = {
                {1, 2, 34, 12, 6, 2},
                {8, 3, 22, 32, 8, 332},
                // {3, 2, 123, 55, 5, 44},
                // {52, 234, 99, 1, 4, 1},
                // {1, 66, 34, 3, 3, 5},
        };
        Matrix<Integer> matrix = new Matrix<>(integers);
        matrix.show();
        matrix.transpose().show();
        MatrixHelper.multiply(matrix, matrix.transpose(), (x, y) -> x * y, (x, y) -> x + y).show();
        MatrixHelper.multiply(matrix.transpose(), matrix, (x, y) -> x * y, (x, y) -> x + y).show();
        Matrix<Integer> scalarMultiply = MatrixHelper.scalarMultiply(2, matrix, (s, e) -> s * e);
        scalarMultiply.show();
        MatrixHelper.add(matrix, scalarMultiply, (x, y) -> x + y).show();
    }

}