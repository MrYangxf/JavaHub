package top.yangxf.interest.algorithm.string;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * Levenshtein编辑距离的动态规划实现
 *
 * @author yangxf
 */
public class LevenshteinDynamicProgramming implements LevenshteinEditDistance {

    public static final LevenshteinEditDistance INSTANCE = new LevenshteinDynamicProgramming();

    private LevenshteinDynamicProgramming() {
    }

    @Override
    public int compute(String left, String right) {
        checkNotNull(left, "string");
        checkNotNull(right, "string");
        int ll = left.length(), rl = right.length();
        if (ll == 0) {
            return rl;
        } else if (rl == 0) {
            return ll;
        }

        int[][] eds = new int[ll][rl];
        for (int j = 0; j < rl; j++) {
            if (left.charAt(0) == right.charAt(j)) {
                eds[0][j] = j;
            } else if (j == 0) {
                eds[0][j] = 1;
            } else {
                eds[0][j] = eds[0][j - 1] + 1;
            }
        }

        for (int i = 0; i < ll; i++) {
            if (left.charAt(i) == right.charAt(0)) {
                eds[i][0] = i;
            } else if (i == 0) {
                eds[i][0] = 1;
            } else {
                eds[i][0] = eds[i - 1][0] + 1;
            }
        }

        for (int i = 1; i < ll; i++) {
            for (int j = 1; j < rl; j++) {
                if (left.charAt(i) == right.charAt(j)) {
                    eds[i][j] = min(eds[i - 1][j - 1],
                                    eds[i][j - 1] + 1,
                                    eds[i - 1][j] + 1);
                } else {
                    eds[i][j] = 1 + min(eds[i - 1][j - 1],
                                        eds[i][j - 1],
                                        eds[i - 1][j]);
                }
            }
        }

        return eds[ll - 1][rl - 1];
    }

    private static int min(int i1, int i2, int i3) {
        return Math.min(i1, Math.min(i2, i3));
    }

}
