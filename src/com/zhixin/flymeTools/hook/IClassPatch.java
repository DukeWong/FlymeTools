package com.zhixin.flymeTools.hook;

import android.content.res.Resources;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by zhixin on 2014/12/14.
 */
public interface IClassPatch {
    void initPatch(XC_LoadPackage.LoadPackageParam loadPackageParam, Resources resources);
}
