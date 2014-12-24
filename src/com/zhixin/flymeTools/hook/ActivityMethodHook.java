package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.res.Resources;
import com.zhixin.flymeTools.Util.AppUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.StringUtil;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by zhixin on 2014/12/19.
 */
public class ActivityMethodHook {
    public interface IDoMethodHook {
        void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook, Object args);
    }

    public static void doMethodHookCallBack(Activity activity, Resources resources, XC_MethodHook.MethodHookParam param, IDoMethodHook callBack, Object args) {
        if (!StringUtil.equals(activity.getPackageName(), FileUtil.THIS_PACKAGE_NAME)) {
            if (!AppUtil.isSystemApp(activity)) {
                ObjectHook hook = ObjectHook.getObjectHook(activity);
                if (hook == null) {
                    hook = new ActivityColorHook(activity, resources);
                }
                if (hook instanceof ActivityColorHook) {
                    callBack.doMethodHook(param, activity, (ActivityColorHook) hook, args);
                }
            }
        }
    }

    /**
     * 销毁hook函数
     */
    public static class DestroyMethod extends XC_MethodHook {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            ObjectHook.removeObjectHook(param.thisObject);
        }
    }

    /**
     * SetContentView函数Hook
     */
    public static class SetContentViewMethod extends WindowFocusMethod {

        public SetContentViewMethod(Resources mResources) {
            super(mResources);
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        }

        @Override
        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, mResources, param, this, null);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook, Object args) {
            int layoutResID = (Integer) param.args[0];
            activityColorHook.updateLayoutResID(layoutResID);
        }
    }

    public static class WindowFocusMethod extends XC_MethodHook implements IDoMethodHook {
        protected Resources mResources;

        public WindowFocusMethod(final Resources mResources) {
            this.mResources = mResources;
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, mResources, param, this, null);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook, Object args) {
            boolean hasFocus = (Boolean) param.args[0];
            if (hasFocus) {
                activityColorHook.updateStatusBarLit(true);
                activityColorHook.showNotification();
                activityColorHook.updateSmartbarColor();
            } else {
                if (activityColorHook.isTouchGetColor()) {
                    activityColorHook.updateStatusBarColor();
                }
            }
        }
    }
}
