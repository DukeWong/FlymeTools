package com.zhixin.flymeTools.app;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by ZXW on 2014/12/12.
 */
public class AppSetting extends SettingActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        this.setTitle(intent.getStringExtra("appName"));
    }
    @Override
    protected PreferenceFragment OnCreateFragment(Bundle savedInstanceState){
        SettingFragement settingFragement = new SettingFragement();
        settingFragement.setPackgeName(this.getIntent().getStringExtra("packgeName"));
        return  settingFragement;
    }
}
