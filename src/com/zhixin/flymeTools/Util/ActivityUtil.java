package com.zhixin.flymeTools.Util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zhixin.flymeTools.hook.StatusBarHook;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by ZXW on 2014/12/15.
 */
public class ActivityUtil {
    public static int STATUS_BAR_HEIGHT = 0;
    public static int ACTION_BAR_HEIGHT = 0;
    public static int NAVIGATION_BAR_HEIGHT = 96;

    public static boolean setStatusBarLit(Activity context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            return true;
        }
        return false;
    }
    /**
     * 获取ActionBar的背景
     *
     * @param context
     * @return
     */
    public static Drawable getActionBarBackground(Context context) {
        int[] android_styleable_ActionBar = {android.R.attr.background};
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId, android_styleable_ActionBar);
        try {
            return abStyle.getDrawable(0);
        } finally {
            abStyle.recycle();
        }
    }

    /**
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof NinePatchDrawable) {
            Bitmap bitmap = Bitmap
                    .createBitmap(
                            drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                    : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                    drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }
    public static Drawable getSmartBarDrawable(Activity activity) {
        Drawable bg = getActionBarBackground(activity);
        if (bg instanceof NinePatchDrawable) {
            Bitmap bitmap = drawable2Bitmap(bg);
            int color = bitmap.getPixel(bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            ColorDrawable colorDrawable = new ColorDrawable(color);
            return colorDrawable;
        }
        return bg;
    }

    /**
     * 设置内容视图高度问题
     *
     * @param activity
     */
    public static int changeContextViewPadding(Activity activity, boolean hasStatusBar,boolean hasSmartBar,boolean hasActionBar) {
        int top = 0,actionHeight=0, bottom = 0;
        if (hasStatusBar) {
            top += ActivityUtil.getStatusBarHeight(activity);
        }
        ActionBar actionBar = activity.getActionBar();
        if (actionBar != null) {
            Object mActionView = ReflectionUtil.getObjectField(actionBar, "mActionView");
            Object mSplitView = ReflectionUtil.getObjectField(actionBar, "mSplitView");
            if (mActionView != null) {
                actionHeight= ((View) mActionView).getHeight();
                top+=actionHeight;
            }
            if (mSplitView != null) {
                bottom += ((View) mSplitView).getHeight();
            }
        }
        if (hasActionBar && actionHeight==0){
            top+=ActivityUtil.getActionBarHeight(activity);
        }
        boolean isKikit = ActivityUtil.setStatusBarLit(activity);
        if (isKikit) {
            View decorView=activity.getWindow().getDecorView();
            View contentView=decorView.findViewById(android.R.id.content);
            top=contentView.getTop()==0?top:contentView.getPaddingTop();
            contentView.setPadding(0, top, 0, hasSmartBar?bottom:0);
            LogUtil.log(activity.getClass().getName() + " 顶部:" + top);
            LogUtil.log(activity.getClass().getName() + " 底部:" + (hasSmartBar?bottom:0));
        }
        return top;
    }
    public static boolean existFlag(Activity activity, int flags) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return attrs.flags == ((attrs.flags & ~flags) | (flags & flags));
    }
    public static int getNavigationBarHeight(Activity activity) {
        return NAVIGATION_BAR_HEIGHT;
    }

    /**
     * 获取状态栏高度+ActionBar高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarAndActionBarHeight(Activity activity) {
        return getStatusBarHeight(activity) + getActionBarHeight(activity);
    }

    /**
     * 获取手机状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStatusBarHeight(Activity activity) {
        if (STATUS_BAR_HEIGHT == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object obj = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = Integer.parseInt(field.get(obj).toString());
                STATUS_BAR_HEIGHT = activity.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        return STATUS_BAR_HEIGHT;
    }
    public static void savePic(Bitmap b, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public  static  Integer getStatusBarColor(Activity activity){
        View decorView = activity.getWindow().getDecorView();
        Bitmap bitmap = ColorUtil.loadBitmapFromView(decorView);
        if (bitmap != null) {
            //保存截图
            //File file=new File(Environment.getExternalStorageDirectory(),"Pictures/"+activity.getClass().getName()+".png");
            //savePic(bitmap,file);
            int color = bitmap.getPixel(bitmap.getWidth() / 2, ActivityUtil.getStatusBarHeight(activity) +2);
            return color;
        }
        return null;
    }
    /**
     * 获取ActionBar的高度
     *
     * @param activity
     * @return
     */
    public static int getActionBarHeight(Activity activity) {
        if (ACTION_BAR_HEIGHT == 0) {
            TypedValue tv = new TypedValue();
            if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {// 如果资源是存在的、有效的
                ACTION_BAR_HEIGHT = TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics());
            }
        }
        return ACTION_BAR_HEIGHT;
    }

    /**
     * 设置出现SmartBar
     *
     * @param activity
     */
    public static void setSmartBarEnable(Activity activity) {
        final ActionBar bar = activity.getActionBar();
        SmartBarUtils.setActionBarViewCollapsable(bar, true);
        bar.setDisplayOptions(0);
    }

    /**
     * 设置状态栏为黑色图标
     *
     * @param activity
     * @param on
     */
    public static void setDarkBar(Activity activity, boolean on) {
        try {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            Field f = winParams.getClass().getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            f.setAccessible(true);
            int bits = f.getInt(winParams);
            Field f2 = winParams.getClass().getDeclaredField("meizuFlags");
            f2.setAccessible(true);
            int meizuFlags = f2.getInt(winParams);
            if (on) {
                meizuFlags |= bits;
            } else {
            }
            f2.setInt(winParams, meizuFlags);
            win.setAttributes(winParams);
            return;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
