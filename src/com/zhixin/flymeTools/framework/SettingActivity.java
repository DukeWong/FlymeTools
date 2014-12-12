package com.zhixin.flymeTools.framework;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.zhixin.flymeTools.base.FragmentActivity;
/**
 * Created by ZXW on 2014/12/12.
 */
public class SettingActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        this.setTitle(intent.getStringExtra("title"));
    }
    @Override
    protected PreferenceFragment OnCreateFragment(Bundle savedInstanceState){
        SettingFragement settingFragement = new SettingFragement();
        return  settingFragement;
    }
}
