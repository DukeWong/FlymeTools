package com.zhixin.flymeTools.hook;
import android.content.pm.PackageItemInfo;
import android.os.Environment;
import com.zhixin.flymeTools.Util.FileUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/12/14.
 */
public class PackageNameHook extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws java.lang.Throwable {
        PackageItemInfo packageItemInfo=(PackageItemInfo)param.thisObject;
        boolean sUserRequired= XposedHelpers.getStaticBooleanField(Environment.class,"sUserRequired");
        if (!sUserRequired){
            XSharedPreferences sharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME,packageItemInfo.packageName+FileUtil.SETTING);
            boolean change= sharedPreferences.getBoolean("preference_replace_app_name", false);
            if (change){
                String app_name= sharedPreferences.getString("preference_app_name", null);
                if (app_name!=null){
                    param.setResult(app_name);
                }
            }
        }
    }
}
