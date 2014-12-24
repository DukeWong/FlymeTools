package com.zhixin.flymeTools.controls;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import com.zhixin.flymeTools.Util.LogUtil;

/**
 * Created by ZXW on 2014/12/24.
 */
public class LitControlLayout extends LinearLayout {
    private int usableHeightPrevious;
    private int statusBarHeight;
    private View contextView;

    public LitControlLayout(Context context, int statusBarHeight, View contextView) {
        super(context);
        this.statusBarHeight = statusBarHeight;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LogUtil.log("LitControlLayout->top:" + contextView.getTop());
        LogUtil.log("LitControlLayout->Y:" + contextView.getY());
        this.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                possiblyResizeChildOfContent();
            }
        });
        this.setLayoutParams(layoutParams);
        this.setFocusable(false);
        this.setFocusableInTouchMode(false);
        this.setPadding(0, contextView.getTop() > 0 ? 0 : statusBarHeight, 0, 0);
        this.requestLayout();
    }

    private void possiblyResizeChildOfContent() {
        int usableHeightNow = computeUsableHeight();
        if (usableHeightNow != usableHeightPrevious) {
            usableHeightPrevious = usableHeightNow;
            int usableHeightSansKeyboard = this.getRootView().getHeight();
            int heightDifference = usableHeightSansKeyboard - usableHeightNow;
            if (heightDifference > (usableHeightSansKeyboard / 4)) {
                int bottom = this.getHeight() - usableHeightPrevious - this.statusBarHeight;
                this.setPadding(0, this.statusBarHeight, 0, bottom > 0 ? bottom : 0);
                LogUtil.log("键盘被改变高度" + bottom);
            } else {
                this.setPadding(0, this.statusBarHeight, 0, 0);
            }
            this.requestLayout();
        }
    }

    private int computeUsableHeight() {
        Rect r = new Rect();
        this.getWindowVisibleDisplayFrame(r);
        return (r.bottom - r.top);
    }
}
