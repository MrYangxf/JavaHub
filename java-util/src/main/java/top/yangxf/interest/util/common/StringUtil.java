package top.yangxf.interest.util.common;

import java.util.concurrent.ThreadLocalRandom;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;

/**
 * @author yangxf
 */
public final class StringUtil {

    private StringUtil() {
        throw new InstantiationError("StringUtil can't be instantiated");
    }

    private static final ThreadLocalRandom R = ThreadLocalRandom.current();

    public static String randomString(int minLength, int maxLength) {
        int chLen = R.nextInt(minLength, maxLength),
                poolSize = CHAR_POOL.length;
        char[] chars = new char[chLen];
        for (int i = 0; i < chLen; i++) {
            chars[i] = CHAR_POOL[R.nextInt(poolSize)];
        }

        return new String(chars);
    }

    public static String fill(String source, int fillLength, char fillChar, boolean leftAligned) {
        checkNotNull(source, "source");
        if (fillLength < 1) {
            return source;
        }

        char[] chs = new char[fillLength];
        for (int i = 0; i < fillLength; i++) {
            chs[i] = fillChar;
        }

        if (leftAligned) {
            return source + String.valueOf(chs);
        } else {
            return String.valueOf(chs) + source;
        }
    }

    private static final char[] CHAR_POOL;

    static {
        CHAR_POOL = new char[52];
        int i = 0;
        for (char c = 'a'; c <= 'z'; c++) {
            CHAR_POOL[i++] = c;
            CHAR_POOL[i++] = (char) (c - 32);
        }
    }

}
