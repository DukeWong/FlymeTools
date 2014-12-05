package com.zhixin.flymeTools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by ZXW on 2014/12/5.
 */
public class AppListActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.applist);
        Intent intent=this.getIntent();
        TextView textView=(TextView)this.findViewById(R.id.appTextView);
        textView.setText(intent.getStringExtra("title"));
    }
}
