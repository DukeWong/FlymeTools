package com.zhixin.flymeTools.Util;

import android.os.Environment;
import com.zhixin.flymeTools.MainActivity;
import de.robv.android.xposed.XSharedPreferences;
import java.io.File;

/**
 * Created by zhixin on 2014/12/13.
 */
public class FileUtil {
    public  static String THIS_PACKAGE_NAME= MainActivity.THIS_PACKAGE_NAME;
    public  static  String SETTING="_setting";
    public static File getgetSharedPreferencesRoot(){
        return getgetSharedPreferencesRoot(THIS_PACKAGE_NAME);
    }
    public static File getgetSharedPreferencesRoot(String packageName){
        File file = new File(Environment.getExternalStorageDirectory(),"data/" + packageName+"/shared_prefs");
        return  file;
    }
    public static XSharedPreferences getSharedPreferences(String packageName){
        return  getSharedPreferences(packageName, packageName + "_preferences");
    }
    public static XSharedPreferences getSharedPreferences(String packageName, String prefFileName){
        File file = new File(getgetSharedPreferencesRoot(packageName),prefFileName+".xml");
        return  new XSharedPreferences(file);
    }
}
