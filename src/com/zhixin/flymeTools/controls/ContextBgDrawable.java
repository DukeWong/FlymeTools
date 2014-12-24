package com.zhixin.flymeTools.controls;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by zhixin on 2014/12/24.
 */
public class ContextBgDrawable extends StatusBarDrawable {
    public ContextBgDrawable(int color, Drawable drawable, int height) {
        super(color, drawable, height);
    }
    @Override
    public void draw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(this.getColor());
        paint.setAlpha(this.getAlpha());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, this.getHeight(), canvas.getWidth(), canvas.getHeight(), paint);
    }
}
