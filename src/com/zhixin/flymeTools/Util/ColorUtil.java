package com.zhixin.flymeTools.Util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

/**
 * Created by ZXW on 2014/12/12.
 */
public class ColorUtil {
    public static String toHexEncoding(int color) {
        String R, G, B, A;
        StringBuffer sb = new StringBuffer();
        R = Integer.toHexString(Color.red(color));
        G = Integer.toHexString(Color.green(color));
        B = Integer.toHexString(Color.blue(color));
        A = Integer.toHexString(Color.alpha(color));
        R = R.length() == 1 ? "0" + R : R;
        G = G.length() == 1 ? "0" + G : G;
        B = B.length() == 1 ? "0" + B : B;
        A = B.length() == 1 ? "0" + A : A;
        sb.append("#");
        sb.append(A.toUpperCase());
        sb.append(R.toUpperCase());
        sb.append(G.toUpperCase());
        sb.append(B.toUpperCase());
        return sb.toString();
    }
    public static Bitmap loadBitmapFromView(View v) {
        if (v == null) {return null; }
        v.buildDrawingCache();
        Bitmap bitmap = v.getDrawingCache();
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(bitmap);
            c.translate(-v.getScrollX(), -v.getScrollY());
            v.draw(c);
        }
        return bitmap;
    }
    public static boolean TestColorOfWhite(int color, int faultTolerant) {
        boolean isWhite = Color.blue(color) >= 255 - faultTolerant;
        isWhite = isWhite && (Color.red(color) >= 255 - faultTolerant);
        isWhite = isWhite && (Color.green(color) >= 255 - faultTolerant);
        return isWhite;
    }
}
