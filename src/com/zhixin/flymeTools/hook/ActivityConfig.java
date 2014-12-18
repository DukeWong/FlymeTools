package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.ConstUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import com.zhixin.flymeTools.controls.StatusBarDrawable;
import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by ZXW on 2014/12/18.
 */
public class ActivityConfig {
    private static XSharedPreferences appSharedPreferences = null;
    private static XSharedPreferences globalSharedPreferences = null;
    private XSharedPreferences activitySharedPreferences = null;

    public void setAutomaticColor(StatusBarDrawable automaticColor) {
        this.automaticColor = automaticColor;
    }

    private StatusBarDrawable automaticColor = null;
    private Drawable backgroundDrawable = null;
    private Drawable actionBarDrawable = null;
    private Activity thisActivity;
    private String packageName;
    private String activityName;

    public void log(String text) {
        LogUtil.log(activityName + " 消息:" + text);
    }

    public ActivityConfig(Activity activity) {
        thisActivity = activity;
        packageName = activity.getPackageName();
        activityName = activity.getClass().getName();
        activitySharedPreferences = FileUtil.getSharedPreferences(packageName, activityName);
        backgroundDrawable = thisActivity.getWindow().getDecorView().getBackground();
        synchronized (ActivityConfig.class) {
            if (globalSharedPreferences == null) {
                globalSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
            }
            if (appSharedPreferences == null) {
                appSharedPreferences = FileUtil.getSharedPreferences(packageName);
            }

        }
    }

    public void reload() {
        if (appSharedPreferences != null) {
            appSharedPreferences.reload();
        }
        if (globalSharedPreferences != null) {
            globalSharedPreferences.reload();
        }
        if (activitySharedPreferences != null) {
            activitySharedPreferences.reload();
        }
    }

    protected Drawable getActionBarDrawable() {
        if (actionBarDrawable == null) {
            actionBarDrawable = ActivityUtil.getSmartBarDrawable(thisActivity);
        }
        return actionBarDrawable;
    }

    /**
     * 是否强制状态栏字体
     *
     * @return
     */
    public boolean isForeBlackCorlor() {
        return this.getConfigBoolean(ConstUtil.FORCE_BLACK_COLOR, false);
    }

    /**
     * 是否反向设置ActionBar颜色
     *
     * @return
     */
    public boolean isReverseActionBarColor() {
        return this.getConfigBoolean(ConstUtil.REVERSE_SETTING, false);
    }

    /**
     * 是否需要更新状态栏
     *
     * @return
     */
    public boolean isChangeStatusBar() {
        boolean change = globalSharedPreferences.getBoolean(ConstUtil.PREFERENCE_TRANSLUCENT, false);
        if (change) {
            return this.getConfigBoolean(ConstUtil.PREFERENCE_TRANSLUCENT, false);
        }
        return false;
    }

    /**
     * 强制模式下是否预留状态栏
     *
     * @return
     */
    public boolean hasStatusBar() {
        return !this.getConfigBoolean(ConstUtil.BRIGHTLY_TATUS_BAR, false);
    }

    /**
     * 强制模式下是否预留ActionBar位置
     *
     * @return
     */
    public boolean hasActionBar() {
        return this.getConfigBoolean(ConstUtil.HAS_ACTIONBAR, false);
    }

    public boolean isStatusBarForceMode() {
        return this.getConfigBoolean(ConstUtil.FORCE_LIT_MODE, false);
    }

    protected StatusBarDrawable getAutomaticColor(boolean noCache, int barHeight) {
        if (noCache){automaticColor=null;}
        if (automaticColor == null) {
            Integer color = null;
            SharedPreferences sharedPreferences = thisActivity.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, 0);
            if (sharedPreferences.contains(activityName) && !noCache) {
                color = sharedPreferences.getInt(activityName, 0);
            } else {
                color = ActivityUtil.getStatusBarColor(thisActivity);
                if (color != null) {
                    this.log("自动识别颜色为" + color);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(activityName, color);
                    editor.commit();
                }
            }
            if (color != null) {
                automaticColor = new StatusBarDrawable(color.intValue(), backgroundDrawable, barHeight);
            } else {
                Drawable drawable = getActionBarDrawable();
                if (drawable instanceof ColorDrawable) {
                    automaticColor = new StatusBarDrawable(((ColorDrawable) drawable).getColor(), backgroundDrawable, barHeight);
                }
            }
        }
        return automaticColor;
    }

    /**
     * 使用自动获取颜色
     * @return
     */
    protected ColorDrawable getStatusBarDrawable(boolean useCache) {
        boolean useAutomaticColor = this.getConfigBoolean(ConstUtil.AUTOMATIC_COLOR_OPEN, true);
        StatusBarDrawable statusBarDrawable = null;
        int barHeight = ActivityUtil.getStatusBarHeight(thisActivity);
        if (useAutomaticColor) {
            statusBarDrawable = getAutomaticColor(useCache,barHeight);
        } else {
            if (appSharedPreferences.contains(ConstUtil.TRANSLUCENT_COLOR)) {
                String color = this.getConfigString(ConstUtil.TRANSLUCENT_COLOR, null);
                statusBarDrawable = new StatusBarDrawable(Color.parseColor(color), backgroundDrawable, barHeight);
            }
        }
        return statusBarDrawable;
    }

    /**
     * 获取Activity配置
     *
     * @param name
     * @param defValue
     * @return
     */
    public boolean getConfigBoolean(String name, boolean defValue) {
        return activitySharedPreferences.getBoolean(name, appSharedPreferences.getBoolean(name, defValue));
    }

    /**
     * 获取Activity配置
     *
     * @param name
     * @param defValue
     * @return
     */
    public String getConfigString(String name, String defValue) {
        return activitySharedPreferences.getString(name, appSharedPreferences.getString(name, defValue));
    }

    /**
     * 根据配置获取SmartBar背景
     *
     * @return
     */
    public Drawable getSmartBarDrawable() {
        String defaultType = globalSharedPreferences.getString(ConstUtil.SMARTBAR_DEFAULT_TYPE, null);
        boolean change = this.getConfigBoolean(ConstUtil.SMARTBAR_CHANGE, defaultType != "-100");
        Drawable smartBarDrawable = null;
        if (change) {
            String smartBarType = this.getConfigString(ConstUtil.SMARTBAR_TYPE, defaultType);
            if (smartBarType != null) {
                //自动设置等
                if (smartBarType.indexOf("#") == -1) {
                    //1为默认设置
                    String smartBarColor = "#FFFFFFFF";
                    smartBarType = smartBarType.equals("1") ? defaultType : smartBarType;
                    if (smartBarType.equals("0")) {
                        if (automaticColor == null) {
                            smartBarDrawable = this.getActionBarDrawable();
                        } else {
                            smartBarDrawable = new ColorDrawable(automaticColor.getColor());
                        }

                    } else {
                        if (smartBarType.equals("-1")) {
                            smartBarColor = this.getConfigString(ConstUtil.SMARTBAR_COLOR, smartBarColor);
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
        return smartBarDrawable;
    }
}
