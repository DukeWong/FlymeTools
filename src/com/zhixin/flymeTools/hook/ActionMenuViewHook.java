package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.zhixin.flymeTools.Util.LogUtil;
import com.zhixin.flymeTools.Util.ReflectionUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by ZXW on 2014/12/23.
 */
public class ActionMenuViewHook extends ActivityMethodHook.WindowFocusMethod {

    public ActionMenuViewHook(final Resources mResources) {
        super(mResources);
    }

    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Object presenter = param.thisObject;
        Object mOverflowButton = ReflectionUtil.getObjectField(presenter, "mOverflowButton");
        if (mOverflowButton instanceof ImageView) {
            Context context = (Context) XposedHelpers.getObjectField(presenter, "mContext");
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                LogUtil.log("mOverflowButton被创建");
                ActivityMethodHook.doMethodHookCallBack(activity, mResources, param, this, mOverflowButton);
            }
        }
    }

    @Override
    public void doMethodHook(MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook, Object args) {
        if (args != null) {
            if (args instanceof ImageView) {
                LogUtil.log("mOverflowButton更新图标");
                activityColorHook.updateOverflowButton((ImageView) args);
            }
        }
    }
}
