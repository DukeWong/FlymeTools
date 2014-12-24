package com.zhixin.flymeTools.Util;

/**
 * Created by ZXW on 2014/12/12.
 */
public class ConvertUtil {
    /**
     * 转换String到Int
     *
     * @param value
     * @param def
     * @return
     */
    public static int string2Int(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {

        }
        return def;
    }
}
