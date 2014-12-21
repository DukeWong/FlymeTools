package com.zhixin.flymeTools.app;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
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
    ActivitySettingFragment settingFragment;
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
        settingFragment = new ActivitySettingFragment();
        settingFragment.setActivityName(activityName);
        settingFragment.setPackageName(packageName);
        if(this.getIntent().hasExtra("color")){
            settingFragment.setInitColor(this.getIntent().getIntExtra("color",0));
        }
        return settingFragment;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem item = menu.add(0, 0, 0, "del");
        item.setIcon(R.drawable.ic_table_delete);
        item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm) ;
        builder.setMessage(R.string.del_config_file) ;
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file= new File( FileUtil.getSharedPreferencesRoot(packageName),activityName+".xml");
                if (file.exists()){ file.delete(); }
                settingFragment.reload(true);
            }
        });
        builder.setNegativeButton(R.string.No, null);
        builder.show();
        return false;
    }
}
