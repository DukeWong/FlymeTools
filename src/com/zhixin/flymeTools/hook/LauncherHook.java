package com.zhixin.flymeTools.hook;
import android.view.View;
import com.zhixin.flymeTools.Util.FileUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
/**
 * Created by zhixin on 2014/12/14.
 */
public class LauncherHook implements  IClassPatch {
    @Override
    public void initPatch(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("com.android.launcher3.Launcher", loadPackageParam.classLoader, "onLongClick", View.class, new LongClickHook());
        XposedHelpers.findAndHookMethod("com.android.launcher3.WidgetGroupView", loadPackageParam.classLoader, "onLongClick", View.class, new LongClickHook());
    }
    public class LongClickHook extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws java.lang.Throwable {
            XSharedPreferences sharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
            Boolean locked= sharedPreferences.getBoolean("preference_app_launcher_locked", false);
            if (locked){param.setResult(false);}
        }
    }
}
