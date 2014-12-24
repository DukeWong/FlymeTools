package com.zhixin.flymeTools.hook;

import android.content.res.Resources;
import android.view.View;
import com.zhixin.flymeTools.Util.ReflectionUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by zhixin on 2014/12/20.
 */
public class StatusBarHook implements IClassPatch {
    private HookEntrance singleton = null;

    public StatusBarHook(HookEntrance singleton) {
        this.singleton = singleton;
    }

    @Override
    public void initPatch(XC_LoadPackage.LoadPackageParam loadPackageParam, Resources resources) {
        Class<?> PhoneStatusBar = XposedHelpers.findClass("com.android.systemui.statusbar.phone.PhoneStatusBar", loadPackageParam.classLoader);
        try {
            XposedHelpers.findAndHookMethod(PhoneStatusBar, "makeStatusBarView", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    final View mStatusBarWindow = (View) ReflectionUtil.getObjectField(param.thisObject, "mStatusBarWindow");
                    if (mStatusBarWindow != null && singleton != null) {
                        //singleton.setStatusBarWindow(mStatusBarWindow);
                    }
                }
            });
        } catch (NoSuchMethodError e) {
        }
    }
}
