package com.zhixin.flymeTools.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.Menu;
import android.view.MenuItem;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.base.BaseSettingActivity;

import java.io.File;

/**
 * Created by ZXW on 2014/12/12.
 */
public class AppSettingActivity extends BaseSettingActivity {
    private  String appName;
    private  String packageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = this.getIntent();
        appName=intent.getStringExtra("appName");
        packageName=intent.getStringExtra("packageName");
        super.onCreate(savedInstanceState);
        this.setTitle(appName);
        getWindow().setUiOptions(ActivityInfo.UIOPTION_SPLIT_ACTION_BAR_WHEN_NARROW);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 0, 0, "list");
        item.setIcon(R.drawable.ic_tab_list_icon);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent =new Intent();
        intent.putExtra("appName",appName);
        intent.putExtra("packageName",packageName);
        intent.setClass(this,ActivityListActivity.class);
        this.startActivity(intent);
        return true;
    }
    protected File getPreferencesDir() {
        return FileUtil.getSharedPreferencesRoot(packageName);
    }
    @Override
    protected PreferenceFragment onCreateFragment(Bundle savedInstanceState){
        AppSettingFragment appSettingFragment = new AppSettingFragment();
        appSettingFragment.setPackageName(packageName);
        appSettingFragment.setAppName(appName);
        if(this.getIntent().hasExtra("color")){
            appSettingFragment.setInitColor(this.getIntent().getIntExtra("color",0));
        }
        appSettingFragment.setDefaultType("1");
        return appSettingFragment;
    }
}
