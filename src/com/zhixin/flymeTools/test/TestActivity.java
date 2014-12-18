package com.zhixin.flymeTools.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.Util.ColorUtil;

/**
 * Created by ZXW on 2014/12/17.
 */
public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
        ColorUtil.TestColorOfWhite(-328966, 55);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("1", "onResume called.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("1", "onStop called.");
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("1", "onResume called.");
    }
}
