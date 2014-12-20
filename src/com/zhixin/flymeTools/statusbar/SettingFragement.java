package com.zhixin.flymeTools.statusbar;

import android.os.Bundle;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.base.BaseFragment;

/**
 * Created by ZXW on 2014/12/12.
 */
public class SettingFragement extends BaseFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.status_bar_setting);
    }
}
