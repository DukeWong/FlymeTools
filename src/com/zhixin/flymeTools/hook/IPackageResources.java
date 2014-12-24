package com.zhixin.flymeTools.hook;

import android.content.res.XModuleResources;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by zhixin on 2014/12/24.
 */
public interface IPackageResources {
    public void initReplace(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam,XModuleResources mResources);
}
