package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import com.zhixin.flymeTools.Util.*;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/12/15.
 */
public class ScreenshotHook extends XC_MethodHook {
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {
            return null;
        }
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
        }
        return bitmap;
    }
    public static String getActivityKeyColorName(Activity thisActivity) {
        return thisActivity.getClass().getName();
    }
    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        boolean hasFocus=(Boolean)param.args[0];
        if (hasFocus){
            Activity thisActivity = (Activity) param.thisObject;
            Object isFullScreenApp = XposedHelpers.getAdditionalInstanceField(thisActivity, ConstUtil.IS_FULLSCRE_ENAPP);
            if (isFullScreenApp == "-1") {
                if (!AppUtil.isSystemApp(thisActivity)) {
                    XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, thisActivity.getPackageName() + FileUtil.SETTING);
                    Boolean change = xSharedPreferences.getBoolean(ConstUtil.PREFERENCE_TRANSLUCENT, false);
                    boolean useAutomaticColor = xSharedPreferences.getBoolean(ConstUtil.AUTOMATIC_COLOR_OPEN, true);
                    if (change && useAutomaticColor) {
                        String activityName = thisActivity.getClass().getName();
                        String KEY_COLOR = getActivityKeyColorName(thisActivity);
                        SharedPreferences sharedPreferences = thisActivity.getSharedPreferences(thisActivity.getPackageName() + FileUtil.SETTING, Context.MODE_PRIVATE);
                        if (!sharedPreferences.contains(KEY_COLOR)) {
                            LogUtil.log("获取颜色开始->" + activityName);
                            View decorView = thisActivity.getWindow().getDecorView();
                            Bitmap bitmap = loadBitmapFromView(decorView);
                            if (bitmap != null) {
                                int color = bitmap.getPixel(bitmap.getWidth() / 2, ActivityUtil.getStatusBarHeight(thisActivity) + 5);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt(KEY_COLOR, color);
                                editor.commit();
                                XposedHelpers.setAdditionalInstanceField(thisActivity, ConstUtil.STATUS_BAR_DRAWABLE, new ColorDrawable(color));
                                StatusBarHook.changeStatusBar(thisActivity);
                                LogUtil.log("获取到颜色->" + activityName + "color:" + color);
                            } else {
                                LogUtil.log("获取颜色出错->" + activityName + ":Bitmap:为空");
                            }
                        }
                    }
                    //调整高度
                    ActivityUtil.changeContextViewPadding(thisActivity, xSharedPreferences.getBoolean(ConstUtil.RETAIN_STATUS, true));
                }
            }
        }
    }
}
