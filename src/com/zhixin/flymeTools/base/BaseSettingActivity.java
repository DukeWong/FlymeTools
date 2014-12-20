package com.zhixin.flymeTools.base;

import android.app.Activity;
import android.content.ContextWrapper;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;

import java.io.File;
import java.lang.reflect.Field;

/**
 * Created by ZXW on 2014/12/12.
 */
public class BaseSettingActivity extends Activity {
    private boolean changePreferencesDir() {
        try {
            // 获取ContextWrapper对象中的mBase变量。该变量保存了ContextImpl对象
            Field field = ContextWrapper.class.getDeclaredField("mBase");
            field.setAccessible(true);
            // 获取mBase变量
            Object obj = field.get(this);
            // 获取ContextImpl。mPreferencesDir变量，该变量保存了数据文件的保存路径
            field = obj.getClass().getDeclaredField("mPreferencesDir");
            field.setAccessible(true);
            // 创建自定义路径
            File file = getPreferencesDir();
            if (!file.exists()) {
                file.mkdirs();
            }
            // 修改mPreferencesDir变量的值
            field.set(obj, file);
            return true;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
    protected File getPreferencesDir() {
        String packageName = this.getPackageName();
        return FileUtil.getSharedPreferencesRoot(packageName);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setDarkBar(this, true);
        this.changePreferencesDir();
        PreferenceFragment preferenceFragment = onCreateFragment(savedInstanceState);
        if (preferenceFragment != null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();
        }
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        View decorView = this.getWindow().getDecorView();
        View contentView = decorView.findViewById(android.R.id.content);
        if (contentView instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) contentView;
            viewGroup.setFitsSystemWindows(true);
            if (viewGroup.getChildCount() > 0) {
                viewGroup.getChildAt(0).setFitsSystemWindows(true);
            }
        }
        ActivityUtil.setStatusBarLit(this);
    }
    protected PreferenceFragment onCreateFragment(Bundle savedInstanceState) {
        return null;
    }
}
