package top.yangxf.interest.algorithm.string;

/**
 * @author yangxf
 */
public interface EditDistance {

    /**
     * @return 字符串left和right的编辑距离
     */
    int compute(String left, String right);

}
