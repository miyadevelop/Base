package com.my.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.lf.app.App;
import com.lf.controler.tools.Config;
import com.lf.controler.tools.NetWorkManager;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.entry.EntryManager;
import com.lf.tools.datacollect.DataCollect;
import com.lf.tools.share.ShareBean;
import com.lf.tools.share.ShareImage;
import com.lf.tools.share.ShareManager;
import com.lf.view.tools.FontHelper;
import com.lf.view.tools.QMUIStatusBarHelper;
import com.lf.view.tools.update.UpdateManager;
import com.my.app.R;
import com.my.m.im.ImManager;
import com.my.m.user.User;
import com.my.m.user.UserManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 主页
 */
public class MainActivity extends BaseMainActivity implements NetWorkManager.IMobileDataListener, NetWorkManager.IWifiListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        QMUIStatusBarHelper.setStatusBarLightMode(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.getInstance().getLoginAction());
        filter.addAction(UserManager.getInstance().getRegistAndLoginAction());
        filter.addAction(UserManager.getInstance().getRegistAction());
        filter.addAction(UserManager.getInstance().getActionLogout());
        filter.setPriority(1000);
        registerReceiver(mReceiver, filter);

        //以下用来给友盟推送打开指定页面
        try {
            String uri = getIntent().getStringExtra("uri");
            if (null != uri) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
        }

        //首次打开显示登录界面
        if (SoftwareData.getAllOpenRecord(this).size() < 2) {
            UserLoginActivity.checkLogin(this, "first_open");
        }

        //加载主页入口，欢迎界面已经加载过，这里只是防止某些时候不加载
        EntryManager.getInstance(App.mContext).load(MyApplication.ENTRY_IDS);

        //监听网络变化
        NetWorkManager.getInstance(this).addMobileDataListener(this);
        NetWorkManager.getInstance(this).addWifiListener(this);


        List<Page> pages = new ArrayList<>();
        //TODO 添加主页的Fragment
        pages.add(new Page(getString(R.string.main_tab_1), R.drawable.base_image_main_tab_1, new Fragment()));
        pages.add(new Page(getString(R.string.main_tab_2), R.drawable.base_image_main_tab_2, new Fragment()));
        pages.add(new Page(getString(R.string.main_tab_3), R.drawable.base_image_main_tab_3, new MeFragment()));
        setupViewPager(pages);


        FontHelper.applyFont(this, getTabLayout(), FontHelper.APP_FONT);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //以下用来给友盟推送打开指定页面
        try {
            String uri = intent.getStringExtra("uri");
            if (null != uri) {
                Intent intent1 = new Intent();
                intent1.setData(Uri.parse(uri));
                startActivity(intent1);
            }
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        ImManager.getInstance().login();//防止即时通信掉线，在回到App的时候重新登录一下
    }



    //======================================点击交互开始====================================
    /**
     * 邀请好友、分享软件
     *
     * @param view
     */
    public void inviteFriend(View view) {
        DataCollect.getInstance(App.mContext).addEvent(this, "click_share");

        ShareBean bean = new ShareBean();
        String host = Config.getConfig().getString("host", getString(R.string.org_url));
        bean.setUrl(host);
        bean.setImage(new ShareImage(this, R.mipmap.ic_launcher));
        bean.setContent(getString(R.string.me_share_content));
        bean.setTitle(getString(R.string.me_share_title));
        ShareManager.getInstance(this).openShare(this, bean, null);

    }


    /**
     * 反馈
     *
     * @param view
     */
    public void feedback(View view) {
        DataCollect.getInstance(App.mContext).addEvent(this, "click_feedback");
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + "df7SxyNpuALqDu-wbVUC_T5wLK-kgCl7"));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
        } catch (Exception e) {
        }

    }


    /**
     * 检测更新
     *
     * @param view
     */
    public void checkUpdate(View view) {
        DataCollect.getInstance(App.mContext).addEvent(this, "click_update");
        // 检测更新
        if (null == mManager)
            mManager = new UpdateManager(this);
        mManager.checkUpdate(getUpdateDownloadTask(), false);
        Toast.makeText(this, R.string.check_update_check, Toast.LENGTH_SHORT).show();
    }


    /**
     * 显示设置
     *
     * @param view
     */
    public void showSettings(View view) {

    }


    /**
     * 点击头像
     *
     * @param view
     */
    public void onClickHead(View view) {
        if (UserLoginActivity.checkLogin(this, "main_click_head"))//已经登录了
        {
            Intent intent = new Intent();
            intent.setClass(this, UserAccountActivity.class);

            List<Pair<View, String>> pairs = new ArrayList<>();
            pairs.add(Pair.create(view, "transitionPic"));

            ActivityOptionsCompat options = ActivityOptionsCompat
                    .makeSceneTransitionAnimation((Activity) view.getContext(), pairs.toArray(new Pair[pairs.size()]));

            ActivityCompat.startActivity(view.getContext(), intent, options.toBundle());
        }
    }


    /**
     * 前往搜索界面
     *
     * @param view
     */
    public void goToSearch(View view) {
        DataCollect.getInstance(App.mContext).addEvent(this, "show_search");
        Intent intent = new Intent();
        intent.setClass(this, SearchActivity.class);
        startActivity(intent);
    }


    /**
     * 显示关于
     *
     * @param view
     */
    public void showAbout(View view) {
    }

    //======================================点击交互结束====================================

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);
            //登录监听
            if (action.equals(UserManager.getInstance(App.mContext).getLoginAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAndLoginAction()))//登录或者更新了用户信息
            {
                if (isSuccess)//登录成功
                {

                    //更新Entry的userId
                    User user = UserManager.getInstance(App.mContext).getUser();
                    if (null != user)
                        EntryManager.getInstance(App.mContext).setUserId(user.getId());
                }
            }
            //退出登录
            else if (action.equals(UserManager.getInstance().getActionLogout())) {
                EntryManager.getInstance(App.mContext).release();

                Intent intent1 = new Intent();
                intent1.setClass(MainActivity.this, WelcomeActivity.class);
                startActivity(intent1);
                mIsRestart = true;
                finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        if (!mIsRestart)
            EntryManager.getInstance(App.mContext).release();
        NetWorkManager.getInstance(this).removeMobileDataListener(this);
        NetWorkManager.getInstance(this).removeWifiListener(this);
    }

    boolean mIsRestart;//是否是退出登录，重新进入界面

    @Override
    public void onSwitch(boolean isOpen) {
        //这里isOpen的值在手机网的情况下不正确，用isConnect代替
        if (NetWorkManager.getInstance(App.mContext).isConnect()) {
            //加载主页入口，欢迎界面已经加载过，这里只是防止某些时候不加载
            EntryManager.getInstance(App.mContext).load(MyApplication.ENTRY_IDS);
        }
    }

    @Override
    public void onStateChange(int state) {

    }

}

