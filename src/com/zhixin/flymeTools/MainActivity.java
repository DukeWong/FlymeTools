package com.zhixin.flymeTools;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TableLayout;
import com.zhixin.flymeTools.Util.ActivityUtil;
import com.zhixin.flymeTools.base.BaseActivity;
import com.zhixin.flymeTools.controls.TableLineDrawable;

public class MainActivity extends BaseActivity {
    public  static String THIS_PACKAGE_NAME=MainActivity.class.getPackage().getName();
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TableLayout mainTable=(TableLayout)this.findViewById(R.id.mainTable);
        TableLineDrawable drawable=new TableLineDrawable(Color.parseColor("#FFD9D9D9"),2,3);
        mainTable.setBackground(drawable);
        ActivityUtil.setSmartBarEnable(this);
    }
}
