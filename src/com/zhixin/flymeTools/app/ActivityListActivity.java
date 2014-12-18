package com.zhixin.flymeTools.app;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import com.zhixin.flymeTools.Util.ActivityUtil;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by ZXW on 2014/12/18.
 */
public class ActivityListActivity extends ListActivity {
    private  String appName;
    private  String packageName;
    private List<String> items;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getListView().setFitsSystemWindows(true);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
        Intent intent=this.getIntent();
        appName=intent.getStringExtra("appName");
        this.setTitle(appName);
        packageName=intent.getStringExtra("packageName");
        try {
            PackageInfo packageInfo= this.getPackageManager().getPackageInfo(packageName,PackageManager.GET_UNINSTALLED_PACKAGES | PackageManager.GET_ACTIVITIES);
            ActivityInfo[] infos= packageInfo.activities;
            if (infos!=null && infos.length>0){
                items=new ArrayList<String>();
                for(int i=0;i<infos.length;i++){
                    items.add(infos[i].name);
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, items);
                setListAdapter(adapter);
                this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (items != null) {
                            String activityName = items.get(position);
                            Intent intent = new Intent();
                            intent.putExtra("appName", appName);
                            intent.putExtra("packageName", packageName);
                            intent.putExtra("activityName", activityName);
                            intent.setClass(ActivityListActivity.this, ActivitySettingActivity.class);
                            ActivityListActivity.this.startActivity(intent);
                        }
                    }
                });
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
