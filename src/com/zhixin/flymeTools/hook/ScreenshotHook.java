package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
/**
 * Created by zhixin on 2014/12/15.
 */
public class ScreenshotHook extends XC_MethodHook {
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {return null;}
        v.buildDrawingCache();
        Bitmap bitmap =v.getDrawingCache();
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
        }
        return bitmap;
    }
    public  static  String getActivityKeyColorName(Activity thisActivity){
        String activityName = thisActivity.getClass().getName();
        String KEY_COLOR = activityName + ".@_color$";
        return  KEY_COLOR;
    }
    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, thisActivity.getPackageName() + FileUtil.SETTING);
        Boolean change = xSharedPreferences.getBoolean(StatusBarHook.PREFERENCE_TRANSLUCENT, false);
        boolean useAutomaticColor = xSharedPreferences.getBoolean(StatusBarHook.AUTOMATIC_COLOR_OPEN, true);
        if (change && useAutomaticColor){
            String activityName = thisActivity.getClass().getName();
            String KEY_COLOR=getActivityKeyColorName(thisActivity);
            SharedPreferences sharedPreferences= thisActivity.getSharedPreferences(thisActivity.getPackageName()+FileUtil.SETTING, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(KEY_COLOR)){
                LogUtil.log("获取颜色开始->" + activityName);
                View decorView = thisActivity.getWindow().getDecorView();
                Bitmap bitmap = loadBitmapFromView(decorView);
                if (bitmap != null) {
                    int color = bitmap.getPixel(bitmap.getWidth() / 2, ActivityUtil.getStatusBarHeight(thisActivity) + 1);
                    SharedPreferences.Editor editor= sharedPreferences.edit();
                    editor.putInt(KEY_COLOR, color);
                    editor.commit();
                    XposedHelpers.setAdditionalInstanceField(thisActivity, StatusBarHook.STATUS_BAR_DRAWABLE, new ColorDrawable(color));
                    StatusBarHook.change(thisActivity);
                    LogUtil.log("获取到颜色->" + activityName + "color:" + color);
                } else {
                    LogUtil.log("获取颜色出错->" + activityName + ":Bitmap:为空");
                }
            }
        }
    }
}
