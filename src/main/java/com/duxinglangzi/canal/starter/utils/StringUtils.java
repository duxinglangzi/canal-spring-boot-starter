package com.duxinglangzi.canal.starter.utils;

public class StringUtils {

    public static boolean isBlank(CharSequence cs) {
        int strLen = length(cs);
        if (strLen == 0) return true;
        for (int i = 0; i < strLen; ++i) {
            if (!Character.isWhitespace(cs.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
