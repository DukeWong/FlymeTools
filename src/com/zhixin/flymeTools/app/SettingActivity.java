package com.zhixin.flymeTools.app;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by ZXW on 2014/12/12.
 */
public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceFragment preferenceFragment =OnCreateFragment(savedInstanceState);
        if (preferenceFragment!=null){
            getFragmentManager().beginTransaction().replace(android.R.id.content, preferenceFragment).commit();
        }
    }
    protected PreferenceFragment OnCreateFragment(Bundle savedInstanceState){
        return  null;
    }
}
