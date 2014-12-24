package com.zhixin.flymeTools.controls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Window;

/**
 * Created by ZXW on 2014/12/17.
 */
public class StatusBarDrawable extends ColorDrawable {
    public Drawable getBaseDrawable() {
        return baseDrawable;
    }

    public void setBaseDrawable(Drawable baseDrawable) {
        this.baseDrawable = baseDrawable;
    }

    private Drawable baseDrawable;
    private int height = 0;

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public StatusBarDrawable(int color, Drawable drawable, int height) {
        super(color);
        this.height = height;
        this.baseDrawable = drawable;
    }

    @Override
    public void draw(Canvas canvas) {
        if (baseDrawable == null) {
            baseDrawable = new ColorDrawable(Color.WHITE);
        }
        baseDrawable.draw(canvas);
        Paint paint = new Paint();
        paint.setColor(this.getColor());
        paint.setAlpha(this.getAlpha());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, canvas.getWidth(), this.getHeight(), paint);
    }
}
