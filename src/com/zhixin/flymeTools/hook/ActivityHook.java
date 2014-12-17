package com.zhixin.flymeTools.hook;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import com.zhixin.flymeTools.Util.*;
import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by ZXW on 2014/12/17.
 */
public class ActivityHook extends ObjectHook<Activity> {
    public static int STATUS_BAR_HEIGHT = 0;
    public static int ACTION_BAR_HEIGHT = 0;
    private boolean isSysApp = false;
    private String packageName = null;
    private String activityName = null;
    private View mActionView;
    private View mSplitView;
    private XSharedPreferences appSharedPreferences = null;
    private XSharedPreferences sharedPreferences = null;
    private Drawable actionBarDrawable = null;
    private StatusBarDrawable automaticColor = null;
    private Integer backgroundColor = null;
    private boolean isFirstLoad=false;
    /**
     * 已经修改够颜色标识
     */
    private Boolean isChangeColor = false;
    private boolean isUpdateColor = false;

    public ActivityHook(Activity thisObject) {
        super(thisObject);
        isSysApp = AppUtil.isSystemApp(thisObject);
        packageName = thisObject.getPackageName();
        activityName = thisObject.getClass().getName();
        Drawable drawable = thisObject.getWindow().getDecorView().getBackground();
        if (drawable != null) {
            if (drawable instanceof ColorDrawable) {
                backgroundColor = ((ColorDrawable) drawable).getColor();
            }
        }
        sharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
        appSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, packageName + FileUtil.SETTING);
    }

    public void log(String text) {
        LogUtil.log(activityName + " 消息:" + text);
    }

    public void reloadSharedPreferences() {
        sharedPreferences.reload();
        appSharedPreferences.reload();
    }

    /**
     * 根据配置更新Smartbar颜色
     */
    public void updateSmartbarColor() {
        synchronized (this){
            this.reloadSharedPreferences();
            String defaultType = sharedPreferences.getString(ConstUtil.SMARTBAR_DEFAULT_TYPE, null);
            boolean change = appSharedPreferences.getBoolean(ConstUtil.SMARTBAR_CHANGE, !isSysApp && defaultType != "-100");
            Drawable smartBarDrawable = null;
            if (change) {
                String smartBarType = appSharedPreferences.getString(ConstUtil.SMARTBAR_TYPE, defaultType);
                if (smartBarType != null) {
                    //自动设置等
                    if (smartBarType.indexOf("#") == -1) {
                        //1为默认设置
                        String smartBarColor = "#FFFFFFFF";
                        smartBarType = smartBarType.equals("1") ? defaultType : smartBarType;
                        if (smartBarType.equals("0")) {
                            smartBarDrawable = getActionBarDrawable();
                        } else {
                            if (smartBarType.equals("-1")) {
                                smartBarColor = appSharedPreferences.getString(ConstUtil.SMARTBAR_COLOR, smartBarColor);
                                int color = Color.parseColor(smartBarColor);
                                smartBarDrawable = new ColorDrawable(color);
                            }
                        }
                    } else {
                        int color = Color.parseColor(smartBarType);
                        smartBarDrawable = new ColorDrawable(color);
                    }
                }
            }
            if (smartBarDrawable != null) {
                SmartBarUtils.changeSmartBarColor(thisObject, smartBarDrawable);
            }
        }
    }

    /**
     * 获取ActionBar颜色
     *
     * @return
     */
    protected Drawable getActionBarDrawable() {
        if (actionBarDrawable == null) {
            actionBarDrawable = ActivityUtil.getSmartBarDrawable(thisObject);
        }
        return actionBarDrawable;
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

    /**
     * 修改内容视图的边距
     *
     * @return 是否需要修改颜色
     */
    protected void changeContextViewPadding() {
        boolean hasStatusBar = !appSharedPreferences.getBoolean(ConstUtil.BRIGHTLY_TATUS_BAR, false);
        boolean hasSmartBar = !appSharedPreferences.getBoolean(ConstUtil.BRIGHTLY_SMART_BAR, false);
        boolean hasActionBar = appSharedPreferences.getBoolean(ConstUtil.HAS_ACTIONBAR, false);
        View decorView = thisObject.getWindow().getDecorView();
        View contentView = decorView.findViewById(android.R.id.content);
        View titleView=decorView.findViewById(android.R.id.title);
        int top = 0, actionHeight = 0, bottom = 0;
        int statusBarHeight=ActivityUtil.getStatusBarHeight(thisObject);
        if (hasStatusBar) {
            top +=statusBarHeight;
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
            }
        }
        if (hasActionBar && actionHeight == 0) {
            top += ActivityUtil.getActionBarHeight(thisObject);
        }
        bottom = hasSmartBar ? bottom : 0;
        contentView.setPadding(0, top, 0, bottom);
        if(titleView!=null){
            this.log("标题栏名称:" + titleView.getClass().getName());
            this.log("标题栏显示:" + (titleView.getVisibility()==View.GONE));
            this.log("标题栏高度:" + titleView.getHeight());
        }
        int[] position = new int[2];
        contentView.getLocationInWindow(position);
        this.log("Y:" + position[0]);
        this.log("顶部:" + top);
        this.log("底部:" + bottom);
    }
    /**
     * 修改成沉浸状态栏
     */
    protected void changeStatusBarLit() {
        ColorDrawable statusBarDrawable = getStatusBarDrawable();
        if (statusBarDrawable != null) {
            if (ActivityUtil.setStatusBarLit(thisObject)) {
                changeContextViewPadding();
                this.setStatusBarDrawable(statusBarDrawable);
            }
        }
    }

    protected void setStatusBarDrawable(ColorDrawable statusBarDrawable) {
        //强制黑色字体状态栏
        isChangeColor = true;
        boolean forceBlack = appSharedPreferences.getBoolean(ConstUtil.FORCE_BLACK_COLOR, false);
        ActivityUtil.setDarkBar(thisObject, forceBlack);
        if (statusBarDrawable != null) {
            View rootLayer = thisObject.getWindow().getDecorView();
            rootLayer.setBackground(statusBarDrawable);
            /**
             * 反向设置ActionBar颜色
             */
            int color = statusBarDrawable.getColor();
            boolean changeColor = ColorUtil.TestColorOfWhite(color, 55);
            boolean reverseSetting = appSharedPreferences.getBoolean(ConstUtil.REVERSE_SETTING, false);
            if (reverseSetting && thisObject.getActionBar() != null) {
                thisObject.getActionBar().setBackgroundDrawable(new ColorDrawable(color));
            }
            if (changeColor) {
                ActivityUtil.setDarkBar(thisObject, changeColor);
                this.log("状态栏强制设置黑色状态栏字体");
            }

        }
    }

    protected ColorDrawable getStatusBarDrawable() {
        boolean useAutomaticColor = appSharedPreferences.getBoolean(ConstUtil.AUTOMATIC_COLOR_OPEN, true);
        StatusBarDrawable statusBarDrawable = null;
        int barHeight = ActivityUtil.getStatusBarHeight(thisObject);
        if (useAutomaticColor) {
            if (automaticColor == null) {
                Integer color = ActivityUtil.getStatusBarColor(thisObject);
                if (color != null) {
                    this.log("自动识别颜色为" + color);
                    automaticColor = new StatusBarDrawable(color.intValue(), backgroundColor, barHeight);
                } else {
                    Drawable drawable = getActionBarDrawable();
                    if (drawable instanceof ColorDrawable) {
                        automaticColor = new StatusBarDrawable(((ColorDrawable) drawable).getColor(), backgroundColor, barHeight);
                    }
                }
            }
            statusBarDrawable = automaticColor;
        } else {
            if (appSharedPreferences.contains(ConstUtil.TRANSLUCENT_COLOR)) {
                String color = appSharedPreferences.getString(ConstUtil.TRANSLUCENT_COLOR, null);
                statusBarDrawable = new StatusBarDrawable(Color.parseColor(color), backgroundColor, barHeight);
            }
        }
        return statusBarDrawable;
    }

    /**
     * 更新顶栏颜色
     */
    public void updateStatusBarColor() {
        if (isChangeColor && !isUpdateColor) {
            automaticColor = null;
            ColorDrawable statusBarDrawable = getStatusBarDrawable();
            this.setStatusBarDrawable(statusBarDrawable);
        }
    }

    /**
     * 根据配置更新顶栏颜色
     */
    public void updateStatusBarLit(boolean firstLoad) {
        synchronized (this){
            if (firstLoad || isFirstLoad){
                isFirstLoad=true;
                if (!isSysApp) {
                    this.reloadSharedPreferences();
                    boolean flag = ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    flag = flag || ActivityUtil.existFlag(thisObject, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    if (!flag) {
                        boolean change = sharedPreferences.getBoolean(ConstUtil.PREFERENCE_TRANSLUCENT, false);
                        if (change) {
                            change = appSharedPreferences.getBoolean(ConstUtil.PREFERENCE_TRANSLUCENT, false);
                            if (change) {
                                this.changeStatusBarLit();
                            }
                        }
                    }
                }
            }
        }
    }
}
