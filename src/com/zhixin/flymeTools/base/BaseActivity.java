package com.zhixin.flymeTools.base;

import android.app.Activity;
import android.os.Bundle;
import com.zhixin.flymeTools.Util.ActivityUtil;

/**
 * Created by ZXW on 2014/12/5.
 */
public class BaseActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setStatusBarLit(this);
    }
}
