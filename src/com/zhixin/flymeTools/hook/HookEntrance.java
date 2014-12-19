package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.os.Build;
import android.view.MotionEvent;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class HookEntrance implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private  XModuleResources mResources;
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mResources = XModuleResources.createInstance(startupParam.modulePath, null);
            XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new ActivityMethodHook.WindowFocusMethod(mResources));
            XposedHelpers.findAndHookMethod(Activity.class, "dispatchTouchEvent", MotionEvent.class, new ActivityMethodHook.TouchEventMethod(mResources));
            XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
            XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        }
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        IClassPatch classPatch = null;
        String packageName = loadPackageParam.packageName;
        if (packageName.equals("com.android.launcher3")) {
            classPatch = new LauncherHook();
        }
/*        if (packageName.equals("android")) {
            LogUtil.log("初始化");
            classPatch = new DecorViewHook();
        }*/
        if (classPatch != null) {
            classPatch.initPatch(loadPackageParam,mResources);
        }
    }
}
