package com.zhixin.flymeTools.hook;

import android.content.SharedPreferences;
import android.content.pm.PackageItemInfo;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import com.zhixin.flymeTools.Util.ConstUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;

/**
 * Created by zhixin on 2014/12/23.
 */
public class AppIconHook extends XC_MethodHook {

    protected File getAppIconFile(String packageName) {
        return new File(Environment.getExternalStorageDirectory(), "data/" + FileUtil.THIS_PACKAGE_NAME + "/icon/" + packageName + ".png");
    }

    protected void beforeHookedMethod(MethodHookParam param) throws java.lang.Throwable {
        PackageItemInfo packageItemInfo = (PackageItemInfo) param.thisObject;
        boolean sUserRequired = XposedHelpers.getStaticBooleanField(Environment.class, "sUserRequired");
        if (!sUserRequired) {
            SharedPreferences sharedPreferences = FileUtil.getSharedPreferences(packageItemInfo.packageName);
            boolean change = sharedPreferences.getBoolean(ConstUtil.IS_REPLACE_APP_ICON, false);
            if (change) {
                File icon = this.getAppIconFile(packageItemInfo.packageName);
                if (icon.exists()) {
                    try {
                        Drawable drawable = BitmapDrawable.createFromPath(icon.getAbsolutePath());
                        if (drawable != null) {
                            param.setResult(drawable);
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }
    }
}
