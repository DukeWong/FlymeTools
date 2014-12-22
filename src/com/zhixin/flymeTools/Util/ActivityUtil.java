package com.zhixin.flymeTools.Util;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.zhixin.flymeTools.R;

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
    public static boolean existFlag(Activity activity, int flags) {
        WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
        return attrs.flags == ((attrs.flags & ~flags) | (flags & flags));
    }

    public static boolean existFlag(WindowManager.LayoutParams attrs, int flags) {
        return attrs.flags == ((attrs.flags & ~flags) | (flags & flags));
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

    public static Integer getStatusBarColor(Activity activity) {
        View decorView = activity.getWindow().getDecorView();
        return ColorUtil.loadBitmapColor(decorView, decorView.getWidth() / 2, ActivityUtil.getStatusBarHeight(activity) + 2);
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
        if (bar!=null){
            SmartBarUtils.setActionBarViewCollapsable(bar, true);
            bar.setDisplayOptions(0);
        }
    }

    /**
     * 显示通知栏消息
     *
     * @param activity
     * @param resources
     */
    public static void showNotification(Activity activity, Resources resources) {
        String packageName = activity.getPackageName();
        String activityName = activity.getClass().getName();
        ComponentName cnActivity = new ComponentName(FileUtil.THIS_PACKAGE_NAME, FileUtil.THIS_PACKAGE_NAME + ".app.ActivitySettingActivity");
        ComponentName cnApp = new ComponentName(FileUtil.THIS_PACKAGE_NAME, FileUtil.THIS_PACKAGE_NAME + ".app.AppSettingActivity");
        Intent activityIntent = new Intent().setComponent(cnActivity);
        Intent appIntent = new Intent().setComponent(cnApp);
        try {
            int color = ActivityUtil.getStatusBarColor(activity);
            ///页面设置
            activityIntent.putExtra("packageName", packageName);
            activityIntent.putExtra("activityName", activityName);
            activityIntent.putExtra("color", color);
            //应用设置
            appIntent.putExtra("packageName", packageName);
            appIntent.putExtra("appName", AppUtil.getApplicationName(activity));
            appIntent.putExtra("color", color);
            //
            PendingIntent activityPendingIntent = PendingIntent.getActivity(activity,activityName.hashCode(),
                    activityIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            PendingIntent appPendingIntent = PendingIntent.getActivity(activity, packageName.hashCode(),
                    appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationManager nm = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder builder = new Notification.Builder(activity);
            builder.setContentText(activityName);
            builder.setContentTitle(packageName);
        /*
        Bitmap bitmap=ColorUtil.ScreenShots(activity,false);
        builder.setLargeIcon(bitmap);
        */
            builder.setSmallIcon(android.R.drawable.sym_def_app_icon);
            builder.setAutoCancel(true);//点击消失
            builder.addAction(android.R.drawable.ic_menu_add,
                    resources.getString(R.string.notification_add_activity), activityPendingIntent);
            builder.addAction(android.R.drawable.ic_menu_add,
                    resources.getString(R.string.notification_add_app), appPendingIntent);
            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            nm.notify(1024,notification);
        } catch (Exception e) {
            return;
        }

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
