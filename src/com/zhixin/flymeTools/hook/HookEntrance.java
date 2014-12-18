package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.WindowManager;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.AppUtil;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class HookEntrance implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            XposedHelpers.findAndHookMethod(Activity.class, "onStart", new SmartBarColorHook());
            XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new WindowFocusMethodHook());
            XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
            XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        }
    }

    public abstract class ActivityMethodHook extends XC_MethodHook {
        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            if (!AppUtil.isSystemApp((Activity) param.thisObject)) {
                ObjectHook hook = ObjectHook.getObjectHook(param.thisObject);
                if (hook == null) {
                    hook = new ActivityHook((Activity) param.thisObject);
                }
                if (hook instanceof ActivityHook) {
                    this.afterDoMethodHook(param, (Activity) param.thisObject, (ActivityHook) hook);
                }
            }
        }

        protected abstract void afterDoMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityHook activityHook);
    }

    public class WindowFocusMethodHook extends ActivityMethodHook {
        @Override
        protected void afterDoMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityHook activityHook) {
            boolean hasFocus = (Boolean) param.args[0];
            activityHook.log(hasFocus ? "激活" : "退出");
            if (hasFocus) {
                activityHook.updateSmartbarColor();
                activityHook.updateStatusBarLit();
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
