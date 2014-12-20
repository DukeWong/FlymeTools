package com.zhixin.flymeTools;

import android.os.Bundle;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.base.BaseActivity;

public class MainActivity extends BaseActivity {
    public  static String THIS_PACKAGE_NAME=MainActivity.class.getPackage().getName();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ActivityUtil.setSmartBarEnable(this);
    }
}
