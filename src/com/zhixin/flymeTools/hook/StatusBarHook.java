package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
    public static String IS_FULLSCRE_ENAPP = "_ZXisFullScreenApp";
    public static String STATUS_BAR_DRAWABLE = "_ZXStatusBarDrawable";
    public static String PREFERENCE_TRANSLUCENT = "preference_translucent_compulsory";
    public static String TRANSLUCENT_COLOR = "preference_translucent_color";
    public static String HAS_ACTIONBAR = "preference_has_ActionBar";
    public static String HAS_NAVIGATIONBAR = "preference_has_NavigationBar";
    public static String AUTOMATIC_COLOR_OPEN = "preference_automatic_color_open";

    protected static Drawable getStatusBarDrawable(Activity activity, XSharedPreferences xSharedPreferences) {
        Object drawable = XposedHelpers.getAdditionalInstanceField(activity, STATUS_BAR_DRAWABLE);
        Drawable statusBarDrawable = null;
        if (drawable != null && drawable instanceof Drawable) {
            statusBarDrawable = (Drawable) drawable;
        } else {
            String KEY_COLOR = ScreenshotHook.getActivityKeyColorName(activity);
            boolean useAutomaticColor = xSharedPreferences.getBoolean(AUTOMATIC_COLOR_OPEN, true);
            if (useAutomaticColor) {
                //读取颜色
                SharedPreferences sharedPreferences = activity.getSharedPreferences(activity.getPackageName() + FileUtil.SETTING, Context.MODE_PRIVATE);
                if (sharedPreferences.contains(KEY_COLOR)) {
                    int color = sharedPreferences.getInt(KEY_COLOR, 0);
                    statusBarDrawable = new ColorDrawable(color);
                }
            } else {
                if (xSharedPreferences.contains(TRANSLUCENT_COLOR)) {
                    String color = xSharedPreferences.getString(TRANSLUCENT_COLOR, null);
                    statusBarDrawable = new ColorDrawable(Color.parseColor(color));
                }
            }
            if (statusBarDrawable == null) {
                statusBarDrawable = SmartBarColorHook.getSmartBarDrawable(activity);
            }
        }
        XposedHelpers.setAdditionalInstanceField(activity, STATUS_BAR_DRAWABLE, statusBarDrawable);
        return statusBarDrawable;
    }

    /**
     * @param thisActivity
     * @param xSharedPreferences
     * @param hasActionBar
     * @param hasNavigationBar
     */
    public static void handStatusBarLit(Activity thisActivity, XSharedPreferences xSharedPreferences, boolean hasActionBar, boolean hasNavigationBar) {
        if (ActivityUtil.setStatusBarLit(thisActivity)) {
            View rootLayer = thisActivity.getWindow().getDecorView().findViewById(android.R.id.content);
            int bottom = 0;
            int top = ActivityUtil.getStatusBarHeight(thisActivity);
            if (hasActionBar) {
                top += ActivityUtil.getActionBarHeight(thisActivity);
            }
            if (hasNavigationBar) {
                bottom += ActivityUtil.gethasNavigationBar(thisActivity);
            }
            LogUtil.log("状态栏->" + thisActivity.getPackageName() + ":top->" + top + ",bottom->" + bottom);
            rootLayer.setPadding(0x0, top, 0x0, bottom);
            Drawable drawable = getStatusBarDrawable(thisActivity, xSharedPreferences);
            if (drawable != null) {
                rootLayer.setBackground(drawable);
            }
        }
    }

    protected static void change(Activity thisActivity) {
        XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
        boolean change = xSharedPreferences.getBoolean(PREFERENCE_TRANSLUCENT, false);
        if (change) {
            xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, thisActivity.getPackageName() + FileUtil.SETTING);
            change = xSharedPreferences.getBoolean(PREFERENCE_TRANSLUCENT, false);
            boolean hasActionBar = xSharedPreferences.getBoolean(HAS_ACTIONBAR, false);
            boolean hasNavigationBar = xSharedPreferences.getBoolean(HAS_NAVIGATIONBAR, false);
            if (change) {
                Object isFullScreenApp = XposedHelpers.getAdditionalInstanceField(thisActivity, IS_FULLSCRE_ENAPP);
                if (isFullScreenApp != null) {
                    if (isFullScreenApp == "-1") {
                        handStatusBarLit(thisActivity, xSharedPreferences, hasActionBar, hasNavigationBar);
                    }
                } else {
                    if (!ActivityUtil.existFlag(thisActivity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)) {
                        XposedHelpers.setAdditionalInstanceField(thisActivity, IS_FULLSCRE_ENAPP, "-1");
                        handStatusBarLit(thisActivity, xSharedPreferences, hasActionBar, hasNavigationBar);
                    } else {
                        XposedHelpers.setAdditionalInstanceField(thisActivity, IS_FULLSCRE_ENAPP, "1");
                    }
                }
            }
        }
    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        change(thisActivity);
    }
}
