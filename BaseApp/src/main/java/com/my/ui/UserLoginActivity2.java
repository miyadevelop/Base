package com.my.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.lf.app.App;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.tools.datacollect.DataCollect;
import com.lf.view.tools.Bean2View;
import com.lf.view.tools.activity.FileUpLoadActivity;
import com.my.app.R;
import com.my.m.user.User;
import com.my.m.user.UserManager;


/**
 * 完善信息界面
 * 用户设置昵称、头像、性别
 * @author wangwei
 */
@SuppressLint("UseSparseArrays")
public class UserLoginActivity2 extends BaseTitleActivity {

    private String mImageUrl;
    private String mCity;
    private String mProvince;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity_user_login2);

        if (UserManager.getInstance(App.mContext).isLogin()) {
            User user = UserManager.getInstance(App.mContext).getUser();

            if (null != user) {
                //生成一个新的user对象，以便接下来的修改不对当前用户信息产生影响
                String userString = new Gson().toJson(user);
                user = new Gson().fromJson(userString, User.class);

                try {
                    //将从微信获得的用户信息，填入到user中，并显示到界面上
                     userString = getIntent().getStringExtra("data");
                    if(!TextUtils.isEmpty(userString)) {
                        User tempUser = new Gson().fromJson(userString, User.class);
                        if(null != tempUser)
                        {
                            if(TextUtils.isEmpty(user.getRealName()) && !TextUtils.isEmpty(tempUser.getRealName()))
                            {
                                user.setName(tempUser.getRealName());
                            }

                            if(TextUtils.isEmpty(user.getIcon_url()) && !TextUtils.isEmpty(tempUser.getIcon_url()))
                            {
                                user.setIcon_url(tempUser.getIcon_url());
                            }

                            if(TextUtils.isEmpty(user.getGender()) && !TextUtils.isEmpty(tempUser.getGender()))
                            {
                                user.setGender(tempUser.getGender());
                            }

                            if(!TextUtils.isEmpty(tempUser.getProvince()))
                            {
                               mProvince = tempUser.getProvince();
                            }

                            if(!TextUtils.isEmpty(tempUser.getCity()))
                            {
                                mCity = tempUser.getCity();
                            }
                        }
                    }
                }catch (Exception e)
                {

                }

                mImageUrl = user.getIcon_url();

                Bean2View.show(this, user, findViewById(R.id.root));

                //如果昵称为空，这里不用id后六位来填充昵称
                ((TextView)findViewById(R.id.name)).setText(user.getRealName());

                RadioGroup radioGroup = (RadioGroup) findViewById(R.id.login2_radiogroup);
                if (User.GENDER_M.equals(user.getGender())) {
                    radioGroup.check(R.id.login2_radio_button_boy);
                } else if (User.GENDER_F.equals(user.getGender())) {
                    radioGroup.check(R.id.login2_radio_button_girl);
                }

                if(!TextUtils.isEmpty(mImageUrl)) {
                    ImageView imageView = (ImageView) findViewById(App.id("icon_url"));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.getInstance(this).getUpdateUserAction());
        registerReceiver(mReceiver, filter);
    }


    /**
     * 响应 交 钮
     */
    public void commit(View view) {
        EditText nameEdit = (EditText) findViewById(R.id.name);
//        // Reset errors.
//        nameEdit.setError(null);

        // Store values at the time of the login attempt.
        String name = nameEdit.getText().toString();

        // Check for a valid phone address.
        if (TextUtils.isEmpty(name)) {
            nameEdit.setError(getString(R.string.login2_name_required));
            nameEdit.requestFocus();
            return;
        }

        //没填头像
        if (TextUtils.isEmpty(mImageUrl)) {
            Toast.makeText(this, R.string.login2_head_required, Toast.LENGTH_LONG).show();
            return;
        }

        String gender = null;
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.login2_radiogroup);
        if (radioGroup.getCheckedRadioButtonId() == R.id.login2_radio_button_boy) {
            gender = User.GENDER_M;
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.login2_radio_button_girl) {
            gender = User.GENDER_F;
        }

        if (null == gender) {
            Toast.makeText(this, R.string.login2_gender_required, Toast.LENGTH_LONG).show();
            return;
        }


        boolean changed = false;//数据是否发生了变化

        User user = UserManager.getInstance(App.mContext).getUser();
        if (!name.equals(user.getRealName())) {
            changed = true;
        }

        if (!mImageUrl.equals(user.getIcon_url())) {
            changed = true;
        }

        if (!gender.equals(user.getGender())) {
            changed = true;
        }

        //数据没变化
        if (!changed) {
            finish();
            return;
        }

        //提交昵称和头像
        showProgress(true);

        UserManager.getInstance(this).updateUser(name, mImageUrl, gender, null, mProvince, mCity);
    }


    /**
     * 前往选 头像
     */
    public void chooseHead(View view) {
        DataCollect.getInstance(App.mContext).addEvent(this, "perfect_user_info", "click_choose_head");
        Intent intent = new Intent();
        intent.setClass(this, FileUpLoadActivity.class);
        intent.putExtra(FileUpLoadActivity.EXTRA_URL, "http://www.doubiapp.com/mall/upLoadPicture.json");
        intent.putExtra(FileUpLoadActivity.EXTRA_IMAGE_WIGTH, 150);
        intent.putExtra(FileUpLoadActivity.EXTRA_IMAGE_HEIGHT, 150);
        startActivityForResult(intent, FileUpLoadActivity.REQUEST_CODE);
    }


    /**
     *  示或者隐 加载等待的 示
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        final View progressView = findViewById(R.id.login_progress);
        final View loginFormView = findViewById(R.id.login_form);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);

            if (action.equals(UserManager.getInstance(App.mContext).getUpdateUserAction()))//更新了用户信息
            {
                showProgress(false);
                if (isSuccess)//修改信息成功
                {
                    finish();
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity2.this, "perfect_user_info", "perfect_success");
                } else//更新失败
                {
                    DataCollect.getInstance(App.mContext).addEvent(UserLoginActivity2.this, "perfect_user_info", "perfect_failer_" + intent.getStringExtra(NetLoader.MESSAGE));
                    Snackbar.make(findViewById(R.id.root), intent.getStringExtra(NetLoader.MESSAGE), Snackbar.LENGTH_LONG).show();
                }
            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FileUpLoadActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                DataCollect.getInstance(App.mContext).addEvent(this, "perfect_user_info", "upload_head_success");
                mImageUrl = data.getStringExtra(FileUpLoadActivity.RESULT_URL);
                ImageView imageView = (ImageView) findViewById(App.id("icon_url"));
                Glide.with(this).load(mImageUrl).diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            } else {
                DataCollect.getInstance(App.mContext).addEvent(this, "perfect_user_info", "upload_head_failed_" + data.getStringExtra(FileUpLoadActivity.RESULT_URL));
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


//    public void onChangeSex(View view)
//    {
//        ((RadioButton)view).setChecked(true);
//        if(view.getId() == R.id.login2_radio_button_boy)
//        {
//            ((RadioButton)findViewById(R.id.login2_radio_button_girl)).setChecked(false);
//        }
//        else
//        {
//            ((RadioButton)findViewById(R.id.login2_radio_button_boy)).setChecked(false);
//        }
//    }
}
