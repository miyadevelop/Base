package com.my.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lf.app.App;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.view.tools.Bean2View;
import com.lf.view.tools.WaitDialog;
import com.my.app.R;
import com.my.m.user.User;
import com.my.m.user.UserManager;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

public class UserAccountActivity extends BaseTitleActivity implements UMAuthListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_user_account);

        refreshShow();

        //设置过渡动画
        ViewCompat.setTransitionName(findViewById(R.id.icon_url), "transitionPic");

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.getInstance(App.mContext).getUpdateUserAction());
        filter.addAction(UserManager.getInstance(App.mContext).getBindAction());
        registerReceiver(mReceiver, filter);
    }


    private void refreshShow()
    {
        if (UserManager.getInstance().isLogin()) {

            User user = UserManager.getInstance().getUser();

            Bean2View.show(this, user, findViewById(R.id.root));

            if(!TextUtils.isEmpty(user.getWechat()))
                ((TextView)findViewById(R.id.account_text_wechat)).setText(R.string.account_binded);
            else
                ((TextView)findViewById(R.id.account_text_wechat)).setText(R.string.account_unbinded);

            if(!TextUtils.isEmpty(user.getQq()))
                ((TextView)findViewById(R.id.account_text_qq)).setText(R.string.account_binded);
            else
                ((TextView)findViewById(R.id.account_text_qq)).setText(R.string.account_unbinded);


            if(!TextUtils.isEmpty(user.getPhone())) {
                String phone = user.getPhone();
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append(phone.substring(0,3));
                stringBuffer.append("****");
                stringBuffer.append(phone.substring(7, phone.length()));
                ((TextView) findViewById(R.id.account_text_phone)).setText(stringBuffer.toString());
            }
        }
    }


    public void goToUpdate(View view) {
        if (UserLoginActivity.checkLogin(this, "main_click_head"))//已经登录了
        {
            Intent intent = new Intent();
            intent.setClass(this, UserLoginActivity2.class);
            startActivity(intent);
        }
    }


    boolean mClickLogout = false;//双击退出登录的标识

    public void logout(View view) {

        if(!UserManager.getInstance().isLogin())
            return;

        if (mClickLogout) {
            UserManager.getInstance().logout();
            finish();
        }
        else
        {
            mClickLogout = true;
            ((TextView)view.findViewById(R.id.account_text_logout)).setText(getString(R.string.account_double_click));
            ((TextView)view.findViewById(R.id.account_text_logout)).setTextColor(getResources().getColor(R.color.warning_text_1));
            view.setBackgroundColor(getResources().getColor(R.color.warning));
            view.findViewById(R.id.account_image_logout_1).setVisibility(View.VISIBLE);

        }
    }


    private boolean mClickUnbindQQ;//双击解绑QQ的标识
    /**
     * 绑定或者解绑QQ
     * @param view
     */
    public void bindQQ(View view)
    {
        if(!UserManager.getInstance().isLogin())
            return;

        User user = UserManager.getInstance().getUser();
        //QQ未绑定
        if(TextUtils.isEmpty(user.getQq()))
        {
            WaitDialog.show(UserAccountActivity.this, "", true);
            String qq_appId = SoftwareData.getMetaData("qqappid", this);
            String qq_secret = SoftwareData.getMetaData("qqappkey", this);
            PlatformConfig.setQQZone(qq_appId, qq_secret);
            UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.QQ, this);
        }
        else//已绑定
        {
            if(!mClickUnbindQQ) {//提示用户再次点击就会解绑
                mClickUnbindQQ = true;
                view.setVisibility(View.INVISIBLE);
                findViewById(R.id.account_layout_qq_1).setVisibility(View.VISIBLE);
            }
            else//解绑
            {
                UserManager.getInstance().bindQQ(user.getQq(), false);
                WaitDialog.show(this, "", true);
            }
        }
    }



    private boolean mClickUnbindWechat;//双击解绑QQ的标识
    /**
     * 绑定或者解绑Wechat
     * @param view
     */
    public void bindWechat(View view)
    {
        if(!UserManager.getInstance().isLogin())
            return;

        User user = UserManager.getInstance().getUser();
        //Wechat未绑定
        if(TextUtils.isEmpty(user.getWechat()))
        {
            WaitDialog.show(UserAccountActivity.this, "", true);
            String wx_appId = SoftwareData.getMetaData("wxappid", this);
            String wx_secret = SoftwareData.getMetaData("wxsecret", this);
            PlatformConfig.setWeixin(wx_appId, wx_secret);
            UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.WEIXIN, this);
        }
        else//已绑定
        {
            if(!mClickUnbindWechat) {//提示用户再次点击就会解绑
                mClickUnbindWechat = true;
                view.setVisibility(View.INVISIBLE);
                findViewById(R.id.account_layout_wechat_1).setVisibility(View.VISIBLE);
            }
            else//解绑
            {
                UserManager.getInstance().bindWechat(user.getWechat(), false);
                WaitDialog.show(this, "", true);
            }
        }
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);

            if (action.equals(UserManager.getInstance(App.mContext).getUpdateUserAction()))//登录或者更新了用户信息
            {
                if (isSuccess)//登录成功
                {
                    refreshShow();
                }
            }
            else if (action.equals(UserManager.getInstance(App.mContext).getBindAction()))//绑定成功
            {
                mClickUnbindQQ = false;
                findViewById(R.id.account_layout_qq).setVisibility(View.VISIBLE);
                findViewById(R.id.account_layout_qq_1).setVisibility(View.INVISIBLE);

                mClickUnbindWechat = false;
                findViewById(R.id.account_layout_wechat).setVisibility(View.VISIBLE);
                findViewById(R.id.account_layout_wechat_1).setVisibility(View.INVISIBLE);
                WaitDialog.cancel(UserAccountActivity.this);
                if (isSuccess)//绑定成功
                {
                    refreshShow();
                }
                else
                {
                    Toast.makeText(UserAccountActivity.this, intent.getStringExtra(NetLoader.MESSAGE), Toast.LENGTH_SHORT).show();
                }
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        if (i == ACTION_AUTHORIZE) {
            UMShareAPI.get(this).getPlatformInfo(this, share_media, this);
        }
        else if (i == ACTION_GET_PROFILE) {
            if (SHARE_MEDIA.WEIXIN == share_media) {
                String id = map.get("unionid");
                if(TextUtils.isEmpty(id))
                    id = map.get("uid");
                UserManager.getInstance().bindWechat(id, true);
            } else if (SHARE_MEDIA.QQ == share_media) {
                String id = map.get("openid");
                if (TextUtils.isEmpty(id))
                    id = map.get("uid");
                UserManager.getInstance().bindQQ(id, true);
            }
        }
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        Toast.makeText(this, R.string.login_platform_auth_failed, Toast.LENGTH_LONG).show();
        WaitDialog.cancel(this);
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        Toast.makeText(this, R.string.login_platform_auth_cancel, Toast.LENGTH_LONG).show();
        WaitDialog.cancel(this);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode,resultCode,data);
    }


    /**
     * 性别显示
     * Created by wangwei on 17/12/12.
     */
    public static class GenderView extends TextView implements Bean2View.CustomView {


        public GenderView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public void show(Object fieldValue) {
            if (User.GENDER_M.equals(fieldValue)) {
                setText(R.string.login2_sex_boy);
            } else if (User.GENDER_F.equals(fieldValue)) {
                setText(R.string.login2_sex_girl);
            } else
                setText("");
        }
    }
}
