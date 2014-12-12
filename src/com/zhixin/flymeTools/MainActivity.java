package com.zhixin.flymeTools;
import android.os.Bundle;
import com.zhixin.flymeTools.base.BaseActivity;

public class MainActivity extends BaseActivity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.setSmartBarEnable();
    }
}
