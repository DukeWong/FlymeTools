package com.zhixin.flymeTools.base;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import com.zhixin.flymeTools.Util.SmartBarUtils;

/**
 * Created by ZXW on 2014/12/5.
 */
public class BaseActivity extends Activity {
    public static   void setStatusBarLit(Activity context){
        Window window = context.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    public  void  setSmartBarEnable(){
        final ActionBar bar = getActionBar();
        SmartBarUtils.setActionBarViewCollapsable(bar, true);
        bar.setDisplayOptions(0);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarLit(this);
    }
}
