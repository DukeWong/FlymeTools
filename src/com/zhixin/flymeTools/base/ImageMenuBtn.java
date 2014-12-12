package com.zhixin.flymeTools.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhixin.flymeTools.R;

/**
 * Created by ZXW on 2014/12/5.
 */
public class ImageMenuBtn extends LinearLayout {
    private ImageView imageView;
    private TextView textView;
    private  Class<? extends Activity>  activity=null;
    private Context thisContext=null;
    public void startActivity(){
        if (activity!=null){
            Intent intent=new Intent();
            intent.setClass(thisContext, activity);
            intent.putExtra("title",textView.getText().toString());
            thisContext.startActivity(intent);
        }
    }
    public ImageMenuBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
        thisContext=context;
        // TODO Auto-generated constructor stub
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.imagebtn, this);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ImageMenuBtn);
        imageView.setBackground(typedArray.getDrawable(R.styleable.ImageMenuBtn_image));
        textView.setText(typedArray.getText(R.styleable.ImageMenuBtn_title));
        String activityUrl=typedArray.getString(R.styleable.ImageMenuBtn_url);
        String packageName= context.getPackageName()+".";
        try {
            Class<?> aClass= Class.forName(packageName+activityUrl);
            activity=aClass.asSubclass(Activity.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            activity=null;
        }
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
        /**
         * 重写这个方法，返回true就行了
         */

    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub

        return true;
    }
}
