package com.zhixin.flymeTools.hook;

import android.app.ActionBar;
import android.content.res.Resources;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class ActionBarHooks implements IClassPatch {
    @Override
    public void initPatch(XC_LoadPackage.LoadPackageParam loadPackageParam, Resources resources) {
        Class<?> ActionBarImpl = XposedHelpers.findClass("com.android.internal.app.ActionBarImpl", null);
        findAndHookMethod(ActionBarImpl, "hide", new UpdatePaddingHook());
        findAndHookMethod(ActionBarImpl, "show", new UpdatePaddingHook());
    }

    public static class UpdatePaddingHook extends XC_MethodHook {
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            ActionBar actionBar = ((ActionBar) param.thisObject);
            ActivityColorHook hook = (ActivityColorHook) ObjectHook.getObjectHook(actionBar.getCustomView().getContext());
            if (hook != null) {
                hook.log("ActionBar is change");
                hook.updateContextViewPadding(0, true);
            }
        }
    }
}
