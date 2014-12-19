package com.zhixin.flymeTools.hook;

import android.app.Activity;
import com.zhixin.flymeTools.Util.AppUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/12/19.
 */
public class ActivityMethodHook {
    public interface IDoMethodHook {
        void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook);
    }

    public static void doMethodHookCallBack(Activity activity, XC_MethodHook.MethodHookParam param, IDoMethodHook callBack) {
        if (!AppUtil.isSystemApp(activity)) {
            ObjectHook hook = ObjectHook.getObjectHook(activity);
            if (hook == null) {
                hook = new ActivityColorHook(activity);
            }
            if (hook instanceof ActivityColorHook) {
                callBack.doMethodHook(param, (Activity) param.thisObject, (ActivityColorHook) hook);
            }
        }
    }

    public static class TouchEventMethod extends XC_MethodHook implements IDoMethodHook {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, param, this);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            if (activityColorHook.isTouchGetColor()) {
                activityColorHook.updateStatusBarColor();
            }
        }
    }

    public static class DecorViewFocusMethod extends WindowFocusMethod {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            Object thisActivity = XposedHelpers.getObjectField(param.thisObject, "mCallback");
            if (thisActivity != null && thisActivity instanceof Activity) {
                doMethodHookCallBack((Activity) thisActivity, param, this);
            } else {
                LogUtil.log(thisActivity == null ? "thisActivity为空" : thisActivity.getClass().getName());
            }
        }
    }

    public static class WindowFocusMethod extends XC_MethodHook implements IDoMethodHook {
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, param, this);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            boolean hasFocus = (Boolean) param.args[0];
            if (hasFocus) {
                activityColorHook.updateSmartbarColor();
                activityColorHook.updateStatusBarLit();
            }
        }
    }
}
