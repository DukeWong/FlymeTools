package com.zhixin.flymeTools.hook;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.TypedValue;
import com.zhixin.flymeTools.Util.FileUtil;
import com.zhixin.flymeTools.Util.LogUtil;
import com.zhixin.flymeTools.Util.SmartBarUtils;
import com.zhixin.flymeTools.app.AppListUtil;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
/**
 * Created by ZXW on 2014/12/12.
 */
public class SmartBarColorHook extends XC_MethodHook {
    public static String SMARTBAR_DEFAULT_TYPE = "preference_smartbar_default_type";
    public static String SMARTBAR_TYPE = "preference_smartbar_type";
    public static String SMARTBAR_Color = "preference_smartbar_color";
    public static String SMARTBAR_Change = "preference_replace_smartbar";
    /**
     *
     * @param activity
     * @return
     */
    protected static Drawable getChangeSmartbarDrawable(Activity activity) {
        String packageName = activity.getPackageName();
        XSharedPreferences xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME);
        xSharedPreferences.makeWorldReadable();
        String defaultType = xSharedPreferences.getString(SMARTBAR_DEFAULT_TYPE, null);
        xSharedPreferences = FileUtil.getSharedPreferences(FileUtil.THIS_PACKAGE_NAME, packageName + FileUtil.SETTING);
        xSharedPreferences.makeWorldReadable();
        boolean isSysApp= AppListUtil.isSystemApp(activity);
        boolean change = xSharedPreferences.getBoolean(SMARTBAR_Change, !isSysApp);
        LogUtil.log(activity.getPackageName() + ":改变颜色->" + (change?"是":"否"));
        if (change) {
            String smartbar_type = xSharedPreferences.getString(SMARTBAR_TYPE, defaultType);

            if (smartbar_type != null) {
                //自动设置等
                if (smartbar_type.indexOf("#") == -1) {
                    //1为默认设置
                    String smartbar_color="#FFFFFFFF";
                    smartbar_type = smartbar_type.equals("1") ? defaultType : smartbar_type;
                    if (smartbar_type.equals("0")) {
                        LogUtil.log( activity.getPackageName() + ":颜色->监测颜色");
                          return  getSmartBarDrawable(activity);
                    } else {
                        if (smartbar_type.equals("-1")) {
                            smartbar_color=xSharedPreferences.getString(SMARTBAR_Color,smartbar_color);
                            int  color=Color.parseColor(smartbar_color);
                            LogUtil.log(activity.getPackageName() + ":自定义颜色->"+smartbar_color);
                            return  new ColorDrawable(color);
                        }
                    }
                }
                else
                {
                    LogUtil.log(activity.getPackageName() + ":默认颜色->"+smartbar_type);
                    int  color=Color.parseColor(smartbar_type);
                    return  new ColorDrawable(color);
                }
            }
        }
        return null;
    }

    /**
     * 获取ActionBar的背景
     * @param context
     * @return
     */
    public static Drawable getActionBarBackground(Context context)
    {
        int[] android_styleable_ActionBar = { android.R.attr.background };
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.actionBarStyle, outValue, true);
        TypedArray abStyle = context.getTheme().obtainStyledAttributes(outValue.resourceId, android_styleable_ActionBar);
        try
        {
            return abStyle.getDrawable(0);
        }
        finally
        {
            abStyle.recycle();
        }
    }

    /**
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable){
            return ((BitmapDrawable)drawable).getBitmap() ;
        }else if(drawable instanceof NinePatchDrawable){
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
        }else{
            return null ;
        }
    }

    /**
     *
     * @param activity
     * @return
     */
    public static Drawable getSmartBarDrawable(Activity activity) {
        Drawable bg= getActionBarBackground(activity);
        if(bg instanceof NinePatchDrawable){
            Bitmap bitmap= drawable2Bitmap(bg);
            int color=bitmap.getPixel(bitmap.getWidth()/2,bitmap.getHeight()/2);
            XposedBridge.log("ZX:" + activity.getPackageName() + ":Color->"+ color);
            ColorDrawable colorDrawable=new ColorDrawable();
            return  colorDrawable;
        }
        return  bg;
    }

    /**
     *
     * @param param
     * @throws Throwable
     */
    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        Activity thisActivity = (Activity) param.thisObject;
        Drawable drawable = getChangeSmartbarDrawable(thisActivity);
        if (drawable!=null) {
            SmartBarUtils.changeSmartBarColor(thisActivity, drawable);
        }
    }
}
