package top.yangxf.interest.algorithm.string;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class LevenshteinDynamicProgrammingTest {

    @Test
    public void test() {
        EditDistance editDistance = LevenshteinDynamicProgramming.INSTANCE;

        int ed = 0;
        String left = "", right = "";
        assertSame(ed, editDistance.compute(left, right));

        left = "abc";
        right = left.replace("a", "c");
        ed++;
        assertSame(ed, editDistance.compute(left, right));

        right += "d";
        ed++;
        assertSame(ed, editDistance.compute(left, right));

    }
}