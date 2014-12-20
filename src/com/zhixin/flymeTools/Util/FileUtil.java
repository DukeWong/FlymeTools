package com.zhixin.flymeTools.Util;

import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import com.zhixin.flymeTools.MainActivity;
import de.robv.android.xposed.XSharedPreferences;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by zhixin on 2014/12/13.
 */
public class FileUtil {
    public static String THIS_PACKAGE_NAME = MainActivity.THIS_PACKAGE_NAME;
    public static File SHARED_PREFERENCES_FOLDER = null;
    /**
     * 共享数据权限
     */
    public static int FILE_MODE = Context.MODE_WORLD_READABLE | Context.MODE_MULTI_PROCESS;

    public static File getSharedPreferencesRoot() {
        File file = new File(Environment.getDataDirectory(), "data/" + THIS_PACKAGE_NAME + "/shared_prefs");
        return file;
    }

    public static File getSharedPreferencesRoot(String packageName) {
        String folder = "data/" + THIS_PACKAGE_NAME;
        if (StringUtil.isNotEmpty(packageName)) {
            folder += "/apps_prefs/" + packageName + "/shared_prefs";
        } else {
            folder += "/shared_prefs";
        }
        File file = new File(Environment.getDataDirectory(), folder);
        return file;
    }

    public static boolean restorePreferencesDir(Context context) {
        if (SHARED_PREFERENCES_FOLDER != null) {
            return changePreferencesDir(context, SHARED_PREFERENCES_FOLDER);
        }
        return true;
    }

    public static boolean changePreferencesDir(Context context, File folder) {
        try {
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            Field field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(context);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            if (SHARED_PREFERENCES_FOLDER == null) {
                SHARED_PREFERENCES_FOLDER = (File) field.get(obj);
            }
            // 创建自定义路径
            FileUtil.mkdirs(folder, true);
            // 修改mPreferencesDir变量的值
            field.set(obj, folder);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 创建文件夹，并给予执行权限
     *
     * @param folder
     * @param executable
     */
    public static void mkdirs(File folder, boolean executable) {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (executable) {
            File file = folder;
            while (file != null) {
                file.setExecutable(true, false);
                file = file.getParentFile();
            }
        }
    }

    /**
     * 返回全局配置文件
     *
     * @return
     */
    public static XSharedPreferences getSharedPreferences() {
        File file = new File(getSharedPreferencesRoot(), MainActivity.THIS_PACKAGE_NAME + ConstUtil.DEF_PREFERENCES + ".xml");
        XSharedPreferences xSharedPreferences = new XSharedPreferences(file);
        xSharedPreferences.makeWorldReadable();
        return xSharedPreferences;
    }

    /**
     * 返回应用程序配置文件
     *
     * @param packageName
     * @return
     */
    public static XSharedPreferences getSharedPreferences(String packageName) {
        return getSharedPreferences(packageName, packageName + ConstUtil.DEF_PREFERENCES);
    }

    /**
     * 返回页面配置文件
     *
     * @param packageName
     * @param prefFileName
     * @return
     */
    public static XSharedPreferences getSharedPreferences(String packageName, String prefFileName) {
        File file = new File(getSharedPreferencesRoot(packageName), prefFileName + ".xml");
        XSharedPreferences xSharedPreferences = new XSharedPreferences(file);
        xSharedPreferences.makeWorldReadable();
        return xSharedPreferences;
    }
}
