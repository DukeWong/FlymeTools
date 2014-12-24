package com.zhixin.flymeTools.framework;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
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
        addPreferencesFromResource(R.xml.framework_setting);
        SharedPreferences sharedPreferences = this.getPreferenceManager().getSharedPreferences();
        String preference_smartbar_default_type = getResources().getString(R.string.preference_smartbar_default_type);
        final ListPreference listPreference = (ListPreference) findPreference(preference_smartbar_default_type);
        //修改Smartbar类型
        String smart_type = sharedPreferences.getString(preference_smartbar_default_type, null);
        int index = listPreference.findIndexOfValue(String.valueOf(smart_type));
        if (index != -1) {
            CharSequence[] entries = listPreference.getEntries();
            listPreference.setTitle(entries[index]);
        }
        if (listPreference != null) {
            listPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    int index = listPreference.findIndexOfValue(newValue.toString());
                    CharSequence[] entries = listPreference.getEntries();
                    listPreference.setTitle(entries[index]);
                    return true;
                }
            });
        }
    }
}
