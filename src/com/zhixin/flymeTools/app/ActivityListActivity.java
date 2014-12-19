package com.zhixin.flymeTools.app;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.FileUtil;

import java.io.File;
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (items!=null && items.size()>0){
            MenuItem item = menu.add(0, 0, 0, "del");
            item.setIcon(R.drawable.ic_table_delete);
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        AlertDialog.Builder builder  = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm) ;
        builder.setMessage(R.string.delConfigFile) ;
        builder.setPositiveButton(R.string.Yes, new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String folder= FileUtil.getSharedPreferencesRoot(packageName).getPath();
                for (int i=0;i<items.size();i++){
                    File file=new File(folder,items.get(i)+".xml");
                    if (file.exists()){
                        file.delete();
                    }
                }
                Toast.makeText(ActivityListActivity.this,R.string.successfully_del,Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton(R.string.No, null);
        builder.show();
        return false;
    }
}
