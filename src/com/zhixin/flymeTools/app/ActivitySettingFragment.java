package com.zhixin.flymeTools.app;

import android.content.SharedPreferences;
import com.zhixin.flymeTools.Util.ConstUtil;

/**
 * Created by ZXW on 2014/12/18.
 */
public class ActivitySettingFragment extends AppBaseFragment {
    @Override
    protected void bindSmartBarDefault(SharedPreferences sharedPreferences) {
        SharedPreferences appSharedPreferences = this.getActivity().getSharedPreferences(this.getPackageName() + ConstUtil.DEF_PREFERENCES, 0);
        m_smartbar_type.setValue(sharedPreferences.getString(preference_smartbar_type, appSharedPreferences.getString(preference_smartbar_type, null)));
        m_replace_smartbar.setChecked(sharedPreferences.getBoolean(preference_replace_smartbar, appSharedPreferences.getBoolean(preference_replace_smartbar, false)));
        String color = sharedPreferences.getString(preference_smartbar_color, appSharedPreferences.getString(preference_smartbar_color, null));
        String smart_type = sharedPreferences.getString(preference_smartbar_type, appSharedPreferences.getString(preference_smartbar_type, null));
        if (color != null) {
            m_smartbar_color.setTitle(color);
        }
        //修改Smartbar类型
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
    protected void init() {
        super.init();
        if (activityName != null) {
            this.getPreferenceManager().setSharedPreferencesName(activityName);
        }
    }

    @Override
    protected void bindStatusBarDefault(SharedPreferences sharedPreferences) {
        SharedPreferences appSharedPreferences = this.getActivity().getSharedPreferences(this.getPackageName() + ConstUtil.DEF_PREFERENCES, 0);
        m_translucent_compulsory.setChecked(sharedPreferences.getBoolean(preference_translucent_compulsory, appSharedPreferences.getBoolean(preference_translucent_compulsory, false)));
        m_force_brightly_lit_mode.setChecked(sharedPreferences.getBoolean(preference_force_brightly_lit_mode, appSharedPreferences.getBoolean(preference_force_brightly_lit_mode, false)));
        m_reverse_setting_action_bar.setChecked(sharedPreferences.getBoolean(preference_reverse_setting_action_bar, appSharedPreferences.getBoolean(preference_reverse_setting_action_bar, false)));
        m_brightly_lit_status_bar.setChecked(sharedPreferences.getBoolean(preference_brightly_lit_status_bar, appSharedPreferences.getBoolean(preference_brightly_lit_status_bar, false)));
        m_has_ActionBar.setChecked(sharedPreferences.getBoolean(preference_has_ActionBar, appSharedPreferences.getBoolean(preference_has_ActionBar, false)));
        m_force_black_color.setChecked(sharedPreferences.getBoolean(preference_force_black_color, appSharedPreferences.getBoolean(preference_force_black_color, false)));
        m_automatic_color_open.setChecked(sharedPreferences.getBoolean(preference_automatic_color_open, appSharedPreferences.getBoolean(preference_automatic_color_open, false)));
        m_has_NavigationBar.setChecked(sharedPreferences.getBoolean(preference_has_NavigationBar, appSharedPreferences.getBoolean(preference_has_NavigationBar, false)));
        m_translucent_color.setValue(sharedPreferences.getString(preference_translucent_color, appSharedPreferences.getString(preference_translucent_color, null)));
        String color = sharedPreferences.getString(preference_translucent_color, appSharedPreferences.getString(preference_translucent_color, null));
        m_translucent_color.setValue(color);
        if (color != null) {
            m_translucent_color.setTitle(color);
        }
    }
}
