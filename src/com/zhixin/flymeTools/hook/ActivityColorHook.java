package com.zhixin.flymeTools.hook;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
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
    private ActivityState mState = new ActivityState();
    private Class<?> actionBarOverlayLayout;
    private int windowHeight;
    private ImageView mOverflowButton;
    private Drawable mOverflowButtonIcon;

    /**
     * 已经修改够颜色标识
     */
//    private static Class<?> actionBarOverlayLayout;
    public ActivityColorHook(final Activity thisObject, final Resources resources) {
        super(thisObject);
        mResources = resources;
        packageName = thisObject.getPackageName();
        activityName = thisObject.getClass().getName();
        config = new ActivityConfig(thisObject);
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        thisObject.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        windowHeight = mDisplayMetrics.heightPixels;

        try {
            actionBarOverlayLayout = Class.forName("com.android.internal.widget.ActionBarOverlayLayout");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 写日志
     * @param text
     */
    public void log(String text) {
        if (config.isShowAppLog()) {
            LogUtil.log(activityName + " 消息:" + text);
        }
    }

    /**
     * 重新读取配置
     */
    public void reloadConfig() {
        config.reload();
    }

    /**
     * 根据配置更新Smartbar颜色
     */
    public void updateSmartbarColor() {
        if (config != null) {
            Drawable smartBarDrawable = config.getSmartBarDrawable();
            if (smartBarDrawable != null) {
                if (smartBarDrawable instanceof ColorDrawable) {
                    ColorDrawable colorDrawable = (ColorDrawable) smartBarDrawable;
                    int color = colorDrawable.getColor();
                    if (ColorUtil.TestColorOfWhite(color, 55)) {
                        SmartBarUtils.changeSmartBarColor(thisObject, mResources.getDrawable(R.drawable.mz_smartbar_background));
                        mOverflowButtonIcon = mResources.getDrawable(R.drawable.mz_ic_sb_more);
                        this.updateOverflowButton(null);
                        return;
                    } else {
                        mOverflowButtonIcon = null;
                    }
                }
                SmartBarUtils.changeSmartBarColor(thisObject, smartBarDrawable);
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
    /*
    protected void updateStatusBarWindowColor(Drawable drawable) {
        if (mStatusBarWindow != null) {
            this.log("变色龙单独重新更新颜色");
            mStatusBarWindow.setBackground(drawable);
        }
    }
    */

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

    protected boolean getDecorViewFrameLayout(View decorView) {
        if (decorView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) decorView;
            int count = viewGroup.getChildCount();
            if (count > 0) {
                View view = viewGroup.getChildAt(0);
                if (actionBarOverlayLayout != null || actionBarOverlayLayout.isAssignableFrom(view.getClass())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 更新布局
     *
     * @param decorView
     * @param contentView
     * @param hasStatusBar
     * @param forceMode
     * @param hasActionBar
     * @return
     */
    protected boolean forceChangeStatusBarlit(View decorView, View contentView, boolean hasStatusBar, boolean forceMode, boolean hasActionBar) {
        this.log("模式:" + hasStatusBar + forceMode + hasActionBar);
        int statusBarHeight = ActivityUtil.getStatusBarHeight(thisObject);
        boolean isFitsSystemWindows = this.isFitsSystemWindows(contentView, false);
        boolean isActionBarFrameout = this.getDecorViewFrameLayout(decorView);
        if (!isFitsSystemWindows || forceMode) {
            int top = 0, actionHeight = 0, bottom = 0;
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
                } else {
                    if (actionBar.isShowing()) {
                        top += ActivityUtil.getActionBarHeight(thisObject);
                    }
                }
                if (config.hasNavigationBar()) {
                    if (splitView != null) {
                        bottom += splitView.getHeight();
                    }
                }
            }
            if (hasActionBar && actionHeight == 0) {
                top += ActivityUtil.getActionBarHeight(thisObject);
            }
            if (!isFitsSystemWindows || forceMode) {
                this.log("top:" + top + " bottom:" + bottom);
                this.log("ActionBar:" + isActionBarFrameout);
                this.log("TOP:" + contentView.getPaddingTop());
                this.log("Y:" + contentView.getY());
                //不是isActionBar 或者TOP！=Y修改
                if (!isActionBarFrameout || contentView.getPaddingTop() != contentView.getY()) {
                    contentView.setPadding(0, top, 0, bottom);
                    contentView.requestLayout();
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 修改内容视图的边距
     *
     * @return 是否需要修改颜色
     */
    public void updateContextViewPadding(int delay, boolean actionBarChange) {
        actionBarChange = actionBarChange || !mState.IS_DELAY_UPDATE_PADDING;
        if (actionBarChange) {
            if (delay > 0) {
                //延时执行
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        updateContextViewPadding(0, false);
                        mState.IS_DELAY_UPDATE_PADDING = true;
                    }
                }, delay);
            } else {
                if (mState.IS_CHANGE_COLOR) {
                    boolean hasStatusBar = config.hasStatusBar();
                    boolean forceMode = config.isStatusBarForceMode();
                    boolean hasActionBar = config.hasActionBar();
                    View decorView = thisObject.getWindow().getDecorView();
                    View contentView = decorView.findViewById(android.R.id.content);
                    //自动模式
                    if (!forceChangeStatusBarlit(decorView, contentView, hasStatusBar, forceMode, hasActionBar)) {
                        this.isFitsSystemWindows(contentView, true);
                    }
                }
            }
        }
    }

    protected void updateWindowBackground(StatusBarDrawable statusBarDrawable) {
        View rootLayer = config.getRootView();
        rootLayer.setBackground(statusBarDrawable);
        View context = rootLayer.findViewById(android.R.id.content);
        context.setBackground(statusBarDrawable);
    }

    /**
     * 更新菜单点击按钮
     *
     * @param button
     */
    public void updateOverflowButton(ImageView button) {
        if (button != null) {
            this.mOverflowButton = button;
        }
        if (mOverflowButtonIcon != null && mOverflowButton != null) {
            this.log("更新右侧图标");
            mOverflowButton.setImageDrawable(mOverflowButtonIcon);
        }
    }

    /**
     * 设置状态栏颜色
     *
     * @param statusBarDrawable
     */
    protected void setStatusBarDrawable(StatusBarDrawable statusBarDrawable) {
        //强制黑色字体状态栏
        mState.IS_CHANGE_COLOR = true;
        boolean forceBlack = config.isForeBlackColor();
        ActivityUtil.setDarkBar(thisObject, forceBlack);
        if (statusBarDrawable != null) {
            this.log("更新颜色值" + ColorUtil.toHexEncoding(statusBarDrawable.getColor()));
            updateWindowBackground(statusBarDrawable);
            //反向设置ActionBar颜色
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

    /**
     * 显示通知消息
     */
    public void showNotification() {
        if (config.isAppChangeStatusBar()) {
            if (config.isShowNotification() && mResources != null) {
                ActivityUtil.showNotification(thisObject, mResources);
            }
        }
    }

    /**
     * 更新状态栏
     *
     * @param hasFocus
     */
    public void updateStatusBarLit(boolean hasFocus) {
        hasFocus = hasFocus || mState.IS_WINDOW_FOCUS;
        mState.IS_WINDOW_FOCUS = hasFocus;
        if (hasFocus) {
            this.reloadConfig();
            mState.IS_UPDATE_COLOR = false;
            boolean flag = ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            flag = flag || ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (!flag) {
                boolean change = config.isChangeStatusBar();
                if (change) {
                    StatusBarDrawable statusBarDrawable = config.getStatusBarDrawable(true);
                    if (statusBarDrawable != null) {
                        mState.IS_CHANGE_COLOR = true;
                        //沉浸模式
                        this.updateContextViewPadding(0, false);
                        ActivityUtil.setStatusBarLit(thisObject);
                        this.updateContextViewPadding(1000, false);
                        this.setStatusBarDrawable(statusBarDrawable);
                    } else {
                        mState.IS_MUST_CHANGE = true;
                    }
                }
            } else {
                if (mState.IS_CHANGE_COLOR) {
                    this.updateContextViewPadding(0, false);
                }
            }
        }
    }

    /**
     * 更新顶栏颜色
     */
    public void updateStatusBarColor() {
        if (mState.IS_MUST_CHANGE) {
            mState.IS_MUST_CHANGE = false;
            this.updateStatusBarLit(false);
        } else {
            if (mState.IS_CHANGE_COLOR && !mState.IS_UPDATE_COLOR) {
                mState.IS_UPDATE_COLOR = true;
                config.setAutomaticColor(null);
                StatusBarDrawable drawable = config.getStatusBarDrawable(false);
                //沉浸模式
                this.log("沉浸模式单独重新更新颜色");
                this.setStatusBarDrawable(drawable);
            }
        }
    }
}
