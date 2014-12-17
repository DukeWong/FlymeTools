package com.zhixin.flymeTools.hook;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by ZXW on 2014/12/17.
 */
public class StatusBarDrawable extends ColorDrawable {
    public Integer getLastColor() {
        return lastColor;
    }

    public void setLastColor(int lastColor) {
        this.lastColor = lastColor;
    }
    private  Integer lastColor;
    public int getHeight() {
        return height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    private  int height=0;
     public StatusBarDrawable(int color,Integer lastColor, int height){
         super(color);
         this.height=height;
         this.lastColor=lastColor;
     }
    @Override
    public void draw(Canvas canvas) {
        Paint paint=new Paint();
        paint.setColor(this.getColor());
        paint.setAlpha(this.getAlpha());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0,0,canvas.getWidth(), this.getHeight(), paint);
        if (lastColor!=null){
            paint.setColor(lastColor.intValue());
            canvas.drawRect(0,this.getHeight(),canvas.getWidth(), canvas.getHeight(), paint);
        }
    }
}
