package com.zhixin.flymeTools.controls;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhixin.flymeTools.R;

/**
 * Created by ZXW on 2014/12/5.
 */
public class ImageMenuBtn extends ActivityLinkItem {
    private ImageView imageView;
    private TextView textView;
    protected  void startActivity(Intent intent){
        intent.putExtra("title", textView.getText().toString());
        super.startActivity(intent);
    }
    protected void  onCreate(Context context,LayoutInflater inflater){
        inflater.inflate(R.layout.image_btn, this);
        imageView = (ImageView) findViewById(R.id.imageView);
        textView = (TextView) findViewById(R.id.textView);
        imageView.setBackground(this.getImage());
        textView.setText(this.getTitle());
    }
    public ImageMenuBtn(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
