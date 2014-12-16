package com.zhixin.flymeTools.Util;

import android.graphics.Color;

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
    public static boolean TestColorOfWhite(int color, int faultTolerant) {
        boolean isWhite = Color.blue(color) >= 255 - faultTolerant;
        isWhite = isWhite && (Color.red(color) >= 255 - faultTolerant);
        isWhite = isWhite && (Color.green(color) >= 255 - faultTolerant);
        return isWhite;
    }
}
