package com.zhixin.flymeTools.Util;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;

import java.io.ByteArrayOutputStream;

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

    public static Bitmap ScreenShots(Activity activity, boolean full) {
        final TypedArray typedArray = activity.obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int actionBarSize = (int) typedArray.getDimension(0, 0);
        typedArray.recycle();
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int top = rect.top;
        Bitmap bitmap1 = loadBitmapFromView(activity.getWindow().getDecorView());
        if (bitmap1 != null && !full) {
            Bitmap bitmap = Bitmap.createBitmap(bitmap1, 0, top, bitmap1.getWidth(), actionBarSize);
            ByteArrayOutputStream compressedBitmap = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, compressedBitmap);
            return bitmap;
        }
        return bitmap1;
    }

    public static Bitmap loadBitmapFromView(View view) {
        Bitmap bitmap=null;
        if (view != null) {
            view.setDrawingCacheEnabled(true);
            bitmap = view.getDrawingCache();
            if (bitmap == null) {
                if (view.getWidth() > 0 && view.getHeight() > 0) {
                    bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas c = new Canvas(bitmap);
                    c.translate(-view.getScrollX(), -view.getScrollY());
                    view.draw(c);
                }
            }
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
