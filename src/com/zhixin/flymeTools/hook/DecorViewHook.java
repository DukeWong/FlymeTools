package com.zhixin.flymeTools.hook;

import android.content.res.Resources;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by zhixin on 2014/12/19.
 */
public class DecorViewHook implements IClassPatch {
    @Override
    public void initPatch(XC_LoadPackage.LoadPackageParam loadPackageParam, Resources resources) {
        Class<?> decorView = XposedHelpers.findClass("com.android.internal.policy.impl.PhoneWindow$DecorView", loadPackageParam.classLoader);
        XposedHelpers.findAndHookMethod(decorView, "onWindowFocusChanged", boolean.class, new ActivityMethodHook.DecorViewFocusMethod(resources));
    }
}
