package top.yangxf.interest.algorithm.string;

/**
 * 标记接口
 * Levenshtein编辑距离，是编辑距离的一种。
 * <p>
 * 指两个字串之间，由一个转成另一个所需的最少编辑操作次数。
 * <p>
 * 允许的编辑操作包括将一个字符替换成另一个字符，插入一个字符，删除一个字符。
 *
 * @author yangxf
 * @see LevenshteinDynamicProgramming 动态规划版本
 * @see LevenshteinBacktracking 回溯版本
 */
public interface LevenshteinEditDistance extends EditDistance {
}