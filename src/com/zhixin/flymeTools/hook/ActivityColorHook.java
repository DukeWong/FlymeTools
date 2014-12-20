package com.zhixin.flymeTools.hook;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.*;
import com.zhixin.flymeTools.controls.StatusBarDrawable;

/**
 * Created by ZXW on 2014/12/17.
 */
public class ActivityColorHook extends ObjectHook<Activity> {
    private String packageName = null;
    private String activityName = null;
    private View mActionView;
    private View mSplitView;
    private ActivityConfig config;
    private Resources mResources;
    /**
     * 已经修改够颜色标识
     */
    private boolean isChangeColor = false;
    private boolean isUpdateColor = false;
    private boolean mustChange = false;
    private View mStatusBarWindow;

    public ActivityColorHook(final Activity thisObject, final Resources resources, final View statusBarWindow) {
        super(thisObject);
        mResources = resources;
        mStatusBarWindow = statusBarWindow;
        packageName = thisObject.getPackageName();
        activityName = thisObject.getClass().getName();
        config = new ActivityConfig(thisObject);
    }

    public void log(String text) {
        LogUtil.log(activityName + " 消息:" + text);
    }
    public void DebugLog(String text) {
        if (activityName.indexOf(DEBUG_PACKAGE_NAME)!=-1){
            LogUtil.log(activityName + " 消息:" + text);
        }
    }
    public void reloadConfig() {
        config.reload();
    }

    /**
     * 根据配置更新Smartbar颜色
     */
    public void updateSmartbarColor() {
        synchronized (this) {
            if (config != null) {
                this.reloadConfig();
                Drawable smartBarDrawable = config.getSmartBarDrawable();
                if (smartBarDrawable != null) {
                    SmartBarUtils.changeSmartBarColor(thisObject, smartBarDrawable);
                }
            }
        }
    }

    /**
     * 获取ActionView
     *
     * @return
     */
    protected View getActionView() {
        if (mActionView == null) {
            ActionBar actionBar = thisObject.getActionBar();
            if (actionBar != null) {
                Object view = ReflectionUtil.getObjectField(actionBar, "mActionView");
                if (view != null) {
                    mActionView = (View) view;
                }
            }
        }
        return mActionView;
    }

    protected void updateStatusBarWindowColor(Drawable drawable) {
        if (mStatusBarWindow != null) {
            this.log("变色龙单独重新更新颜色");
            mStatusBarWindow.setBackground(drawable);
        }
    }

    /**
     * 获取mSplitView
     *
     * @return
     */
    protected View getSplitView() {

        if (mSplitView == null) {
            ActionBar actionBar = thisObject.getActionBar();
            if (actionBar != null) {
                Object view = ReflectionUtil.getObjectField(actionBar, "mSplitView");
                if (view != null) {
                    mSplitView = (View) view;
                }
            }
        }
        return mSplitView;
    }

