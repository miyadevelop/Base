package com.my.ui;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.dd.CircularProgressButton;
import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.DialogClickListener;
import com.lf.view.tools.DialogManager;
import com.lf.view.tools.FontHelper;
import com.my.app.R;
import com.my.m.user.User;
import com.my.m.user.UserManager;
import com.my.m.user.VerficationLoader;

import java.util.HashMap;

/**
 * 手机号注册界面(绑定手机号)
 * 如果已经注册过，则提示注册失败，让用户选择登录该账号还是重填手机号，如果没注册过，则注册成功
 * 注册成功后，如果需要，会绑定微信等第三方平台账户
 */
public class UserLoginActivity3 extends BaseTitleActivity implements DialogClickListener {


    private AutoCompleteTextView mPhoneView;
    private EditText mVerView;
    private View mProgressView;
    private VerficationLoader mVerficationLoader;  //获取验证码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_user_login3);

        mPhoneView = (AutoCompleteTextView) findViewById(R.id.phone_edit);

        mVerView = (EditText) findViewById(R.id.ver_edit);
        mProgressView = findViewById(R.id.login_progress);

        mVerficationLoader = new VerficationLoader(App.mContext);

        IntentFilter filter = new IntentFilter();
        filter.addAction(mVerficationLoader.getAction());
        filter.addAction(UserManager.getInstance(this).getRegistAction());
        filter.addAction(UserManager.getInstance(this).getBindAction());
        filter.addAction(UserManager.getInstance(this).getLoginAction());
        filter.addAction(UserManager.getInstance(this).getUserInfoAction());
        registerReceiver(mReceiver, filter);
    }


    String ver;

    /**
     * 尝试登录
     */
    public void attemptLogin(View view) {

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
            focusView.requestFocus();
        } else {
            showProgress(true);
            //执行登录手机号
            mHandler.postDelayed(mRegistRunnable, 1500);
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
            DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "get_verfication");
            mVerficationLoader.getVerfication(phone);
        }
    };
    private Runnable
            mRegistRunnable = new Runnable() {

        @Override
        public void run() {
            //注册前，先判断一下该账号是否已存在
            UserManager.getInstance().loadUserInfoByPhone(phone);
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
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "get_verfication_success");
                    mHandler.postDelayed(mRunnable, 2000);
                    Snackbar.make(findViewById(R.id.root), getString(R.string.login_get_verfication_success), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
                    verficationButton.setProgress(100);
                } else//获取验证码失败
                {
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "get_verfication_failed_" + intent.getStringExtra(NetLoader.MESSAGE));
                    Snackbar.make(findViewById(R.id.root), App.string("login_get_verfication_failed"), Snackbar.LENGTH_LONG).show();
                    CircularProgressButton verficationButton = (CircularProgressButton) findViewById(App.id("get_ver_button"));
                    verficationButton.setProgress(-1);
                    verficationButton.setEnabled(true);
                }
            } else if (action.equals(UserManager.getInstance(context).getRegistAction())) {//绑定（注册手机号）的结果处理

                String code = intent.getStringExtra("obtain");
                if (null != code && !"".equals(code))//bug：这个判断不可靠
                {
                    if (isSuccess) {//注册成功
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "login_success");
                        DataCollect.getInstance(App.mContext).onceEvent(UserLoginActivity3.this, "login_success");

                        String userString = getIntent().getStringExtra("data");
                        //绑定微信或QQ
                        if (!TextUtils.isEmpty(userString)) {
                            User user = new Gson().fromJson(userString, User.class);
                            if (!TextUtils.isEmpty(user.getWechat()))
                                UserManager.getInstance().bindWechat(user.getWechat(), true);
                            else if (!TextUtils.isEmpty(user.getQq()))
                                UserManager.getInstance().bindQQ(user.getQq(), true);
                        } else {
                            finish();
                            goToLoginActivity2();
                        }
                    } else {//注册失败
//                        showProgress(false);
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "login_failed_" + intent.getStringExtra(NetLoader.MESSAGE));
                        Snackbar.make(findViewById(R.id.root), intent.getStringExtra(NetLoader.MESSAGE), Snackbar.LENGTH_LONG).show();

                        String message = intent.getStringExtra(NetLoader.MESSAGE);

                        View view = View.inflate(UserLoginActivity3.this, R.layout.base_layout_dialog_2, null);
                        FontHelper.applyFont(UserLoginActivity3.this, view, FontHelper.APP_FONT);

                        HashMap<Integer, String> idAndContents = new HashMap<>();
                        idAndContents.put(R.id.base_dialog_text_title, getResources().getString(R.string.login_phone_binded_title));
                        idAndContents.put(R.id.base_dialog_text_message, getResources().getString(R.string.login_phone_binded_message));
                        idAndContents.put(R.id.base_dialog_text_button_1, getResources().getString(R.string.login_phone_binded_ok));
                        idAndContents.put(R.id.base_dialog_text_button_2, getResources().getString(R.string.login_phone_binded_cancel));
                        DialogManager.getDialogManager().onShow(UserLoginActivity3.this, view, idAndContents, "user_info", UserLoginActivity3.this);

                    }
                }
            } else if (action.equals(UserManager.getInstance(context).getLoginAction())) {//登录结果处理

                String code = intent.getStringExtra("obtain");
                if (null != code && !"".equals(code))//bug：这个判断不可靠
                {
                    if (isSuccess) {//登录成功
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "login_success");
                        DataCollect.getInstance(App.mContext).onceEvent(UserLoginActivity3.this, "login_success");

                        finish();
                        goToLoginActivity2();
                    } else {//登录失败
                        showProgress(false);
                        DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity3.this, "login", "login_failed_" + intent.getStringExtra(NetLoader.MESSAGE));
                        Snackbar.make(findViewById(R.id.root), intent.getStringExtra(NetLoader.MESSAGE), Snackbar.LENGTH_LONG).show();
                    }
                }
            } else if (action.equals(UserManager.getInstance(context).getBindAction())) {//无论绑定成功与否，都关闭当前界面
                finish();
                goToLoginActivity2();
            } else if (action.equals(UserManager.getInstance(context).getUserInfoAction())) {//获取手机号信息接口

                if (isSuccess) {//该手机号账户已存在，提示用户无法注册

                    View view = View.inflate(UserLoginActivity3.this, R.layout.base_layout_dialog_2, null);

                    HashMap<Integer, String> idAndContents = new HashMap<>();
                    idAndContents.put(R.id.base_dialog_text_title, getResources().getString(R.string.login_phone_binded_title));
                    idAndContents.put(R.id.base_dialog_text_message, getResources().getString(R.string.login_phone_binded_message));
                    idAndContents.put(R.id.base_dialog_text_button_1, getResources().getString(R.string.login_phone_binded_ok));
                    idAndContents.put(R.id.base_dialog_text_button_2, getResources().getString(R.string.login_phone_binded_cancel));
                    DialogManager.getDialogManager().init(UserLoginActivity3.this, view, idAndContents, "user_info", UserLoginActivity3.this).onShow();
                } else {//该手机号账户不存在，注册该账号
                    String message = intent.getStringExtra(NetLoader.MESSAGE);
                    if ("该账号不存在".equals(message))
                        UserManager.getInstance().registByPhone(phone, ver);
                }
            }

        }

    };


    protected void goToLoginActivity2() {
        //如果没有昵称，说明是注册而非登录，进而展示填写昵称的界面
        User user = UserManager.getInstance(App.mContext).getUser();
        if (TextUtils.isEmpty(user.getRealName())) {
            Intent loginIntent = new Intent();
            loginIntent.setClass(UserLoginActivity3.this, UserLoginActivity2.class);
            String userString = getIntent().getStringExtra("data");
            if(!TextUtils.isEmpty(userString))
                loginIntent.putExtra("data", userString);
            startActivity(loginIntent);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mHandler.removeCallbacks(mGetVerRunnable);
        mHandler.removeCallbacks(mRunnable);
    }


    @Override
    public void onDialogItemClick(View view, String dialogId) {

        if (view.getId() == R.id.base_dialog_text_button_1)//登录该手机号
        {
            UserManager.getInstance().loginByPhone(phone, ver);
            DialogManager.getDialogManager().onCancel(this, "user_info");
        } else if (view.getId() == R.id.base_dialog_text_button_2)//重新填写手机号
        {
            showProgress(false);
            mPhoneView.setText("");
            mPhoneView.requestFocus();
            mVerView.setText("");
            DialogManager.getDialogManager().onCancel(this, "user_info");
        }

    }
}

