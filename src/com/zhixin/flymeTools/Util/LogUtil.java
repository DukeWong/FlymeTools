package com.zhixin.flymeTools.Util;

import de.robv.android.xposed.XposedBridge;

/**
 * Created by zhixin on 2014/12/14.
 */
public final class LogUtil {
    public static Boolean DEBUG = true;

    public static void log(String text) {
        if (DEBUG) {
            XposedBridge.log("ZX:" + text);
        }
    }
}
