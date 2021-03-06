package com.zhixin.flymeTools.controls;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.*;
import android.preference.DialogPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.zhixin.flymeTools.Util.ColorUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorPickerPreference extends DialogPreference { // ColorPickerPreference
    // 继承自
    // DialogPreference
    private int mCurrentColor = Color.WHITE;
    private ColorPickerView mCPView;
    private EditText editText;

    public int getColor() {
        return mCurrentColor;
    }

    public void setColor(int color) {
        mCurrentColor = color;
    }

    public String getValue() {
        return ColorUtil.toHexEncoding(mCurrentColor);
    }

    public void setValue(String colorStr) {
        if (colorStr != null) {
            try {
                mCurrentColor = Color.parseColor(colorStr);
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (defaultValue != null) {
            if (defaultValue instanceof Integer) {
                mCurrentColor = ((Integer) defaultValue).intValue();
            }
            if (defaultValue instanceof CharSequence) {
                mCurrentColor = Color.parseColor(defaultValue.toString());
            }
        }
    }

    private static class ColorPickerView extends View { // ColorPickerView 扩展自
        // View 这部分是参考 ApiDemo中的
        // ColorPickerDialog
        private Paint mPaint;
        private Paint mCenterPaint;
        private Paint mHSVPaint;
        private final int[] mColors;
        private int[] mHSVColors;
        private boolean mRedrawHSV;
        private OnColorChangedListener mListener;

        ColorPickerView(Context c, OnColorChangedListener l, int color) {
            super(c);
            mListener = l;
            mColors = new int[]{0xFFFF0000, 0xFFFF00FF, 0xFF0000FF,
                    0xFF00FFFF, 0xFF00FF00, 0xFFFFFF00, 0xFFFF0000};
            Shader s = new SweepGradient(0, 0, mColors, null);
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 定义主调色面板
            mPaint.setShader(s);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(100); // 主调色面板的 宽度
            mCenterPaint = new Paint(Paint.ANTI_ALIAS_FLAG); // 定义中间的显示颜色面板
            // 主要是在主面板中选择的颜色反应到
            // 显示颜色面板
            mCenterPaint.setColor(color);
            mCenterPaint.setStrokeWidth(5); // 中间定义面板的半径
            mHSVColors = new int[]{0xFF000000, color, 0xFFFFFFFF};
            // 定义细条的bar 颜色从 黑色过渡主面板选中的颜色再过渡到白色 主要解决 没有白色和黑色的问题
            mHSVPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mHSVPaint.setStrokeWidth(20); // 设置Bar的宽度
            mRedrawHSV = true;
        }

        private boolean mTrackingCenter;
        private boolean mHighlightCenter;

        public int getColor() {
            return mCenterPaint.getColor();
        }

        public void setColor(int color) {
            mCenterPaint.setColor(color);
            mRedrawHSV = false;
            invalidate();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float r = CENTER_X - mPaint.getStrokeWidth() * 0.5f;
            canvas.translate(CENTER_X, CENTER_Y);
            int c = mCenterPaint.getColor();
            if (mRedrawHSV) {
                mHSVColors[1] = c;
                mHSVPaint.setShader(new LinearGradient(-100, 0, 100, 0,
                        mHSVColors, null, Shader.TileMode.CLAMP));
            }
            canvas.drawOval(new RectF(-r, -r, r, r), mPaint); // 绘制 主调色版
            canvas.drawCircle(0, 0, CENTER_RADIUS, mCenterPaint); // 绘制 中间显示颜色面板
            r = r * 1.8f;
            canvas.drawRect(new RectF(-r, r, r, r + 30), mHSVPaint); // 绘制
            // 细调的BAR
            if (mTrackingCenter) {
                mCenterPaint.setStyle(Paint.Style.STROKE);
                if (mHighlightCenter) {
                    mCenterPaint.setAlpha(0xFF);
                } else {
                    mCenterPaint.setAlpha(0x80);
                }
                canvas.drawCircle(0, 0, CENTER_RADIUS
                        + mCenterPaint.getStrokeWidth(), mCenterPaint);
                mCenterPaint.setStyle(Paint.Style.FILL);
                mCenterPaint.setColor(c);
            }
            mRedrawHSV = true;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(CENTER_X * 2, (CENTER_Y + 60) * 2);
        }

        private static final int CENTER_X = 160;
        private static final int CENTER_Y = 160;
        private static final int CENTER_RADIUS = 30;

        private int ave(int s, int d, float p) {
            return s + java.lang.Math.round(p * (d - s));
        }

        private int interpColor(int colors[], float unit) {
            if (unit <= 0) {
                return colors[0];
            }
            if (unit >= 1) {
                return colors[colors.length - 1];
            }
            float p = unit * (colors.length - 1);
            int i = (int) p;
            p -= i;
            // now p is just the fractional part [0...1) and i is the index
            int c0 = colors[i];
            int c1 = colors[i + 1];
            int a = ave(Color.alpha(c0), Color.alpha(c1), p);
            int r = ave(Color.red(c0), Color.red(c1), p);
            int g = ave(Color.green(c0), Color.green(c1), p);
            int b = ave(Color.blue(c0), Color.blue(c1), p);
            return Color.argb(a, r, g, b);
        }

        private static final float PI = 3.1415926f;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX() - CENTER_X;
            float y = event.getY() - CENTER_Y;
            boolean inCenter = java.lang.Math.sqrt(x * x + y * y) <= CENTER_RADIUS;
            float d = (CENTER_X - mPaint.getStrokeWidth() * 0.5f) * 1.8f;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mTrackingCenter = inCenter;
                    if (inCenter) {
                        mHighlightCenter = true;
                        invalidate();
                        break;
                    }
                case MotionEvent.ACTION_MOVE:
                    if (mTrackingCenter) {
                        if (mHighlightCenter != inCenter) {
                            mHighlightCenter = inCenter;
                            invalidate();
                        }
                    } else if (y >= d) {
                        int a, r, g, b, c0, c1;
                        float p;
                        x = x < -d ? -d : x;
                        x = x > d ? d : x;
                        if (x < 0) {
                            c0 = mHSVColors[0];
                            c1 = mHSVColors[1];
                            p = (x + d) / d;
                        } else {
                            c0 = mHSVColors[1];
                            c1 = mHSVColors[2];
                            p = x / d;
                        }
                        a = ave(Color.alpha(c0), Color.alpha(c1), p);
                        r = ave(Color.red(c0), Color.red(c1), p);
                        g = ave(Color.green(c0), Color.green(c1), p);
                        b = ave(Color.blue(c0), Color.blue(c1), p);
                        // 把细调颜色设置到显示面板中
                        mCenterPaint.setColor(Color.argb(a, r, g, b));
                        mListener.colorChanged(mCenterPaint.getColor(), false);
                        mRedrawHSV = false;
                        invalidate();
                    } else {
                        if (y < d) {
                            float angle = (float) java.lang.Math.atan2(y, x);
                            // need to turn angle [-PI ... PI] into unit [0....1]
                            float unit = angle / (2 * PI);
                            if (unit < 0) {
                                unit += 1;
                            }
                            mCenterPaint.setColor(interpColor(mColors, unit));
                            mListener.colorChanged(mCenterPaint.getColor(), false);
                            invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (mTrackingCenter) {
                        if (inCenter) {
                            mListener.colorChanged(mCenterPaint.getColor(), true);
                        }
                        mTrackingCenter = false;
                        invalidate();
                    }
                    break;
            }
            return true;
        }
    }

    public interface OnColorChangedListener {
        void colorChanged(int color, boolean close);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorPickerPreference(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            // 保存设置好的颜色
            mCurrentColor = mCPView.getColor();
            String color = ColorUtil.toHexEncoding(mCurrentColor);
            if (callChangeListener(color)) {
                SharedPreferences.Editor editor = getEditor();
                editor.putString(getKey(), color);
                editor.commit();
            }
        }
    }

    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        OnColorChangedListener l = new OnColorChangedListener() {
            public void colorChanged(int color, boolean close) {
                mCurrentColor = color;
                editText.setEnabled(true);
                editText.setText(ColorUtil.toHexEncoding(mCurrentColor));
                if (close) {
                    onDialogClosed(true);
                    getDialog().dismiss();
                }
            }
        };
        SharedPreferences prefs = getPreferenceManager().getSharedPreferences();
        String color = prefs.getString(getKey(), null);
        if (color != null) {
            mCurrentColor = Color.parseColor(color);
        }
        // 主要是设置 调色板的布局
        LinearLayout layout = new LinearLayout(getContext());
        layout.setPadding(10, 10, 10, 10);
        layout.setOrientation(LinearLayout.VERTICAL);
        mCPView = new ColorPickerView(getContext(), l, mCurrentColor);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.gravity = Gravity.CENTER;
        mCPView.setLayoutParams(params1);
        layout.addView(this.mCPView);
        editText = new EditText(getContext());
        editText.setLayoutParams(params1);
        editText.setEnabled(false);
        editText.setText(ColorUtil.toHexEncoding(mCurrentColor));
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.indexOf('#') == -1) {
                    str = "#" + str;
                }
                Pattern pattern = Pattern.compile("#[0-9a-fA-F]{8}");
                Matcher matcher = pattern.matcher(str);
                if (matcher.matches()) {
                    str = str.toUpperCase();
                    mCurrentColor = Color.parseColor(str);
                    mCPView.setColor(mCurrentColor);
                }
            }
        });
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                editText.setText(ColorUtil.toHexEncoding(mCPView.getColor()));
            }
        });
        layout.addView(this.editText);
        layout.setId(android.R.id.widget_frame);
        builder.setView(layout);
    }
}