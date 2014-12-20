package com.zhixin.flymeTools.base;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.zhixin.flymeTools.Util.FileUtil;

/**
 * Created by zhixin on 2014/12/20.
 */
public class BaseFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getPreferenceManager().setSharedPreferencesMode(FileUtil.FILE_MODE);
    }
}
