package com.zhixin.flymeTools.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ConstUtil;
import com.zhixin.flymeTools.base.BaseFragment;

/**
 * Created by ZXW on 2014/12/24.
 */
public class SysSettingFragment extends BaseFragment {
    public void setAppName(String appName) {
        this.appName = appName;
    }

    private String appName;

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    private String packageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.getPreferenceManager().setSharedPreferencesName(packageName + ConstUtil.DEF_PREFERENCES);
        addPreferencesFromResource(R.xml.app_settting);
        SharedPreferences sharedPreferences = this.getPreferenceManager().getSharedPreferences();
        AppSettingFragment.bindAppTitle(sharedPreferences, this, appName);
    }
}
