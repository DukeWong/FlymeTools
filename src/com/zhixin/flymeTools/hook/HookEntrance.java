package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class HookEntrance implements IXposedHookZygoteInit,IXposedHookLoadPackage {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        XposedHelpers.findAndHookMethod(Activity.class, "onStart", new SmartBarColorHook());
        XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        IClassPatch classPatch=null;
        if (loadPackageParam.packageName.equals("com.android.launcher3")){
            classPatch=new LauncherHook();
        }
        if (classPatch!=null){
            classPatch.initPatch(loadPackageParam);
        }
    }
}
