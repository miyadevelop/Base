package com.my.ui;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.FontHelper;
import com.lf.view.tools.QMUIStatusBarHelper;
import com.my.app.R;
import com.umeng.message.PushAgent;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //将状态栏文字颜色改为黑色
        QMUIStatusBarHelper.setStatusBarLightMode(this);
        PushAgent.getInstance(this).onAppStart();//友盟推送
    }


    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(null != toolbar) {
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        FontHelper.applyFont(this, findViewById(android.R.id.content), FontHelper.APP_FONT);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }
                else
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onPause() {
        super.onPause();
        DataCollect.getInstance(this).onPause(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DataCollect.getInstance(this).onResume(this);
    }

}
