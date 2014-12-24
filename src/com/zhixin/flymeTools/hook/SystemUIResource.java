package com.zhixin.flymeTools.hook;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

/**
 * Created by zhixin on 2014/12/24.
 */
public class SystemUIResource implements IPackageResources {
    @Override
    public void initReplace(XC_InitPackageResources.InitPackageResourcesParam resparam, XModuleResources mResources) {
        resparam.res.setReplacement("com.android.systemui", "drawable", "status_bar_background", new XResources.DrawableLoader() {
            @Override
            public Drawable newDrawable(XResources res, int id) throws Throwable {
                return new ColorDrawable(Color.TRANSPARENT);
            }
        });
    }
}
