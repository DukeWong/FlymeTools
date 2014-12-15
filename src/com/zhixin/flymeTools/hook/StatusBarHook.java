package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by ZXW on 2014/12/15.
 */
public class StatusBarHook extends XC_MethodHook {
    public static String IS_FULLSCREEN_APP = "_ZXisFullScreenApp";
    public static String PREFERENCE_TRANSLUCENT = "preference_translucent_compulsory";
    public static String HAS_ACTIONBAR = "preference_has_ActionBar";
    public static String HAS_NAVIGATIONBAR= "preference_has_NavigationBar";
    protected static Drawable getStatusBarDrawable(Activity activity) {
        String packageName = activity.getPackageName();
        XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, packageName + FileUtil.SETTING);
        xSharedPreferences.makeWorldReadable();
        String smartbar_type = xSharedPreferences.getString(SmartBarColorHook.SMARTBAR_TYPE, null);
        if (smartbar_type != null) {
            //自动设置等
            if (smartbar_type.indexOf("#") == -1) {
                //1为默认设置
                String smartbar_color = "#FFFFFFFF";
                if (smartbar_type.equals("-1")) {
                    smartbar_color = xSharedPreferences.getString(SmartBarColorHook.SMARTBAR_Color, smartbar_color);
                    int color = Color.parseColor(smartbar_color);
                    LogUtil.log(activity.getPackageName() + ":自定义颜色->" + smartbar_color);
                    return new ColorDrawable(color);
                }
            } else {
                LogUtil.log(activity.getPackageName() + ":默认颜色->" + smartbar_type);
                int color = Color.parseColor(smartbar_type);
                return new ColorDrawable(color);
            }
        }
        return SmartBarColorHook.getSmartBarDrawable(activity);
    }

    public boolean existFlag(Activity activity, int flags) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        if (attrs.flags == ((attrs.flags & ~flags) | (flags & flags))) {
            return true;
        }
        return false;
    }

    public void handStatusBarLit(Activity thisActivity, boolean hasActionBar,boolean  hasNavigationBar) {
        if (ActivityUtil.setStatusBarLit(thisActivity)) {
            View rootLayer = thisActivity.getWindow().getDecorView().findViewById(android.R.id.content);
            int bottom = 0;
            int top = ActivityUtil.getStatusBarHeight(thisActivity);
            if (hasActionBar) {
                top += ActivityUtil.getActionBarHeight(thisActivity);
            }
            if (hasNavigationBar){
                bottom+=ActivityUtil.gethasNavigationBar(thisActivity);
            }
            LogUtil.log("状态栏->" + thisActivity.getPackageName() + ":top->" + top + ",bottom->" + bottom);
            rootLayer.setPadding(0x0, top, 0x0,bottom);
            rootLayer.setBackground(getStatusBarDrawable(thisActivity));
        }
    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
        boolean change = xSharedPreferences.getBoolean(PREFERENCE_TRANSLUCENT, false);
        if (change) {
            xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, thisActivity.getPackageName() + FileUtil.SETTING);
            change = xSharedPreferences.getBoolean(PREFERENCE_TRANSLUCENT, false);
            boolean hasActionBar=xSharedPreferences.getBoolean(HAS_ACTIONBAR, false);
            boolean hasNavigationBar=xSharedPreferences.getBoolean(HAS_NAVIGATIONBAR, false);
            if (change) {
                Object isFullScreenApp = XposedHelpers.getAdditionalInstanceField(thisActivity, IS_FULLSCREEN_APP);
                if (isFullScreenApp != null) {
                    if (isFullScreenApp == "-1") {
                        this.handStatusBarLit(thisActivity,hasActionBar,hasNavigationBar);
                    }
                } else {
                    if (!existFlag(thisActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)) {
                        XposedHelpers.setAdditionalInstanceField(thisActivity, IS_FULLSCREEN_APP, "-1");
                        this.handStatusBarLit(thisActivity,hasActionBar,hasNavigationBar);
                    } else {
                        XposedHelpers.setAdditionalInstanceField(thisActivity, IS_FULLSCREEN_APP, "1");
                    }
                }
            }
        }
    }
}
