package com.zhixin.flymeTools.hook;

import android.app.Activity;
import com.zhixin.flymeTools.Util.ActivityUtil;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by ZXW on 2014/12/15.
 */
public class StatusBarHook extends XC_MethodHook {
    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        boolean isKikit=ActivityUtil.setStatusBarLit(thisActivity);
        if (isKikit){
            thisActivity.findViewById(android.R.id.content).setFitsSystemWindows(true);
        }
    }
}
