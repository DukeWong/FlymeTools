package com.zhixin.flymeTools.test;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

/**
 * Created by ZXW on 2014/12/18.
 */
public class TestListActivity extends ListActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView listView = this.getListView();
        View context = this.getWindow().findViewById(android.R.id.content);
        Log.d("测试", "是否为" + listView.equals(context));
    }
}
