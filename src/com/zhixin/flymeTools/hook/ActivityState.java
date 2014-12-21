package com.zhixin.flymeTools.hook;

/**
 * Created by zhixin on 2014/12/21.
 */
public class ActivityState {
    /**
     * 是否已经修改颜色
     */
    public  boolean IS_CHANGE_COLOR=false;
    /**
     * 是否已经更新颜色
     */
    public  boolean IS_UPDATE_COLOR=false;
    /**
     * 窗口是否得到焦点
     */
    public  boolean IS_WINDOW_FOCUS=false;
    /**
     * 是否全屏程序
     */
    public boolean IS_IN_FULL_SCREEN=false;
    /**
     * 必须重新执行
     */
    public boolean IS_MUST_CHANGE=false;

    public  boolean IS_DELAY_UPDATE_PADDING=false;
}
