package com.zhixin.flymeTools.controls;

import android.graphics.*;
import android.graphics.drawable.ColorDrawable;

/**
 * Created by ZXW on 2014/12/22.
 */
public class TableLineDrawable extends ColorDrawable {
    private int strokeWidth = 1;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    private int row;
    private int column;

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public TableLineDrawable(int color, int row, int column) {
        super(color);
        this.row = row;
        this.column = column;
    }

    @Override
    public void draw(Canvas canvas) {
        if (row > 1 && column > 1) {

            Paint paint = new Paint();
            paint.setStrokeWidth(strokeWidth);
            int width = canvas.getWidth();
            int height = canvas.getHeight();
            int space = (height - strokeWidth * (row - 1)) / row;
            int startY = space + 1;
            int startX = 0;
            int stopX = width;
            int colors[] = new int[4];
            colors[0] = colors[3] = Color.WHITE;
            colors[1] = colors[2] = this.getColor();
            float positions[] = new float[4];
            positions[0] = 0.0f;
            positions[1] = 0.4f;
            positions[2] = 0.6f;
            positions[3] = 1.0f;
            for (int i = 0; i < row - 1; i++) {
                LinearGradient gradient = new LinearGradient(startX, startY, stopX, startY, colors, positions, Shader.TileMode.MIRROR);
                paint.setShader(gradient);
                canvas.drawLine(startX, startY, stopX, startY, paint);
                startY += 1 + space;
            }
            space = (width - strokeWidth * (column - 1)) / column;
            startY = 0;
            startX = space + 1;
            int stopY = height;
            for (int i = 0; i < column - 1; i++) {
                LinearGradient gradient = new LinearGradient(startX, startY, startX, stopY, colors, positions, Shader.TileMode.MIRROR);
                paint.setShader(gradient);
                canvas.drawLine(startX, startY, startX, stopY, paint);
                startX += 1 + space;
            }
        }
    }
}
