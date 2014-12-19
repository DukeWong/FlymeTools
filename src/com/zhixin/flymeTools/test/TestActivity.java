package com.zhixin.flymeTools.test;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import com.zhixin.flymeTools.R;
import com.zhixin.flymeTools.Util.ActivityUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ZXW on 2014/12/17.
 */
public class TestActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        ActivityUtil.setStatusBarLit(this);
        ActivityUtil.setDarkBar(this, true);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.d("1",hasFocus?"1":"0");
        File file=new File(Environment.getExternalStorageDirectory(),"Soft/com.qzone.ui.tab.QZoneTabActivity.png");
        Bitmap bitmap = null;
        InputStream in = null;
        try
        {
            in=new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(in);
            int color = bitmap.getPixel(0, ActivityUtil.getStatusBarHeight(this) +2);
            this.getWindow().setBackgroundDrawable(new ColorDrawable(color));
            int a= Color.alpha(color);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {

        }
    }
}
