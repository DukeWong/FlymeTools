package com.zhixin.flymeTools.test;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import de.robv.android.xposed.XC_MethodHook;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zhixin on 2014/12/20.
 */
public class AppHooKTest extends XC_MethodHook {
    private String packageName;

    public AppHooKTest(String packageName) {
        this.packageName = packageName;
    }

    public void printView(View view, FileWriter fileWriter, String deep) {
        try {
            fileWriter.write("<" + view.getClass().getName() + ">\r\n");
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                int length = viewGroup.getChildCount();
                for (int i = 0; i < length; i++) {
                    View child = viewGroup.getChildAt(i);
                    printView(child, fileWriter, deep + "----");
                }
            }
            fileWriter.write("</" + view.getClass().getName() + ">\r\n");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printView(Activity activity, String path) {
        File file = new File(Environment.getExternalStorageDirectory(), path);
        try {
            FileWriter fileWriter = new FileWriter(file);
            Window window = activity.getWindow();
            printView(window.getDecorView(), fileWriter, null);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {

    }

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        boolean has = (Boolean) param.args[0];
        if (has) {
            Activity activity = (Activity) param.thisObject;
            if (activity.getPackageName().indexOf(packageName) != -1) {
                printView(activity, "data/" + activity.getClass().getName() + ".xml");
            }
        }
    }
}
