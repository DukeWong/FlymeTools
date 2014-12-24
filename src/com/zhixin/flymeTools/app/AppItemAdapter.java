package com.zhixin.flymeTools.app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhixin.flymeTools.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhixin on 2014/12/11.
 */
public class AppItemAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<AppItem> appItems;

    public AppItemAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        appItems = new ArrayList<AppItem>();
    }

    public void addItem(AppItem item) {
        appItems.add(item);
    }

    @Override
    public int getCount() {
        return appItems.size();
    }

    @Override
    public Object getItem(int position) {
        return appItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.app_item, null);
        }
        bind(position, convertView);
        return convertView;
    }

    public AppItem getAppItem(int position) {
        return (AppItem) getItem(position);
    }

    private void bind(int position, View convertView) {
        ImageView imageView = (ImageView) convertView.findViewById(R.id.app_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.app_name);
        TextView packageName = (TextView) convertView.findViewById(R.id.packgeName);
        AppItem item = appItems.get(position);
        imageView.setImageDrawable(item.getIcon());
        textView.setText(item.getAppName());
        packageName.setText(item.getPackgeName());
        packageName.setTextColor(item.isSysApp() ? Color.RED : Color.BLACK);
        item.setView(convertView);
    }
}
