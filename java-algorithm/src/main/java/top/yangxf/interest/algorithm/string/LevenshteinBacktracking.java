package top.yangxf.interest.algorithm.string;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * Levenshtein编辑距离的回溯法实现
 *
 * @author yangxf
 */
public class LevenshteinBacktracking implements LevenshteinEditDistance {
    public static final LevenshteinEditDistance INSTANCE = new LevenshteinBacktracking();

    private String left, right;
    private int ll, rl;
    private int minEdit = Integer.MAX_VALUE;

    private LevenshteinBacktracking() {
    }

    @Override
    public int compute(String left, String right) {
        checkNotNull(left, "string");
        checkNotNull(right, "string");
        reset(left, right);
        doRecursion(0, 0, 0);
        return minEdit;
    }

    private void doRecursion(int i, int j, int edit) {
        if (i == ll || j == rl) {
            if (i < ll) {
                edit += (ll - i);
            }

            if (j < rl) {
                edit += (rl - j);
            }

            if (edit < minEdit)
                minEdit = edit;
            return;
        }

        if (left.charAt(i) == right.charAt(j)) {
            doRecursion(i + 1, j + 1, edit);
        } else {
            doRecursion(i + 1, j + 1, edit + 1);
            doRecursion(i, j + 1, edit + 1);
            doRecursion(i + 1, j, edit + 1);
        }
    }

    private void reset(String left, String right) {
        this.left = left;
        this.right = right;
        ll = left.length();
        rl = right.length();
        minEdit = Integer.MAX_VALUE;
    }

}
