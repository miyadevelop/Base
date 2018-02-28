package com.my.ui;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import com.lf.view.tools.FontHelper;
import com.my.app.R;

public class BaseTitleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.base_activity_title);

        ViewGroup contentView = (ViewGroup) findViewById(R.id.content);
        contentView.addView(View.inflate(this, layoutResID, null),-1, -1);

        FontHelper.applyFont(this, findViewById(R.id.root), "app_font.ttf");
    }

}
