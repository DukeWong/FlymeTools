package com.zhixin.flymeTools.hook;

import android.content.SharedPreferences;
import android.content.pm.PackageItemInfo;
import android.os.Environment;
import com.zhixin.flymeTools.Util.ConstUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/12/14.
 */
public class AppNameHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws java.lang.Throwable {
        PackageItemInfo packageItemInfo = (PackageItemInfo) param.thisObject;
        boolean sUserRequired = XposedHelpers.getStaticBooleanField(Environment.class, "sUserRequired");
        if (!sUserRequired) {
            SharedPreferences sharedPreferences = FileUtil.getSharedPreferences(packageItemInfo.packageName);
            boolean change = sharedPreferences.getBoolean(ConstUtil.IS_REPLACE_APP_NAME, false);
            if (change) {
                String app_name = sharedPreferences.getString(ConstUtil.REPLACE_APP_NAME_KEY, null);
                if (app_name != null) {
                    param.setResult(app_name);
                }
            }
        }
    }
}
