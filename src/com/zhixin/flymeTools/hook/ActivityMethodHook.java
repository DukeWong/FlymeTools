package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.res.Resources;
import com.zhixin.flymeTools.Util.AppUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import com.zhixin.flymeTools.Util.StringUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by zhixin on 2014/12/19.
 */
public class ActivityMethodHook {
    public interface IDoMethodHook {
        void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook);
    }

    public static void doMethodHookCallBack(Activity activity, Resources resources, XC_MethodHook.MethodHookParam param, IDoMethodHook callBack) {
        if (!StringUtil.equals(activity.getPackageName(),FileUtil.THIS_PACKAGE_NAME)){
            if (!AppUtil.isSystemApp(activity)) {
                ObjectHook hook = ObjectHook.getObjectHook(activity);
                if (hook == null) {
                    hook = new ActivityColorHook(activity, resources);
                }
                if (hook instanceof ActivityColorHook) {
                    callBack.doMethodHook(param, (Activity) param.thisObject, (ActivityColorHook) hook);
                }
            }
        }
    }
    public static class TouchEventMethod extends XC_MethodHook implements IDoMethodHook {
        private Resources mResources;

        public TouchEventMethod(Resources mResources) {
            this.mResources = mResources;
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, mResources, param, this);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            if (activityColorHook.isTouchGetColor()) {
                activityColorHook.updateStatusBarColor();
            }
        }
    }

    public static class DecorViewFocusMethod extends WindowFocusMethod {
        public DecorViewFocusMethod(Resources mResources) {
            super(mResources);
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            Object thisActivity = XposedHelpers.getObjectField(param.thisObject, "mCallback");
            if (thisActivity != null && thisActivity instanceof Activity) {
                doMethodHookCallBack((Activity) thisActivity, this.getmResources(), param, this);
            } else {
                LogUtil.log(thisActivity == null ? "thisActivity为空" : thisActivity.getClass().getName());
            }
        }
    }

    public static class WindowFocusMethod extends XC_MethodHook implements IDoMethodHook {
        public Resources getmResources() {
            return mResources;
        }

        private Resources mResources;

        public WindowFocusMethod(Resources mResources) {
            this.mResources = mResources;
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject, mResources, param, this);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            boolean hasFocus = (Boolean) param.args[0];
            if (hasFocus) {
                activityColorHook.updateSmartbarColor();
                activityColorHook.updateStatusBarLit();
                activityColorHook.showNotification();
            }
        }
    }
}
