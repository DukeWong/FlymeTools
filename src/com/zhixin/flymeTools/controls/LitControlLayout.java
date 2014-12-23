package com.zhixin.flymeTools.controls;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import com.zhixin.flymeTools.Util.ActivityUtil;

/**
 * Created by ZXW on 2014/12/23.
 */
public class LitControlLayout extends LinearLayout {
    private boolean mIsControl = false;

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!this.getParent().getClass().getName().equals("com.android.internal.policy.impl.PhoneWindow$DecorView")) {
            mIsControl = true;
            super.onLayout(changed, l, t + ActivityUtil.getStatusBarHeight(this.getContext()), r, b);
        } else {
            super.onLayout(changed, l, t, r, b);
        }
    }

    /**
     * 是否包括调整的控制权，为了兼容以前代码
     *
     * @return
     */
    public boolean isControl() {
        return mIsControl;
    }

    public LitControlLayout(Context context, View contextView) {
        super(context);
        this.addView(contextView);
        if (contextView instanceof FrameLayout) {
            mIsControl = true;
            this.setPadding(0, ActivityUtil.getStatusBarHeight(context), 0, 0);
        }
    }
}
