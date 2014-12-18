package com.zhixin.flymeTools.hook;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import com.zhixin.flymeTools.Util.*;

/**
 * Created by ZXW on 2014/12/17.
 */
public class ActivityHook extends ObjectHook<Activity> {
    private String packageName = null;
    private String activityName = null;
    private View mActionView;
    private View mSplitView;
    private ActivityConfig config;
    /**
     * 已经修改够颜色标识
     */
    private Boolean isChangeColor = false;
    private boolean isUpdateColor = false;

    public ActivityHook(Activity thisObject) {
        super(thisObject);
        packageName = thisObject.getPackageName();
        activityName = thisObject.getClass().getName();
        config = new ActivityConfig(thisObject);
    }

    public void log(String text) {
        LogUtil.log(activityName + " 消息:" + text);
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

    /**
     * 设置状态栏颜色
     *
     * @param statusBarDrawable
     */
    protected void setStatusBarDrawable(ColorDrawable statusBarDrawable) {
        //强制黑色字体状态栏
        isChangeColor = true;
        boolean forceBlack = config.isForeBlackCorlor();
        ActivityUtil.setDarkBar(thisObject, forceBlack);
        if (statusBarDrawable != null) {
            View rootLayer = thisObject.getWindow().getDecorView();
            rootLayer.setBackground(statusBarDrawable);
            /**
             * 反向设置ActionBar颜色
             */
            int color = statusBarDrawable.getColor();
            boolean changeColor = ColorUtil.TestColorOfWhite(color, 55);
            boolean reverseSetting = config.isReverseActionBarColor();
            if (reverseSetting && thisObject.getActionBar() != null) {
                thisObject.getActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
            if (changeColor) {
                ActivityUtil.setDarkBar(thisObject, changeColor);
                this.log("状态栏强制设置黑色状态栏字体");
            }

        }
    }


    /**
     * 更新顶栏颜色
     */
    public void updateStatusBarColor() {
        if (isChangeColor && !isUpdateColor) {
            config.setAutomaticColor(null);
            this.setStatusBarDrawable(config.getStatusBarDrawable());
        }
    }

    /**
     * 根据配置更新顶栏颜色
     */
    public void updateStatusBarLit() {
        synchronized (this) {
            this.reloadConfig();
            boolean flag = ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            flag = flag || ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (!flag) {
                boolean change = config.isChangeStatusBar();
                if (change) {
                    ColorDrawable statusBarDrawable = config.getStatusBarDrawable();
                    if (statusBarDrawable != null) {
                        changeContextViewPadding();
                        ActivityUtil.setStatusBarLit(thisObject);
                        this.setStatusBarDrawable(statusBarDrawable);
                    }
                }
            }
        }
    }
}
