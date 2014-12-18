package com.zhixin.flymeTools.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.base.BaseSettingActivity;

import java.io.File;

/**
 * Created by ZXW on 2014/12/18.
 */
public class ActivitySettingActivity extends BaseSettingActivity {
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    private String packageName;
    private String activityName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        packageName=intent.getStringExtra("packageName");
        activityName=intent.getStringExtra("activityName");
        super.onCreate(savedInstanceState);
        this.setTitle(activityName);
        getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
    }
    @Override
    protected File getPreferencesDir() {
        return FileUtil.getSharedPreferencesRoot(packageName);
    }
    @Override
    protected PreferenceFragment onCreateFragment(Bundle savedInstanceState){
        ActivitySettingFragment settingFragment = new ActivitySettingFragment();
        settingFragment.setActivityName(activityName);
        settingFragment.setPackageName(packageName);
        return settingFragment;
    }
}
