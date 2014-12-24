package com.zhixin.flymeTools.Util;

/**
 * Created by ZXW on 2014/12/19.
 */
public class StringUtil {

    /**
     * 判断两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean equals(String str1, String str2) {
        return equals(str1, str2, true);
    }

    /**
     * 判断两个字符串是否相等
     *
     * @param str1
     * @param str2
     * @param nullIsEquals
     * @return
     */
    public static boolean equals(String str1, String str2, boolean nullIsEquals) {
        if (str1 == null && str2 == null) return nullIsEquals;
        if (str1 != null) return str1.equals(str2);
        if (str2 != null) return str2.equals(str1);
        return false;
    }

    public static boolean isNotEmpty(String s) {
        return s != null && s.length() > 0;
    }

    /**
     * 判断字符串是否为空
     *
     * @param s
     * @return
     */
    public static boolean isNullOrEmpty(String s) {
        if (s == null || s.length() <= 0) {
            return true;
        }
        return false;
    }
}
