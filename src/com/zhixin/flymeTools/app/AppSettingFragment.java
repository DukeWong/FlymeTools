package com.zhixin.flymeTools.app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ColorUtil;
import com.zhixin.flymeTools.Util.ConvertUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.controls.ColorPickerPreference;

/**
 * Created by ZXW on 2014/12/12.
 */
public class AppSettingFragment extends AppBaseFragment {
    private String packageName;
    private String appName;
    private String defaultType = null;

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setDefaultType(String defaultType) {
        this.defaultType = defaultType;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packgeName) {
        this.packageName = packgeName;
    }

    /**
     * 设置标题配置
     *
     * @param sharedPreferences
     */
    protected void bindAppTile(SharedPreferences sharedPreferences) {
        String preference_app_name = getResources().getString(R.string.preference_app_name);
        String preference_replace_app_name = getResources().getString(R.string.preference_replace_app_name);
        final EditTextPreference editTextPreference = (EditTextPreference) findPreference(preference_app_name);
        final SwitchPreference replace_app_checkBox = (SwitchPreference) findPreference(preference_replace_app_name);
        if (!sharedPreferences.getBoolean(preference_replace_app_name, false)) {
            editTextPreference.setEnabled(false);
        }
        //设置修改的应用名称
        String app_name = sharedPreferences.getString(preference_app_name, appName);
        if (editTextPreference != null) {
            editTextPreference.setDefaultValue(app_name);
            editTextPreference.setTitle(app_name);
        }
        if (replace_app_checkBox != null) {
            replace_app_checkBox.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editTextPreference.setEnabled((Boolean) newValue);
                    return true;
                }
            });
        }
        if (editTextPreference != null) {
            editTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editTextPreference.setTitle(newValue.toString());
                    return true;
                }
            });
        }
    }

    @Override
    protected void init() {
        super.init();
        if (packageName != null) {
            this.getPreferenceManager().setSharedPreferencesName(packageName + "_preferences");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_settting);
        SharedPreferences sharedPreferences = this.getPreferenceManager().getSharedPreferences();
        this.bindAppTile(sharedPreferences);
    }

    @Override
    protected void bindSmartBarDefault(SharedPreferences sharedPreferences) {
        if (m_smartbar_color != null) {
            String color = sharedPreferences.getString(preference_smartbar_color, null);
            if (color != null) {
                m_smartbar_color.setTitle(color);
            }
        }
        if (!sharedPreferences.getBoolean(preference_replace_smartbar, false)) {
            m_smartbar_type.setEnabled(false);
        }
        //修改Smartbar类型
        String smart_type = sharedPreferences.getString(preference_smartbar_type, "1");
        if (smart_type != null) {
            m_smartbar_color.setEnabled("-1".equals(smart_type));
        }
        int index = m_smartbar_type.findIndexOfValue(String.valueOf(smart_type));
        if (index != -1) {
            CharSequence[] entries = m_smartbar_type.getEntries();
            m_smartbar_type.setTitle(entries[index]);
        }
    }

    @Override
    protected void bindStatusBarDefault(SharedPreferences sharedPreferences) {
        m_translucent_compulsory.setChecked(sharedPreferences.getBoolean(preference_translucent_compulsory, false));
        m_force_brightly_lit_mode.setChecked(sharedPreferences.getBoolean(preference_force_brightly_lit_mode, false));
        m_automatic_color_open.setChecked(sharedPreferences.getBoolean(preference_automatic_color_open, true));
        String color = sharedPreferences.getString(preference_translucent_color, null);
        m_translucent_color.setValue(color);
        if (color!=null){
            m_translucent_color.setTitle(color);
        }
    }
}