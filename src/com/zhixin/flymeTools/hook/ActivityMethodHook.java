package com.zhixin.flymeTools.hook;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import com.zhixin.flymeTools.Util.AppUtil;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.StringUtil;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by zhixin on 2014/12/19.
 */
public class ActivityMethodHook {
    public interface IDoMethodHook {
        void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook);
    }

    public static void doMethodHookCallBack(Activity activity, View statusBarWindow, Resources resources, XC_MethodHook.MethodHookParam param, IDoMethodHook callBack) {
        if (!StringUtil.equals(activity.getPackageName(), FileUtil.THIS_PACKAGE_NAME)) {
            if (!AppUtil.isSystemApp(activity)) {
                ObjectHook hook = ObjectHook.getObjectHook(activity);
                if (hook == null) {
                    hook = new ActivityColorHook(activity, resources,statusBarWindow);
                }
                if (hook instanceof ActivityColorHook) {
                    callBack.doMethodHook(param, (Activity) param.thisObject, (ActivityColorHook) hook);
                }
            }
        }
    }

    public static class TouchEventMethod extends XC_MethodHook implements IDoMethodHook {
        private Resources mResources;
        private  View statusBarWindow;
        public TouchEventMethod(final  Resources mResources,final View statusBarWindow) {
            this.mResources = mResources;
            this.statusBarWindow=statusBarWindow;
        }
        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject,statusBarWindow,  mResources, param, this);
        }
        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            if (activityColorHook.isTouchGetColor()) {
                activityColorHook.updateStatusBarColor();
            }
        }
    }

    public static class WindowFocusMethod extends XC_MethodHook implements IDoMethodHook {
        private Resources mResources;


        private View statusBarWindow;

        public WindowFocusMethod(final Resources mResources, View statusBarWindow) {
            this.mResources = mResources;
            this.statusBarWindow = statusBarWindow;
        }

        @Override
        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
            doMethodHookCallBack((Activity) param.thisObject,statusBarWindow, mResources, param, this);
        }

        @Override
        public void doMethodHook(XC_MethodHook.MethodHookParam param, Activity thisObject, ActivityColorHook activityColorHook) {
            boolean hasFocus = (Boolean) param.args[0];
            if (hasFocus) {
                activityColorHook.updateSmartbarColor();
                activityColorHook.updateStatusBarLit();
                activityColorHook.showNotification();
            }else {
                activityColorHook.updateStatusBarColor();
            }
        }
    }
}
