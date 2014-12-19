package com.zhixin.flymeTools.app;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ColorUtil;
import com.zhixin.flymeTools.controls.ColorPickerPreference;

/**
 * Created by ZXW on 2014/12/18.
 */
public abstract class AppBaseFragment extends PreferenceFragment {


    public void setInitColor(Integer initColor) {
        this.initColor = initColor;
    }

    private   Integer initColor;
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    protected String activityName;
    private String appName;
    private String packageName;
    //导航栏字符串
    protected String preference_smartbar_type;
    protected String preference_replace_smartbar;
    protected String preference_smartbar_color;
    //导航栏控件
    protected ListPreference m_smartbar_type = null;
    protected SwitchPreference m_replace_smartbar = null;
    protected ColorPickerPreference m_smartbar_color = null;

    //状态栏字符串
    protected String preference_translucent_compulsory;
    protected String preference_force_brightly_lit_mode;
    protected String preference_reverse_setting_action_bar;
    protected String preference_brightly_lit_status_bar;
    protected String preference_has_ActionBar;
    protected String preference_force_black_color;
    protected String preference_automatic_color_open;
    protected String preference_translucent_color;
    //状态栏控件
    protected SwitchPreference m_translucent_compulsory = null;
    protected SwitchPreference m_force_brightly_lit_mode = null;
    protected SwitchPreference m_reverse_setting_action_bar = null;
    protected SwitchPreference m_brightly_lit_status_bar = null;
    protected SwitchPreference m_has_ActionBar = null;
    protected SwitchPreference m_force_black_color = null;
    protected SwitchPreference m_automatic_color_open = null;
    protected ColorPickerPreference m_translucent_color = null;

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    protected void init() {
        preference_smartbar_type = getResources().getString(R.string.preference_smartbar_type);
        preference_replace_smartbar = getResources().getString(R.string.preference_replace_smartbar);
        preference_smartbar_color = getResources().getString(R.string.preference_smartbar_color);
        preference_translucent_compulsory = this.getResources().getString(R.string.preference_translucent_compulsory);
        preference_force_brightly_lit_mode = this.getResources().getString(R.string.preference_force_brightly_lit_mode);
        preference_reverse_setting_action_bar = this.getResources().getString(R.string.preference_reverse_setting_action_bar);
        preference_brightly_lit_status_bar = this.getResources().getString(R.string.preference_brightly_lit_status_bar);
        preference_has_ActionBar = this.getResources().getString(R.string.preference_has_ActionBar);
        preference_force_black_color = this.getResources().getString(R.string.preference_force_black_color);
        preference_automatic_color_open = this.getResources().getString(R.string.preference_automatic_color_open);
        preference_translucent_color = this.getResources().getString(R.string.preference_translucent_color);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.init();
        addPreferencesFromResource(R.xml.activity_setting);
        this.reload(false);
    }
    public  void reload(boolean clear){
        SharedPreferences sharedPreferences = this.getPreferenceManager().getSharedPreferences();
        if (clear){
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.clear();
            editor.commit();
        }
        this.bindSmartBar(sharedPreferences);
        this.bindStatusBar(sharedPreferences);
    }
    protected abstract void bindSmartBarDefault(SharedPreferences sharedPreferences);

    protected abstract void bindStatusBarDefault(SharedPreferences sharedPreferences);

    protected void onReplace_smartbarChange(boolean isSelect) {
        m_smartbar_type.setEnabled(isSelect);
        onSmartbar_typeChange(m_smartbar_type.getValue());
    }

    protected void onSmartbar_typeChange(String smart_type) {
        m_smartbar_color.setEnabled("-1".equals(smart_type) && m_smartbar_type.isEnabled());
    }