    protected boolean isFitsSystemWindows(View contentView, boolean setFitsSystemWindows) {
        if (contentView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) contentView;
            int count = viewGroup.getChildCount();
            if (count > 0) {
                View view = viewGroup.getChildAt(0);
                if (setFitsSystemWindows) {
                    view.setFitsSystemWindows(true);
                }
                return view.getFitsSystemWindows();
            }
        }
        return false;
    }

    /**
     * 修改内容视图的边距
     *
     * @return 是否需要修改颜色
     */
    protected void changeContextViewPadding() {
        boolean hasStatusBar = config.hasStatusBar();
        boolean forceMode = config.isStatusBarForceMode();
        boolean hasActionBar = config.hasActionBar();
        View decorView = thisObject.getWindow().getDecorView();
        View contentView = decorView.findViewById(android.R.id.content);
        this.log("模式:" + hasStatusBar + forceMode + hasActionBar);
        //自动模式
        boolean isFitsSystemWindows = this.isFitsSystemWindows(contentView, false);
        //只有不是自动调整模式下才调整边距
        if (!isFitsSystemWindows || forceMode) {
            int top = 0, actionHeight = 0, bottom = 0;
            int statusBarHeight = ActivityUtil.getStatusBarHeight(thisObject);
            if (hasStatusBar) {
                top += statusBarHeight;
            }
            ActionBar actionBar = thisObject.getActionBar();
            if (actionBar != null) {
                View actionView = this.getActionView();
                View splitView = this.getSplitView();
                if (actionView != null) {
                    actionHeight = actionView.getHeight();
                    top += actionHeight;
                }
                if (splitView != null) {
                    bottom += splitView.getHeight();
                } else {
                    isFitsSystemWindows = true;
                }
            } else {
                isFitsSystemWindows = true;
            }
            if (hasActionBar && actionHeight == 0) {
                top += ActivityUtil.getActionBarHeight(thisObject);
            }
            if (!isFitsSystemWindows || forceMode) {
                contentView.setPadding(0, top, 0, bottom);
            } else {
                this.isFitsSystemWindows(contentView, true);
            }
        } else {
            this.isFitsSystemWindows(contentView, true);
        }
    }
    protected  void  updateWindowBackground(StatusBarDrawable statusBarDrawable)
    {
        View rootLayer = config.getRootView();
        rootLayer.setBackground(statusBarDrawable);
        View context=rootLayer.findViewById(android.R.id.content);
        context.setBackground(statusBarDrawable);
    }
    /**
     * 设置状态栏颜色
     *
     * @param statusBarDrawable
     */
    protected void setStatusBarDrawable(StatusBarDrawable statusBarDrawable) {
        //强制黑色字体状态栏
        isChangeColor = true;
        boolean forceBlack = config.isForeBlackColor();
        ActivityUtil.setDarkBar(thisObject, forceBlack);
        if (statusBarDrawable != null) {
            this.log("更新颜色值" + ColorUtil.toHexEncoding(statusBarDrawable.getColor()));
            updateWindowBackground(statusBarDrawable);
            /**
             * 反向设置ActionBar颜色
             */
            int color = statusBarDrawable.getColor();
            boolean changeColor = ColorUtil.TestColorOfWhite(color, 55);
            boolean reverseSetting = config.isReverseActionBarColor();
            if (reverseSetting && thisObject.getActionBar() != null) {
                thisObject.getActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
            ActivityUtil.setDarkBar(thisObject, changeColor);
            if (changeColor) {
                this.log("状态栏强制设置黑色状态栏字体");
            }
        }
    }

    public boolean isTouchGetColor() {
        return config.isTouchGetColor();
    }

    public void showNotification() {
        if (config.isAppChangeStatusBar()) {
            if (config.isShowNotification() && mResources != null) {
                showNotification(thisObject, mResources);
            }
        }
    }
    protected  String DEBUG_PACKAGE_NAME="com.tencent";
    /*
     * 根据配置更新顶栏颜色
     */
    public void updateStatusBarLit() {
        this.reloadConfig();
        isUpdateColor = false;
        boolean flag = ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        flag = flag || ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        this.DebugLog("1");
        if (!flag) {
            this.DebugLog("2");
            boolean change = config.isChangeStatusBar();
            if (change) {
                this.DebugLog("3");
                StatusBarDrawable statusBarDrawable = config.getStatusBarDrawable(true);
                if (statusBarDrawable != null) {
                    this.DebugLog("4");
                    isChangeColor = true;
                    if (config.isChangeColorMode()) {
                        //变色龙模式
                        this.DebugLog("5");
                        updateStatusBarWindowColor(statusBarDrawable);
                    } else {
                        //沉浸模式
                        this.DebugLog("6");
                        changeContextViewPadding();
                        ActivityUtil.setStatusBarLit(thisObject);
                        this.setStatusBarDrawable(statusBarDrawable);
                    }
                } else {
                    mustChange = true;
                }
            }
        }
    }

    /**
     * 更新顶栏颜色
     */
    public void updateStatusBarColor() {
        if (mustChange) {
            mustChange = false;
            this.updateStatusBarLit();
        } else {
            if (isChangeColor && !isUpdateColor) {
                isUpdateColor = true;
                config.setAutomaticColor(null);
                StatusBarDrawable drawable = config.getStatusBarDrawable(false);
                if (config.isChangeColorMode()) {
                    //变色龙模式
                    this.updateStatusBarWindowColor(drawable);
                } else {
                    //沉浸模式
                    this.log("沉浸模式单独重新更新颜色");
                    this.setStatusBarDrawable(drawable);
                }
            }
        }
    }

    /**
     * 显示通知栏消息
     *
     * @param activity
     * @param resources
     */
    public static void showNotification(Activity activity, Resources resources) {
        String packageName = activity.getPackageName();
        String activityName = activity.getClass().getName();
        ComponentName cnActivity = new ComponentName(FileUtil.THIS_PACKAGE_NAME, FileUtil.THIS_PACKAGE_NAME + ".app.ActivitySettingActivity");
        ComponentName cnApp = new ComponentName(FileUtil.THIS_PACKAGE_NAME, FileUtil.THIS_PACKAGE_NAME + ".app.AppSettingActivity");
        Intent activityIntent = new Intent().setComponent(cnActivity);
        Intent appIntent = new Intent().setComponent(cnApp);

        int color = ActivityUtil.getStatusBarColor(activity);
        ///页面设置
        activityIntent.putExtra("packageName", packageName);
        activityIntent.putExtra("activityName", activityName);
        activityIntent.putExtra("color", color);
        //应用设置

        appIntent.putExtra("packageName", packageName);
        appIntent.putExtra("appName", AppUtil.getApplicationName(activity));
        appIntent.putExtra("color", color);
        //
        PendingIntent activityPendingIntent = PendingIntent.getActivity(activity, 0,
                activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent appPendingIntent = PendingIntent.getActivity(activity, 1,
                appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(activity);
        builder.setContentText(activityName);
        builder.setContentTitle(packageName);
        /*
        Bitmap bitmap=ColorUtil.ScreenShots(activity,false);
        builder.setLargeIcon(bitmap);
        */
        builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        builder.setAutoCancel(true);//点击消失
        builder.addAction(android.R.drawable.ic_menu_add,
                resources.getString(R.string.notification_add_activity), activityPendingIntent);
        builder.addAction(android.R.drawable.ic_menu_add,
                resources.getString(R.string.notification_add_app), appPendingIntent);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        nm.notify(1240, notification);
    }
}
