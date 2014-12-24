package com.zhixin.flymeTools.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ActivityUtil;


/**
 * Created by ZXW on 2014/12/17.
 */
public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ActivityUtil.setSmartBarEnable(this);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
        int length = ActivityUtil.getActionBarHeight(this);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            View rootView = this.getWindow().getDecorView();
            View view = this.getWindow().getDecorView().findViewById(android.R.id.content);
            view.setTop(100);
            rootView.layout(rootView.getLeft(), rootView.getTop() + 1, rootView.getRight(), rootView.getBottom());
            rootView.layout(rootView.getLeft(), rootView.getTop() - 1, rootView.getRight(), rootView.getBottom());
        }
    }
}
