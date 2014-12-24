package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.os.Build;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class EntranceMethodHook implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private XModuleResources mResources;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //程序名称修改
        AppNameHook appNameHook = new AppNameHook();
        XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, appNameHook);
        XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, appNameHook);
        //图标修改
        AppIconHook appIconHook = new AppIconHook();
        XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadIcon", PackageManager.class, appIconHook);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mResources = XModuleResources.createInstance(startupParam.modulePath, null);
            //状态栏hook
            XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new ActivityMethodHook.WindowFocusMethod(mResources));
            XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new ActivityMethodHook.DestroyMethod());
            //更多图标hook
            Class<?> menuBuilder = XposedHelpers.findClass("com.android.internal.view.menu.MenuBuilder", null);
            ActionMenuViewHook actionMenuViewHook = new ActionMenuViewHook(mResources);
            Class<?> actionMenuPresenter = XposedHelpers.findClass("com.android.internal.view.menu.ActionMenuPresenter", null);
            XposedHelpers.findAndHookMethod(actionMenuPresenter, "initForMenu", Context.class, menuBuilder, actionMenuViewHook);
            XposedHelpers.findAndHookMethod(actionMenuPresenter, "updateMenuView", boolean.class, actionMenuViewHook);
        }
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        IClassPatch classPatch = null;
        String packageName = loadPackageParam.packageName;
        if (packageName.equals("com.android.launcher3")) {
            classPatch = new LauncherHook();
        }
        if (packageName.equals("com.android.systemui")) {
            classPatch = new StatusBarHook(this);
        }
        if (packageName.equals("android")) {
            classPatch = new ActionBarHooks();
        }
        if (classPatch != null) {
            classPatch.initPatch(loadPackageParam, mResources);
        }
    }
}

/*
XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new AppHooKTest("com.tencent"));
xposedHelpers.findAndHookMethod(Activity.class, "dispatchTouchEvent", MotionEvent.class, new ActivityMethodHook.TouchEventMethod(mResources, mStatusBarWindow));
XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new ActivityMethodHook.WindowFocusMethod(mResources));
XposedHelpers.findAndHookMethod(Activity.class, "onDestroy", new ActivityMethodHook.DestroyMethod());
XposedHelpers.findAndHookMethod(Activity.class, "onWindowAttributesChanged", WindowManager.LayoutParams.class, new ActivityMethodHook.WindowAttributes(mResources, mStatusBarWindow));
 */