    /**
     * 绑定SmartBar设置
     *
     * @param sharedPreferences
     */
    protected void bindSmartBar(SharedPreferences sharedPreferences) {
        m_replace_smartbar = (SwitchPreference) findPreference(preference_replace_smartbar);
        m_smartbar_type = (ListPreference) findPreference(preference_smartbar_type);
        m_smartbar_color = (ColorPickerPreference) findPreference(preference_smartbar_color);
        this.bindSmartBarDefault(sharedPreferences);
        if (initColor!=null){
            m_smartbar_color.setColor(initColor);
            m_smartbar_color.setTitle(ColorUtil.toHexEncoding(initColor));
        }
        onReplace_smartbarChange(m_replace_smartbar.isChecked());
        if (m_replace_smartbar != null) {
            m_replace_smartbar.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    m_smartbar_type.setEnabled((Boolean) newValue);
                    return true;
                }
            });
        }
        m_smartbar_type.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                int index = m_smartbar_type.findIndexOfValue(newValue.toString());
                CharSequence[] entries = m_smartbar_type.getEntries();
                m_smartbar_type.setTitle(entries[index]);
                if (m_smartbar_color != null) {
                    m_smartbar_color.setEnabled(newValue.toString().equals("-1"));
                }
                return true;
            }
        });
        if (m_smartbar_color != null) {
            m_smartbar_color.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    m_smartbar_color.setTitle(newValue.toString());
                    return true;
                }
            });
        }
    }

    protected void onTranslucent_compulsoryChange(boolean isSelect) {
        m_force_brightly_lit_mode.setEnabled(isSelect);
        m_force_black_color.setEnabled(isSelect);
        m_reverse_setting_action_bar.setEnabled(isSelect);
        m_automatic_color_open.setEnabled(isSelect);
        onTranslucent_colorChange(isSelect);
        onForce_brightly_lit_modeChange(m_force_brightly_lit_mode.isChecked());
    }
    protected void onForce_brightly_lit_modeChange(boolean isSelect) {
        m_brightly_lit_status_bar.setEnabled(isSelect);
        m_has_ActionBar.setEnabled(isSelect);
    }
    protected void onTranslucent_colorChange(boolean isSelect) {
        m_translucent_color.setEnabled(!isSelect && m_automatic_color_open.isEnabled());
    }

    /**
     * 绑定状态栏设置
     *
     * @param sharedPreferences
     */
    protected void bindStatusBar(SharedPreferences sharedPreferences) {
        m_translucent_compulsory = (SwitchPreference) findPreference(preference_translucent_compulsory);
        m_force_brightly_lit_mode = (SwitchPreference) findPreference(preference_force_brightly_lit_mode);
        m_reverse_setting_action_bar = (SwitchPreference) findPreference(preference_reverse_setting_action_bar);
        m_brightly_lit_status_bar = (SwitchPreference) findPreference(preference_brightly_lit_status_bar);
        m_has_ActionBar = (SwitchPreference) findPreference(preference_has_ActionBar);
        m_force_black_color = (SwitchPreference) findPreference(preference_force_black_color);
        m_automatic_color_open = (SwitchPreference) findPreference(preference_automatic_color_open);
        m_translucent_color = (ColorPickerPreference) findPreference(preference_translucent_color);
        this.bindStatusBarDefault(sharedPreferences);
        this.onTranslucent_compulsoryChange(m_translucent_compulsory.isChecked());
        m_translucent_compulsory.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                onTranslucent_compulsoryChange((Boolean) newValue);
                return true;
            }
        });
        m_force_brightly_lit_mode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                onForce_brightly_lit_modeChange((Boolean) newValue);
                return true;
            }
        });
        m_automatic_color_open.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                onTranslucent_colorChange((Boolean) newValue);
                return true;
            }
        });
        if (initColor!=null){
            m_translucent_color.setColor(initColor);
            m_translucent_color.setTitle(ColorUtil.toHexEncoding(initColor));
        }
        if (m_translucent_color != null) {
            m_translucent_color.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    m_translucent_color.setTitle(newValue.toString());
                    return true;
                }
            });
        }
    }
}
