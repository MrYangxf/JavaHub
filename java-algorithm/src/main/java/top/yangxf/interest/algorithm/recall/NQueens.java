package top.yangxf.interest.algorithm.recall;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static top.yangxf.interest.util.common.ObjectUtil.defaultIfNull;

/**
 * N皇后问题
 *
 * @author yangxf
 */
public class NQueens {

    private final AtomicBoolean started = new AtomicBoolean();
    private final int nQueens;
    private int solutionSize;
    private final Consumer<byte[][]> resultHandler;

    public NQueens(int nQueens) {
        this(nQueens, null);
    }

    /**
     * @param nQueens       皇后的个数，也就是棋盘的边长
     * @param resultHandler 结果处理器，输入棋盘矩阵，0代表空，1代表放置皇后
     */
    public NQueens(int nQueens, Consumer<byte[][]> resultHandler) {
        if (nQueens < 1) {
            throw new IllegalArgumentException("nQueens must be > 0");
        }
        this.nQueens = nQueens;
        this.resultHandler = defaultIfNull(resultHandler, matrix -> {
            StringBuilder buf = new StringBuilder();
            buf.append("Solution ").append(solutionSize).append(" : \n");
            for (byte[] vector : matrix) {
                buf.append(Arrays.toString(vector)).append("\n");
            }
            System.out.println(buf.toString());
        });
    }

    public void exec() {
        if (!started.compareAndSet(false, true)) {
            throw new IllegalStateException("already started");
        }

        // 从第0行开始决策
        processRow(0, new int[nQueens]);
    }

    /**
     * 处理第row行
     *
     * @param row  当前决策行
     * @param cols 保存之前的决策（每行放置皇后的列）
     */
    private void processRow(int row, int[] cols) {
        if (row == nQueens) {
            byte[][] matrix = new byte[nQueens][nQueens];
            for (int i = 0; i < nQueens; i++) {
                byte[] vector = new byte[nQueens];
                vector[cols[i]] = 1;
                matrix[i] = vector;
            }
            solutionSize++;
            resultHandler.accept(matrix);
            return;
        }

        for (int i = 0; i < nQueens; i++) {
            if (isOk(row, i, cols)) {
                cols[row] = i;
                // recall
                processRow(row + 1, cols);
            }
        }
    }

    /**
     * 判断当前决策是否符合规定
     *
     * @param row    当前决策的行
     * @param column 当前决策的列
     * @param cols   保存之前的决策（每行放置皇后的列）
     * @return true表示当前位置可以放置，否则为false
     */
    private boolean isOk(int row, int column, int[] cols) {
        int leftOffset = column - 1,
                rightOffset = column + 1;
        for (int i = row - 1; i >= 0; i--) {
            if (cols[i] == column ||
                    leftOffset >= 0 && cols[i] == leftOffset ||
                    rightOffset < nQueens && cols[i] == rightOffset) {
                return false;
            }
            leftOffset--;
            rightOffset++;
        }
        return true;
    }

}