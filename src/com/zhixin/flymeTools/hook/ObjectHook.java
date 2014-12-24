package com.zhixin.flymeTools.hook;

import de.robv.android.xposed.XposedHelpers;

/**
 * Created by ZXW on 2014/12/17.
 */
public class ObjectHook<T> {
    public static String ACTIVITY_HOOK_NAME = "_HOOK_NAME";
    protected T thisObject = null;

    public T getThisObject() {
        return thisObject;
    }

    public void setThisObject(T thisObject) {
        this.thisObject = thisObject;
    }

    public ObjectHook(T thisObject) {
        this.thisObject = thisObject;
        XposedHelpers.setAdditionalInstanceField(thisObject, ACTIVITY_HOOK_NAME, this);
    }

    /**
     * 删除对象
     *
     * @param thisObject
     */
    public static void removeObjectHook(Object thisObject) {
        synchronized (ObjectHook.class) {
            XposedHelpers.removeAdditionalInstanceField(thisObject, ACTIVITY_HOOK_NAME);
        }
    }

    /**
     * 获取对象
     *
     * @param thisObject
     * @return
     */
    public static ObjectHook getObjectHook(Object thisObject) {
        synchronized (ObjectHook.class) {
            Object hook = XposedHelpers.getAdditionalInstanceField(thisObject, ACTIVITY_HOOK_NAME);
            if (hook != null) {
                if (hook instanceof ObjectHook) {
                    return (ObjectHook) hook;
                }
            }
            return null;
        }
    }
}
