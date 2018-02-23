package com.my.m.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.lf.app.App;
import com.my.m.R;

public class UserManager {

    private static UserManager mInstance;
    private RegistAndLoginLoader mRALLoader;//注册并登录接口
    private LoginLoader mLLoader;//登录接口
    private LoginAutoLoader mLALoader;//自动登录接口
    private RegistLoader mRLoader;//注册接口
    private BindLoader mBindLoader;//绑定手机号、微信等接口
    private UpdateUserLoader mUpdateUserLoader;//更新用户信息
    private UserInfoLoader mUserInfoLoader;//获取用户信息接口
    private Context mContext;
    //    public static String LOGIN_ACTION = "mall_login";
    private User mUser;//当前登录的用户


    public static UserManager getInstance(Context context) {
        if (null == mInstance)
            mInstance = new UserManager(context);
        return mInstance;
    }


    public static UserManager getInstance() {
        if (null == mInstance)
            mInstance = new UserManager(App.mContext);
        return mInstance;
    }


    private UserManager(Context context) {
        mContext = context.getApplicationContext();
        mUser = new User();
        setHost(mContext.getString(R.string.app_host));//默认host
//        mRALLoader = new RegistAndLoginLoader(mContext);
//        mLoginAutoLoader = new LoginAutoLoader(mContext);
//        mUpdateUserLoader = new UpdateUserLoader(mContext);
    }


    public void setHost(String host) {
        Consts.HOST = host;
    }

    //================注册并登录======================

    /**
     * 通过手机号注册并登录，已有账号则登录，没账号则注册
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void registAndLoginByPhone(String phone, String code) {
        getRALLoader().phone(phone, code);
    }


    /**
     * 通过微信id注册并登录，已有账号则登录，没账号则注册
     *
     * @param wechatId 微信平台的id
     */
    public void registAndLoginByWechat(String wechatId) {
        getRALLoader().wechat(wechatId);
    }


    /**
     * 通过qq的id注册并登录，已有账号则登录，没账号则注册
     *
     * @param qqId 微信平台的id
     */
    public void registAndLoginByQQ(String qqId) {
        getRALLoader().qq(qqId);
    }


    //================登录======================

    /**
     * 通过手机号登录，已有账号则登录，没账号则登录失败
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void loginByPhone(String phone, String code) {
        getLLoader().phone(phone, code);
    }


    /**
     * 通过微信id登录，已有账号则登录，没账号则登录失败
     *
     * @param wechatId 微信平台的id
     */
    public void loginByWechat(String wechatId) {
        getLLoader().wechat(wechatId);
    }


    /**
     * 通过qq的id登录，已有账号则登录，没账号则登录失败
     *
     * @param qqId 微信平台的id
     */
    public void loginByQQ(String qqId) {
        getLLoader().qq(qqId);
    }


    /**
     * 自动通过cookie登录，有cookie则登录成功，没cookie则登录失败
     */
    public void autoLogin() {
        SharedPreferences sp = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        if (sp.getBoolean("auto", false))
            getLALoader().autoLogin();
    }

    //================注册======================

    /**
     * 通过手机号注册，已有账号则注册失败，没账号则注册
     *
     * @param phone 手机号
     * @param code  验证码
     */
    public void registByPhone(String phone, String code) {
        getRLoader().phone(phone, code);
    }


    /**
     * 通过微信id注册，已有账号则注册失败，没账号则注册
     *
     * @param wechatId 微信平台的id
     */
    public void registByWechat(String wechatId) {
        getRLoader().wechat(wechatId);
    }


    /**
     * 通过qq的id注册，已有账号则注册失败，没账号则注册
     *
     * @param qqId 微信平台的id
     */
    public void registByQQ(String qqId) {
        getRLoader().qq(qqId);
    }


    //================绑定======================

    /**
     * 绑定手机号
     *
     * @param phone  手机号
     * @param code   验证码
     * @param status 绑定：true，解绑：false
     */
    public void bindPhone(String phone, String code, boolean status) {
        getBindLoader().phone(phone, code, status);
    }


    /**
     * 绑定微信
     *
     * @param wechatId 微信平台的id
     * @param status   绑定：true，解绑：false
     */
    public void bindWechat(String wechatId, boolean status) {
        getBindLoader().wechat(wechatId, status);
    }


    /**
     * 绑定qq
     *
     * @param qqId   微信平台的id
     * @param status 绑定：true，解绑：false
     */
    public void bindQQ(String qqId, boolean status) {
        getBindLoader().qq(qqId, status);
    }


    //====================获取用户信息=======================

    /**
     * 通过手机号加载用户信息
     *
     * @param phone 手机号
     */
    public void loadUserInfoByPhone(String phone) {
        getUserInfoLoader().phone(phone);
    }


