package com.zhixin.flymeTools;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.zhixin.flymeTools.Util.ActivityUtil;

/**
 * Created by ZXW on 2014/12/17.
 */
public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View tile=this.findViewById(android.R.id.title);
        setContentView(R.layout.test);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

    }
}