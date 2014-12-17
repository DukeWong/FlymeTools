package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.view.WindowManager;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class HookEntrance implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onStart", new SmartBarColorHook());
        XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new WindowFocusMethodHook());
        XposedHelpers.findAndHookMethod(Activity.class, "onWindowAttributesChanged", WindowManager.LayoutParams.class, new WindowAttributesMethodHook());
        XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
    }
    public class WindowAttributesMethodHook extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            ObjectHook hook = ObjectHook.getObjectHook(param.thisObject);
            if (hook == null) {
                hook = new ActivityHook((Activity) param.thisObject);
            }
            if (hook instanceof ActivityHook) {
                ActivityHook activityHook = (ActivityHook) hook;
                activityHook.log("窗口模式被改变");
                activityHook.updateStatusBarLit(false);
            }
        }
    }
    public class WindowFocusMethodHook extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            boolean hasFocus = (Boolean) param.args[0];
            ObjectHook hook = ObjectHook.getObjectHook(param.thisObject);
            if (hook == null) {
                hook = new ActivityHook((Activity) param.thisObject);
            }
            if (hook instanceof ActivityHook) {

                ActivityHook activityHook = (ActivityHook) hook;
                activityHook.log(hasFocus?"激活":"退出");
                if (hasFocus){
                    activityHook.updateSmartbarColor();
                    activityHook.updateStatusBarLit(true);
                }else
                {
                    activityHook.updateStatusBarColor();
                }
            }
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        IClassPatch classPatch = null;
        if (loadPackageParam.packageName.equals("com.android.launcher3")) {
            classPatch = new LauncherHook();
        }
        if (classPatch != null) {
            classPatch.initPatch(loadPackageParam);
        }
    }
}
