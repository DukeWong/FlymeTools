package com.zhixin.flymeTools;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ListView;
import com.zhixin.flymeTools.app.AppItem;
import com.zhixin.flymeTools.app.AppItemAdapter;
import com.zhixin.flymeTools.app.AppListUtil;
/**
 * Created by ZXW on 2014/12/5.
 */
public class AppListActivity extends ListActivity {
    private AppItemAdapter mAdapter;
    private ActionMode mActionMode;
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
    }
    public  void loadData(){
        mAdapter=AppListUtil.getAppItemAdapter(this, !isDeleteSystemApp);
        setListAdapter(mAdapter);
    }
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        isDeleteSystemApp=!isDeleteSystemApp;
        this.loadData();
        return  true;
    }
    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        menu.add(1, 1, 1, isDeleteSystemApp ? R.string.showSystemApp : R.string.noShowSystemApp);
        return super.onCreatePanelMenu(featureId, menu);
    }
    public void onListItemClick(View view, int position, long id) {
    }
}
