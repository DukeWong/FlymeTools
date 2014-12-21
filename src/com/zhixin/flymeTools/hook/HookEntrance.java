package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.res.XModuleResources;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.zhixin.flymeTools.test.AppHooKTest;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by ZXW on 2014/12/12.
 */
public class HookEntrance implements IXposedHookZygoteInit, IXposedHookLoadPackage {
    private XModuleResources mResources;

    public void setStatusBarWindow(View mStatusBarWindow) {
        this.mStatusBarWindow = mStatusBarWindow;
    }

    private View mStatusBarWindow;

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        /**
         * 程序名称修改
         */
        XposedHelpers.findAndHookMethod(PackageItemInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        XposedHelpers.findAndHookMethod(ComponentInfo.class, "loadLabel", PackageManager.class, new PackageNameHook());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mResources = XModuleResources.createInstance(startupParam.modulePath, null);
            //XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new AppHooKTest("com.tencent"));
            //XposedHelpers.findAndHookMethod(Activity.class, "dispatchTouchEvent", MotionEvent.class, new ActivityMethodHook.TouchEventMethod(mResources, mStatusBarWindow));
            XposedHelpers.findAndHookMethod(Activity.class,"onDestroy",new ActivityMethodHook.DestroyMethod());
            XposedHelpers.findAndHookMethod(Activity.class, "onWindowFocusChanged", boolean.class, new ActivityMethodHook.WindowFocusMethod(mResources, mStatusBarWindow));
            XposedHelpers.findAndHookMethod(Activity.class, "onWindowAttributesChanged", WindowManager.LayoutParams.class, new ActivityMethodHook.WindowAttributes(mResources, mStatusBarWindow));
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
            classPatch=new ActionBarHooks();
        }
        if (classPatch != null) {
            classPatch.initPatch(loadPackageParam, mResources);
        }
    }
}