    /**
     * 通过用户id加载用户信息
     *
     * @param userId 用户id
     */
    public void loadUserInfoById(String userId) {
        getUserInfoLoader().id(userId);
    }


    /**
     * 通过用户id获取用户信息
     *
     * @param id 用户id
     * @return 一条用户信息
     */
    public User getUserInfoById(String id) {
        return getUserInfoLoader().getUserInfo(id);
    }


    /**
     * 通过手机号获取用户信息
     *
     * @param phone 用户手机号
     * @return 一条用户信息
     */
    public User getUserInfoByPhone(String phone) {
        return getUserInfoLoader().getUserInfo(phone);
    }

    //===================================================

    /**
     * 更新用户信息
     */
    public void updateUser(String name, String headUrl, String gender, String birthday, String province, String city) {

        getUpdateUserLoader().update(name, headUrl, gender, birthday, province, city);
    }


    /**
     * 获取当前登录的用户
     *
     * @return
     */
    public User getUser() {
        return mUser;
    }


    /**
     * 设置当前用户
     *
     * @param user
     */
    public void setUser(User user) {
        if (null == user)
            return;
        mUser = user;

        SharedPreferences sp = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putBoolean("auto", true).commit();
    }


    /**
     * 获取登录的广播Action
     *
     * @return
     */
    public String getLoginAction() {
//        return mContext.getPackageName() + ".user_l";
        return getLLoader().getAction();
    }


    /**
     * 获取注册并登录的广播Action
     *
     * @return
     */
    public String getRegistAndLoginAction() {
//        return mContext.getPackageName() + ".user_ral";
        return getRALLoader().getAction();
    }


    /**
     * 获取注册的广播Action
     *
     * @return
     */
    public String getRegistAction() {
//        return mContext.getPackageName() + ".user_r";
        return getRLoader().getAction();
    }


    /**
     * 获取绑定的广播Action
     *
     * @return
     */
    public String getBindAction() {
//        return mContext.getPackageName() + ".user_b";
        return getBindLoader().getAction();
    }


    /**
     * 加载用户信息后发的广播Action
     *
     * @return
     */
    public String getUserInfoAction() {
//        return mContext.getPackageName() + ".user_b";
        return getUserInfoLoader().getAction();
    }


    /**
     * 获取更新用户信息的广播
     *
     * @return
     */
    public String getUpdateUserAction() {
        return getUpdateUserLoader().getAction();
    }


    /**
     * 退出登录广播
     *
     * @return
     */
    public String getActionLogout() {
        return mContext.getPackageName() + ".logout";
    }


    /**
     * 清除本地cookie
     */
    public void clearLoginPhoneFromLocal() {
        mRALLoader.clearLoginPhoneFromLocal();
    }


    /**
     * 是否已登录
     *
     * @return
     */
    public boolean isLogin() {
        return null != mUser && !TextUtils.isEmpty(mUser.getUser_id());
    }


    /**
     * 退出登录
     */
    public void logout() {

        //清除cookie
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(mContext);
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        }

        mUpdateUserLoader.release();
        getLLoader().release();
        getLALoader().release();
        mUser = null;
        mInstance = null;
        //设置下次不自动登录
        SharedPreferences sp = mContext.getSharedPreferences("login", Context.MODE_PRIVATE);
        sp.edit().putBoolean("auto", false).commit();
        Intent intent = new Intent(getActionLogout());
        mContext.sendBroadcast(intent);
    }


    protected LoginLoader getLLoader() {
        if (null == mLLoader)
            mLLoader = new LoginLoader(mContext);
        return mLLoader;
    }


    protected LoginAutoLoader getLALoader() {
        if (null == mLALoader)
            mLALoader = new LoginAutoLoader(mContext);
        return mLALoader;
    }


    protected RegistLoader getRLoader() {
        if (null == mRLoader)
            mRLoader = new RegistLoader(mContext);
        return mRLoader;
    }


    protected RegistAndLoginLoader getRALLoader() {
        if (null == mRALLoader)
            mRALLoader = new RegistAndLoginLoader(mContext);
        return mRALLoader;
    }


    protected BindLoader getBindLoader() {
        if (null == mBindLoader)
            mBindLoader = new BindLoader(mContext);
        return mBindLoader;
    }

    protected UserInfoLoader getUserInfoLoader() {
        if (null == mUserInfoLoader)
            mUserInfoLoader = new UserInfoLoader(mContext);
        return mUserInfoLoader;
    }


    protected UpdateUserLoader getUpdateUserLoader() {
        if (null == mUpdateUserLoader)
            mUpdateUserLoader = new UpdateUserLoader(mContext);
        return mUpdateUserLoader;
    }
}
