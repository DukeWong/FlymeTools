package com.zhixin.flymeTools.base;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.ViewGroup;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;

import java.io.File;

/**
 * Created by ZXW on 2014/12/12.
 */
public class BaseSettingActivity extends Activity {
    protected File getPreferencesDir() {
        return FileUtil.getSharedPreferencesRoot();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setDarkBar(this, true);
        File folder = getPreferencesDir();
        FileUtil.changePreferencesDir(this, folder);
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
