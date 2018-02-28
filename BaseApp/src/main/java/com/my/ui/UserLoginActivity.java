package com.my.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.WaitDialog;
import com.my.app.R;
import com.my.m.user.User;
import com.my.m.user.UserManager;
import com.my.m.user.VerficationLoader;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * 登录界面
 * 手机号登录、注册+微信QQ登录
 */
public class UserLoginActivity extends BaseTitleActivity implements UMAuthListener {


    // UI references.
    private AutoCompleteTextView mPhoneView;
    private EditText mVerView;


    private VerficationLoader mVerficationLoader;  //获取验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_user_login);

        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone_edit);

        mVerView = (EditText) findViewById(R.id.ver_edit);

        mVerficationLoader = new VerficationLoader(App.mContext);

        IntentFilter filter = new IntentFilter();
        filter.addAction(mVerficationLoader.getAction());
        filter.addAction(UserManager.getInstance(this).getLoginAction());
        filter.addAction(UserManager.getInstance(this).getRegistAndLoginAction());
        registerReceiver(mReceiver, filter);

        DataCollect.getInstance(App.mContext).addEvent(this, "show_login", getIntent().getStringExtra("from"));
        DataCollect.getInstance(App.mContext).addEvent(this, "login", "show_login");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    String ver;

    /**
     * 尝试注册并登录
     */
    public void attemptLogin(View view) {

//        Intent loginIntent = new Intent();
//        loginIntent.setClass(LoginActivity.this, LoginActivity2.class);
//        startActivity(loginIntent);

        CircularProgressButton signInButton = (CircularProgressButton) findViewById(R.id.sign_in_button);
        if (signInButton.getProgress() == 50)
            return;

        // Reset errors.
        mPhoneView.setError(null);
        mVerView.setError(null);

        // Store values at the time of the login attempt.
        phone = mPhoneView.getText().toString();
        ver = mVerView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid ver, if the com.my.shop.com.my.shop.user entered one.
        if (TextUtils.isEmpty(ver) || ver.length() != 4) {
            mVerView.setError(getString(R.string.error_invalid_ver));
            focusView = mVerView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (phone.length() != 11) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the com.my.shop.com.my.shop.user login attempt.
            showProgress(true);
            //执行根据手机号注册并登录
            mHandler.postDelayed(mRALRunnable, 1500);
//            UserManager.getInstance(this).phone(phone, ver);
        }

    }


    /**
     * 显示或者隐藏加载等待的提示
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        CircularProgressButton signInButton = (CircularProgressButton) findViewById(R.id.sign_in_button);
        if (show) {
            signInButton.setIndeterminateProgressMode(true);
            signInButton.setProgress(0);
            signInButton.setProgress(50);
        } else {
            signInButton.setProgress(0);
        }
    }


    String phone;

    /**
     * 点击了获取验证码按钮，前往获取验证码
     */
    public void getVer(View view) {

        CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
        if (verficationButton.getProgress() == 50)
            return;

        if (mVerficationLoader.getLoadingStaus() == NetLoader.EnumLoadingStatus.Loading)
            return;

        // Reset errors.
        mPhoneView.setError(null);

        // Store values at the time of the login attempt.
        phone = mPhoneView.getText().toString();

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            mPhoneView.requestFocus();
            return;
        } else if (phone.length() != 11) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            mPhoneView.requestFocus();
            return;
        }

        verficationButton.setIndeterminateProgressMode(true);
        verficationButton.setProgress(0);
        verficationButton.setProgress(50);
        mHandler.postDelayed(mGetVerRunnable, 1000);
    }


    //bug,退出重进后，将可以重新获取验证码
    private int mCurSecond = 60;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            Button verficationButton = (Button) findViewById(App.id("get_ver_button"));
            if (mCurSecond != 0)//倒计时未到时间
            {
                verficationButton.setText(mCurSecond + App.string("login_verfication_time"));
                mCurSecond--;
                mHandler.postDelayed(mRunnable, 1000);
            } else//到了时间
            {
                verficationButton.setEnabled(true);
                verficationButton.setText(App.string("login_reget_verfication"));
                mCurSecond = 60;
            }
        }
    };

    private Runnable mGetVerRunnable = new Runnable() {

        @Override
        public void run() {
            //获取验证码
            CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
            verficationButton.setEnabled(false);
            DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity.this, "login", "get_verfication");
            mVerficationLoader.getVerfication(phone);
        }
    };
    private Runnable mRALRunnable = new Runnable() {

        @Override
        public void run() {
            //执行登录手机号
            UserManager.getInstance().registAndLoginByPhone(phone, ver);
        }
    };


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);

            if (action.equals(mVerficationLoader.getAction()))//验证码结果
            {
                if (isSuccess)//获取验证码成功
                {
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity.this, "login", "get_verfication_success");
                    mHandler.postDelayed(mRunnable, 2000);
                    Toast.makeText(App.mContext, getString(R.string.login_get_verfication_success), Toast.LENGTH_LONG).show();

                    CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
                    verficationButton.setProgress(100);
                } else//获取验证码失败
                {
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity.this, "login", "get_verfication_failed_" + intent.getStringExtra(NetLoader.MESSAGE));
                    Toast.makeText(App.mContext,App.string("login_get_verfication_failed"), Toast.LENGTH_LONG).show();
                    CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
                    verficationButton.setProgress(-1);
                    verficationButton.setEnabled(true);
                }
            }
            //通过手机号注册并登录
            else if (action.equals(UserManager.getInstance(context).getRegistAndLoginAction())) {
                showProgress(false);
                String code = intent.getStringExtra("obtain");
                if (null != code && !"".equals(code))//bug：这个判断不可靠
                {
                    if (isSuccess) {
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity.this, "login", "login_success");
                        DataCollect.getInstance(App.mContext).onceEvent(UserLoginActivity.this, "login_success");
                        finish();
                        //如果没有昵称，说明是注册而非登录，进而展示填写昵称的界面
                        User user = UserManager.getInstance(App.mContext).getUser();
                        if (TextUtils.isEmpty(user.getRealName())) {
                            Intent loginIntent = new Intent();
                            loginIntent.setClass(UserLoginActivity.this, UserLoginActivity2.class);
                            startActivity(loginIntent);
                        }
                    } else {
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity.this, "login", "login_failed_" + intent.getStringExtra(NetLoader.MESSAGE));
                        Toast.makeText(App.mContext, intent.getStringExtra(NetLoader.MESSAGE), Toast.LENGTH_LONG).show();
                    }
                }
            }
            //通过微信QQ登录
            else if (action.equals(UserManager.getInstance(context).getLoginAction())) {

                WaitDialog.cancel(UserLoginActivity.this);

                if (isSuccess) {
                    finish();
                    //如果没有昵称，说明是注册而非登录，进而展示填写昵称的界面
                    User user = UserManager.getInstance(App.mContext).getUser();
                    if (TextUtils.isEmpty(user.getRealName())) {
                        Intent loginIntent = new Intent();
                        Gson gson = new Gson();
                        if (null != mPlatformAuthUser)
                            loginIntent.putExtra("data", gson.toJson(mPlatformAuthUser));
                        loginIntent.setClass(UserLoginActivity.this, UserLoginActivity2.class);
                        startActivity(loginIntent);
                    }
                } else {//登录失败，则打开手机号注册界面，进行手机号注册，并将要登录的微信QQ信息传递过去

                    WaitDialog.cancel(UserLoginActivity.this);
                    Intent loginIntent1 = new Intent();
                    loginIntent1.setClass(UserLoginActivity.this, UserLoginActivity3.class);

                    Gson gson = new Gson();
                    if (null != mPlatformAuthUser)
                        loginIntent1.putExtra("data", gson.toJson(mPlatformAuthUser));
                    startActivity(loginIntent1);

                    finish();
                }
            }

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mHandler.removeCallbacks(mGetVerRunnable);
        mHandler.removeCallbacks(mRunnable);
    }


    /**
     * 如果未登录，则打开登录界面，并且返回false，
     * 此方法为了简化 多个地方在未登录时 需要先打开登录界面的代码
     *
     * @param from 从哪个界面进入的登录界面
     * @return
     */
    public static boolean checkLogin(Context context, String from) {
        if (UserManager.getInstance().isLogin())
            return true;

//        Intent intent = new Intent();
//        intent.setClass(context, LoginActivity.class);
//        intent.putExtra("from", from);
//        context.startActivity(intent);

        Intent intent = new Intent();
        intent.setData(Uri.parse("my://www.doubiapp.com/login.json?from=" + from));
        context.startActivity(intent);
        return false;
    }


    public void loginWechat(View view) {
        WaitDialog.show(UserLoginActivity.this, "", true);
//        WaitDialog.getDialog(this).findViewById(R.id.wait_progress).setBackgroundColor(getResources().getColor(R.color.main));
        String wx_appId = SoftwareData.getMetaData("wxappid", this);
        String wx_secret = SoftwareData.getMetaData("wxsecret", this);
        PlatformConfig.setWeixin(wx_appId, wx_secret);
        UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.WEIXIN, this);
    }


    public void loginQQ(View view) {
        WaitDialog.show(UserLoginActivity.this, "", true);
//        WaitDialog.getDialog(this).findViewById(R.id.wait_progress).setBackgroundColor(getResources().getColor(R.color.main));
        String qq_appId = SoftwareData.getMetaData("qqappid", this);
        String qq_secret = SoftwareData.getMetaData("qqappkey", this);
        PlatformConfig.setQQZone(qq_appId, qq_secret);
        UMShareAPI.get(this).doOauthVerify(this, SHARE_MEDIA.QQ, this);
    }


    private User mPlatformAuthUser;//从第三方平台授权得到的用户信息，这个信息将会被传给下一个跳转界面，给下一个界面使用

    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {

        if (i == ACTION_AUTHORIZE) {
            UMShareAPI.get(this).getPlatformInfo(this, share_media, this);
//            Toast.makeText(this, map + "", Toast.LENGTH_LONG).show();
////            Toast.makeText(this, "uid" + map.get("uid"), Toast.LENGTH_LONG).show();
////            Log.i("test-ww", "map = " + map);
//            if (SHARE_MEDIA.WEIXIN == share_media) {
//                String id = map.get("unionid");
//                if (TextUtils.isEmpty(id))
//                    id = map.get("uid");
//                UserManager.getInstance().loginByWechat(id);
//                mPlatformAuthUser = new User();
//                mPlatformAuthUser.setWechat(id);
//            } else if (SHARE_MEDIA.QQ == share_media) {
//                UserManager.getInstance().loginByQQ(map.get("uid"));
//                mPlatformAuthUser = new User();
//                mPlatformAuthUser.setQq(map.get("uid"));
//            }
        } else if (i == ACTION_GET_PROFILE) {
            if (SHARE_MEDIA.WEIXIN == share_media) {
                String id = map.get("unionid");
                if (TextUtils.isEmpty(id))
                    id = map.get("uid");
                UserManager.getInstance().loginByWechat(id);
                mPlatformAuthUser = new User();
                mPlatformAuthUser.setWechat(id);
                //昵称
                if (!TextUtils.isEmpty(map.get("screen_name")))
                    mPlatformAuthUser.setName(map.get("screen_name"));
                else if (!TextUtils.isEmpty(map.get("name")))
                    mPlatformAuthUser.setName(map.get("name"));
                //头像
                if (!TextUtils.isEmpty(map.get("profile_image_url")))
                    mPlatformAuthUser.setIcon_url(map.get("profile_image_url"));
                else if (!TextUtils.isEmpty(map.get("iconurl")))
                    mPlatformAuthUser.setIcon_url(map.get("iconurl"));
                //性别
                if ("男".equals(map.get("gender")))
                    mPlatformAuthUser.setGender(User.GENDER_M);
                else if ("女".equals(map.get("gender")))
                    mPlatformAuthUser.setGender(User.GENDER_F);
                //省
                if (!TextUtils.isEmpty(map.get("province")))
                    mPlatformAuthUser.setProvince(map.get("province"));
                //市
                if (!TextUtils.isEmpty(map.get("city")))
                    mPlatformAuthUser.setProvince(map.get("city"));
            } else if (SHARE_MEDIA.QQ == share_media) {
                String id = map.get("openid");
                if (TextUtils.isEmpty(id))
                    id = map.get("uid");
                UserManager.getInstance().loginByQQ(id);
                mPlatformAuthUser = new User();
                mPlatformAuthUser.setQq(map.get("openid"));
                //昵称
                if (!TextUtils.isEmpty(map.get("screen_name")))
                    mPlatformAuthUser.setName(map.get("screen_name"));
                else if (!TextUtils.isEmpty(map.get("name")))
                    mPlatformAuthUser.setName(map.get("name"));
                //头像
                if (!TextUtils.isEmpty(map.get("profile_image_url")))
                    mPlatformAuthUser.setIcon_url(map.get("profile_image_url"));
                else if (!TextUtils.isEmpty(map.get("iconurl")))
                    mPlatformAuthUser.setIcon_url(map.get("iconurl"));
                //性别
                if ("男".equals(map.get("gender")))
                    mPlatformAuthUser.setGender(User.GENDER_M);
                else if ("女".equals(map.get("gender")))
                    mPlatformAuthUser.setGender(User.GENDER_F);
                //省
                if (!TextUtils.isEmpty(map.get("province")))
                    mPlatformAuthUser.setProvince(map.get("province"));
                //市
                if (!TextUtils.isEmpty(map.get("city")))
                    mPlatformAuthUser.setProvince(map.get("city"));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

}

