package com.zhixin.flymeTools;

import android.app.Activity;
import android.os.Bundle;
import com.zhixin.flymeTools.Util.ActivityUtil;

/**
 * Created by zhixin on 2014/12/22.
 */
public class AboutActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this,true);
    }
}
