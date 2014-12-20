package com.zhixin.flymeTools.app;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.AppUtil;

/**
 * Created by ZXW on 2014/12/5.
 */
public class AppListActivity extends ListActivity {
    private AppItemAdapter mAdapter;
    private AppItem mModifyingItem;
    private boolean isDeleteSystemApp = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView list = this.getListView();
        this.setTitle(this.getIntent().getStringExtra("title"));
        this.loadData();
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemClick(view, position, id);
            }
        });
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
        list.setFitsSystemWindows(true);
    }
    public void loadData() {
        mAdapter = AppUtil.getAppItemAdapter(this, !isDeleteSystemApp,false);
        setListAdapter(mAdapter);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        isDeleteSystemApp = !isDeleteSystemApp;
        item.setTitle(isDeleteSystemApp ? R.string.showSystemApp : R.string.noShowSystemApp);
        this.loadData();
        return true;
    }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        menu.add(1, 1, 1, isDeleteSystemApp ? R.string.showSystemApp : R.string.noShowSystemApp);
        return super.onCreatePanelMenu(featureId, menu);
    }
    public void onListItemClick(View view, int position, long id) {
        mModifyingItem = mAdapter.getAppItem(position);
        String packgeName = mModifyingItem.getPackgeName();
        String appName = mModifyingItem.getAppName();
        Intent intent = new Intent();
        intent.setClass(AppListActivity.this, AppSettingActivity.class);
        intent.putExtra("packageName", packgeName);
        intent.putExtra("appName", appName);
        this.startActivity(intent);
    }
}
