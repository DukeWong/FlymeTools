package com.zhixin.flymeTools.Util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.zhixin.flymeTools.app.AppItem;
import com.zhixin.flymeTools.app.AppItemAdapter;

import java.util.Iterator;
import java.util.List;

/**
 * Created by zhixin on 2014/12/11.
 */
public class AppUtil {
    public static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemApp(Activity activity) {
        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取应用名称
     *
     * @param activity
     * @return
     */
    public static String getApplicationName(Activity activity) {
        PackageManager packageManager = null;
        ApplicationInfo applicationInfo = null;
        try {
            packageManager = activity.getPackageManager();
            applicationInfo = packageManager.getApplicationInfo(activity.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            applicationInfo = null;
        }
        String applicationName = packageManager.getApplicationLabel(applicationInfo).toString();
        return applicationName;
    }

    public static AppItemAdapter getAppItemAdapter(Context context, boolean includeSys) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        AppItemAdapter mAdapter = new AppItemAdapter(context);
        if (pinfo != null) {
            Iterator<PackageInfo> iter = pinfo.iterator();
            while (iter.hasNext()) {
                PackageInfo info = iter.next();
                boolean isSysApp = isSystemApp(info) || info.packageName.equals(FileUtil.THIS_PACKAGE_NAME);
                if (!isSysApp || includeSys) {
                    AppItem item = new AppItem(
                            info.packageName
                            , info.applicationInfo.loadLabel(packageManager).toString()
                            , isSysApp, info.applicationInfo.loadIcon(packageManager)
                    );
                    mAdapter.addItem(item);
                }
            }
        }
        return mAdapter;
    }
}
