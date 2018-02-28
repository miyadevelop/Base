package com.my.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.lf.app.App;
import com.lf.controler.tools.SoftwareData;
import com.lf.entry.Entry;
import com.lf.entry.EntryManager;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.activity.BaseWelcomeActivity;
import com.my.app.R;

import java.util.ArrayList;


/**
 * 欢迎界面
 *
 * @author wangwei
 */
public class WelcomeActivity extends BaseWelcomeActivity  {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置跳转时间
        setShortTime(3000);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_activity_welcome);

        //用户打开
        SoftwareData.userOpen(getApplicationContext());

        //响应友盟推送的点击打开某个页面
        Bundle bundle = new Bundle();
        bundle.putString("uri", getIntent().getStringExtra("uri"));
        setGoToClass(MainActivity.class.getName(), bundle);

        //统计新用户
        DataCollect.getInstance(App.mContext).onceEvent(this, "new_user");

        //加载主页入口
        EntryManager.getInstance(App.mContext).load(MyApplication.ENTRY_IDS);

        //注册广播监听
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EntryManager.getInstance(this).getAction());
        registerReceiver(mReceiver, intentFilter);
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


    public BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            String id = intent.getStringExtra("id");
            //加载完开屏后，显示“跳过”按钮
            if (!TextUtils.isEmpty(id) && id.contains("31")) {
                ArrayList<Entry> entries = EntryManager.getInstance(App.mContext).get("31");
                if (entries.size() == 0) {
                    findViewById(R.id.welcome_text_skip).setVisibility(View.GONE);
                } else
                    findViewById(R.id.welcome_text_skip).setVisibility(View.VISIBLE);
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    /**
     * 跳到下一页
     * @param view
     */
    public void skip(View view) {
        goToNext();
    }

}
