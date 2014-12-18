package com.zhixin.flymeTools.controls;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.zhixin.flymeTools.R;
/**
 * Created by ZXW on 2014/12/18.
 */
public class ActivityLinkItem extends LinearLayout {
    private  Class<? extends Activity>  mActivity=null;
    protected Context mContext=null;
    private Drawable mImage;
    private String url;
    private CharSequence title;
    private TextView subtitleView;
    private TextView titleView;
    public CharSequence getTitle() {
        return title;
    }
    public void setTitle(CharSequence title) {
        this.title = title;
    }
    public String getUrl() {
        return url;
    }
    public Drawable getImage() {
        return mImage;
    }
    public void setImage(Drawable mImage) {
        this.mImage = mImage;
    }
    public void setUrl(String activityUrl) {
        if (this.url !=activityUrl){
            this.url = activityUrl;
            mActivity=null;
            if (activityUrl!=null && activityUrl!=""){
                if (activityUrl.indexOf(".")==0){
                    activityUrl=mContext.getPackageName()+activityUrl;
                }
                try {
                    Class<?> aClass= Class.forName(activityUrl);
                    mActivity=aClass.asSubclass(Activity.class);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    mActivity=null;
                }
            }
        }
    }
    public void startActivity(){
        if (mActivity!=null){
            Intent intent=new Intent();
            intent.setClass(mContext, mActivity);
            this.startActivity(intent);
        }
    }
    protected  void startActivity(Intent intent){
        mContext.startActivity(intent);
    }
    protected void  onCreate(Context context,LayoutInflater inflater) {
        inflater.inflate(R.layout.activity_link, this);
        titleView = (TextView) findViewById(R.id.title);
        subtitleView=(TextView) findViewById(R.id.subTitle);
        titleView.setText(this.title);
        subtitleView.setText(this.url);
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        return true;
    }
    public ActivityLinkItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext=context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ActivityLinkItem);
        this.mImage=typedArray.getDrawable(R.styleable.ActivityLinkItem_image);
        this.title=typedArray.getText(R.styleable.ActivityLinkItem_title);
        this.setUrl(typedArray.getString(R.styleable.ActivityLinkItem_url));
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onCreate(mContext,inflater);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity();
            }
        });
    }
}
