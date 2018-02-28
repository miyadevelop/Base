package com.my.m.im;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.mobileim.IYWLoginService;
import com.alibaba.mobileim.IYWPushListener;
import com.alibaba.mobileim.YWAPI;
import com.alibaba.mobileim.YWIMKit;
import com.alibaba.mobileim.YWLoginParam;
import com.alibaba.mobileim.channel.event.IWxCallback;
import com.alibaba.mobileim.contact.IYWContact;
import com.alibaba.mobileim.conversation.IYWConversationService;
import com.alibaba.mobileim.conversation.YWMessage;
import com.alibaba.mobileim.gingko.model.tribe.YWTribe;
import com.alibaba.mobileim.login.YWLoginState;
import com.alibaba.wxlib.util.SysUtil;
import com.lf.app.App;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.tools.comm.MsgBean;
import com.lf.tools.comm.MsgHandler;
import com.my.m.user.UserManager;

import org.json.JSONObject;

/**
 * 即时通信管理，将账户模块、第三方即时通信模块和即时消息的分发进行关联
 * Created by wangwei on 17/10/20.
 */
public class ImManager {


    public static YWIMKit mIMKit;//淘宝IM的接口
    private Context mContext;
    private static ImManager mInstance;
    private MsgHandler mMsgHandler;//透传详细的分发
//    private MsgHandlerCenter mMsgHandlerCenter;


    public static ImManager getInstance() {
        if (null == mInstance)
            mInstance = new ImManager();
        return mInstance;
    }

    public void init(Context context, MsgHandler handler) {
        mContext = context;
        mMsgHandler = handler;
        //========初始化阿里即时通信开始=========

        //必须首先执行这部分代码, 如果在":TCMSSevice"进程中，无需进行云旺（OpenIM）和app业务的初始化，以节省内存;
        SysUtil.setApplication(mContext);
        if (SysUtil.isTCMSServiceProcess(mContext)) {
            return;
        }
        //第一个参数是Application Context
        // 这里的APP_KEY即应用创建时申请的APP_KEY，同时初始化必须是在主进程中
        if (SysUtil.isMainProcess()) {
            YWAPI.init((Application) mContext, App.string("ali_app_key"));
        }


        //========初始化阿里即时通信结束=========

        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.getInstance(App.mContext).getLoginAction());
        filter.addAction(UserManager.getInstance(App.mContext).getRegistAndLoginAction());
        filter.addAction(UserManager.getInstance(App.mContext).getRegistAction());
        filter.setPriority(100000);
        mContext.registerReceiver(mReceiver, filter);

    }


    /**
     * 登出
     */
    public void logout() {
        if (mIMKit == null) {
            return;
        }


        // openIM SDK提供的登录服务
        IYWLoginService mLoginService = mIMKit.getLoginService();
        mLoginService.logout(new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
            }

            @Override
            public void onProgress(int arg0) {

            }

            @Override
            public void onError(int arg0, String arg1) {

            }
        });
        mIMKit = null;
    }


    public void login() {
        //此实现不一定要放在Application onCreate中
        if (!UserManager.getInstance().isLogin())
            return;

        if (isLoginIM())
            return;

       String userid =  UserManager.getInstance().getUser().getUser_id();
        //此对象获取到后，保存为全局对象，供APP使用
        // 此对象跟用户相关，如果切换了用户，需要重新获取
        mIMKit = YWAPI.getIMKitInstance(userid, App.string("ali_app_key"));
        //开始登录即时通信
        String password = UserManager.getInstance(App.mContext).getUser().getAlim_pwd();
        IYWLoginService loginService = mIMKit.getLoginService();
        YWLoginParam loginParam = YWLoginParam.createLoginParam(userid, password);
        loginService.login(loginParam, new IWxCallback() {

            @Override
            public void onSuccess(Object... arg0) {
                //添加消息监听
                IYWConversationService conversationService = mIMKit.getConversationService();
                conversationService.removePushListener(msgPushListener);
                conversationService.addPushListener(msgPushListener);
            }

            @Override
            public void onProgress(int arg0) {
            }

            @Override
            public void onError(int errCode, String description) {
                //如果登录失败，errCode为错误码,description是错误的具体描述信息
                Toast.makeText(App.mContext, "登录失败 " + errCode + description, Toast.LENGTH_LONG).show();
            }
        });
    }


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            boolean isSuccess = intent.getBooleanExtra(NetLoader.STATUS, false);

            if (action.equals(UserManager.getInstance(App.mContext).getLoginAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAndLoginAction())
                    || action.equals(UserManager.getInstance(App.mContext).getRegistAction())) {
                if (isSuccess)//登录成功
                {
                    login();
                }
            }
        }
    };


    IYWPushListener msgPushListener = new IYWPushListener() {
        @Override
        public void onPushMessage(YWTribe arg0, YWMessage arg1) {
        }

        @Override
        public void onPushMessage(IYWContact arg0, YWMessage message) {
            //当有新消息到达时，会触发此方法的回调
            //第一个参数是新消息发送者信息
            //第二个参数是消息
            //用户在这里可以做自己的消息提醒
            if (arg0.getUserId().equals("miya_ceshi"))//服务器发来的透传消息
            {
                try {
                    String messageContent = message.getContent();
                    JSONObject jsonObject = new JSONObject(messageContent);
                    if(null != mMsgHandler)
                        mMsgHandler.handlerMsg(new MsgBean(jsonObject.getString("customize")));
                } catch (Exception e) {

                }

            }
        }
    };


    public boolean isLoginIM() {
        if (null == mIMKit)
            return false;
        final String loginUserId = mIMKit.getIMCore().getLoginUserId();
        final String appKey = mIMKit.getIMCore().getAppKey();
        return !TextUtils.isEmpty(loginUserId) && !TextUtils.isEmpty(appKey) && mIMKit.getIMCore().getLoginState() == YWLoginState.success;
    }


    public YWIMKit getIMKit() {
        if (null == mIMKit) {
            //此实现不一定要放在Application onCreate中
            final String userid = UserManager.getInstance(App.mContext).getUser().getUser_id();
            //此对象获取到后，保存为全局对象，供APP使用
            // 此对象跟用户相关，如果切换了用户，需要重新获取
            if (!TextUtils.isEmpty(userid))
                mIMKit = YWAPI.getIMKitInstance(userid, App.string("ali_app_key"));
        }
        return mIMKit;
    }
}
