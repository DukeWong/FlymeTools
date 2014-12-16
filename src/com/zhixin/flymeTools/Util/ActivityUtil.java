package com.zhixin.flymeTools.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

/**
 * Created by ZXW on 2014/12/15.
 */
public class ActivityUtil {
    public static int STATUS_BAR_HEIGHT = 0;
    public static int ACTION_BAR_HEIGHT = 0;
    public static int NAVIGATION_BAR_HEIGHT = 96;

    public static boolean setStatusBarLit(Activity context) {
        Window window = context.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            return true;
        }
        return false;
    }

    /**
     * 设置内容视图高度问题
     *
     * @param activity
     */
    public static int changeContextViewPadding(Activity activity, boolean hasStatusBar) {
        int top = 0, bottom = 0;
        if (hasStatusBar) {
            top += ActivityUtil.getStatusBarHeight(activity);
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            Object mActionView = ReflectionUtil.getObjectField(actionBar, "mActionView");
            Object mSplitView = ReflectionUtil.getObjectField(actionBar, "mSplitView");
            if (mActionView != null) {
                top += ((View) mActionView).getHeight();
            }
            if (mSplitView != null) {
                bottom += ((View) mSplitView).getHeight();
                /*
                if (activity.getClass().getName().indexOf("MobileTicket") != -1) {
                    LogUtil.log("12306程序测试------");
                    LogUtil.log("类名:" + mSplitView.getClass().getName());
                    LogUtil.log("高度" + ((View) mSplitView).getHeight());
                    LogUtil.log("12306程序测试------");
                }*/
            }
        }
        boolean isKikit = ActivityUtil.setStatusBarLit(activity);
        if (isKikit) {
            activity.getWindow().getDecorView().findViewById(android.R.id.content).setPadding(0, top, 0, bottom);
        }
        return top;
    }

    public static boolean existFlag(Activity activity, int flags) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return attrs.flags == ((attrs.flags & ~flags) | (flags & flags));
    }
    public static int getNavigationBarHeight(Activity activity) {
        return NAVIGATION_BAR_HEIGHT;
    }

    /**
     * 获取状态栏高度+ActionBar高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarAndActionBarHeight(Activity activity) {
        return getStatusBarHeight(activity) + getActionBarHeight(activity);
    }

    /**
     * 获取手机状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (STATUS_BAR_HEIGHT == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                STATUS_BAR_HEIGHT = activity.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }

    /**
     * 获取ActionBar的高度
     *
     * @param activity
     * @return
     */
    public static int getActionBarHeight(Activity activity) {
        if (ACTION_BAR_HEIGHT == 0) {
            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {// 如果资源是存在的、有效的
                ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
        }
        return ACTION_BAR_HEIGHT;
    }

    /**
     * 设置出现SmartBar
     *
     * @param activity
     */
    public static void setSmartBarEnable(Activity activity) {
        final ActionBar bar = activity.getActionBar();
        SmartBarUtils.setActionBarViewCollapsable(bar, true);
        bar.setDisplayOptions(0);
    }

    /**
     * 设置状态栏为黑色图标
     *
     * @param activity
     * @param on
     */
    public static void setDarkBar(Activity activity, boolean on) {
        try {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            Field f = winParams.getClass().getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            f.setAccessible(true);
            int bits = f.getInt(winParams);
            Field f2 = winParams.getClass().getDeclaredField("meizuFlags");
            f2.setAccessible(true);
            int meizuFlags = f2.getInt(winParams);
            if (on) {
                meizuFlags |= bits;
            } else {
            }
            f2.setInt(winParams, meizuFlags);
            win.setAttributes(winParams);
            return;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
