package com.zhixin.flymeTools.app;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.TextView;

/**
 * Created by zhixin on 2014/12/11.
 */
public  class AppItem {
    private String mPackgeName;
    private String mAppName;
    private Drawable mIcon;
    private View mView;
    private boolean isSysApp=false;
    public boolean isSysApp() {
        return isSysApp;
    }
    public void setSysApp(boolean isSysApp) {
        this.isSysApp = isSysApp;
    }
    public AppItem(String packgeName, String appName,boolean isSysApp,  Drawable icon) {
        this.mPackgeName = packgeName;
        this.mAppName = appName;
        this.mIcon = icon;
        this.isSysApp=isSysApp;
    }
    public String getPackgeName() {
        return mPackgeName;
    }
    public String getAppName() {
        return mAppName;
    }
    public Drawable getIcon() {
        return mIcon;
    }
    public View getView() {
        return mView;
    }
    public void setView(View mView) {
        this.mView = mView;
    }
    @Override
    public String toString() {
        return mAppName;
    }
}