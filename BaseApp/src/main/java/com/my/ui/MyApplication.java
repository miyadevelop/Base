package com.my.ui;


import com.alibaba.mobileim.aop.AdviceBinder;
import com.alibaba.mobileim.aop.PointCutEnum;
import com.lf.app.App;
import com.lf.controler.tools.Config;
import com.lf.controler.tools.NetWorkManager;
import com.lf.tools.comm.MyMsgHandler;
import com.my.m.im.ImManager;
import com.my.m.user.UserManager;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;


/**
 * Created by wangwei on 17/9/7.
 */
public class MyApplication extends App implements NetWorkManager.IMobileDataListener, NetWorkManager.IWifiListener {

    public static String ENTRY_IDS = "26,27,28,29,30,31,33";


    //即时通信的相关绑定
    static {
        //聊天界面
        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_UI_POINTCUT, AliChatActivity.class);
        AdviceBinder.bindAdvice(PointCutEnum.CHATTING_FRAGMENT_OPERATION_POINTCUT, AliChattingOperationCustomSample.class);

        //会话列表UI相关
        AdviceBinder.bindAdvice(PointCutEnum.CONVERSATION_FRAGMENT_UI_POINTCUT, AliConversationActivity.class);

        //修改消息通知相关
        AdviceBinder.bindAdvice(PointCutEnum.NOTIFICATION_POINTCUT, AliIMNotification.class);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (getCurProcessName(this).equals(getPackageName())) {
            //加载配置
            new Config(this).loadWithParams(null);

            UserManager.getInstance(App.mContext).autoLogin();
            //初始化即时通信
            new ImManager().init(this, MyMsgHandler.getInstance());
            //添加监听消息
//            MyMsgHandler.getInstance().addListener(OrderUpdateHandler.DO_WHAT, new OrderUpdateHandler(this));

            //监听网络变化
            NetWorkManager.getInstance(this).addMobileDataListener(this);
            NetWorkManager.getInstance(this).addWifiListener(this);
        }

        //初始化友盟推送
        PushAgent mPushAgent = PushAgent.getInstance(this);
        //注册推送服务，每次调用register方法都会回调该接口
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

    }

    @Override
    public void onSwitch(boolean isOpen) {
        //这里isOpen的值在手机网的情况下不正确，用isConnect代替
        if (NetWorkManager.getInstance(App.mContext).isConnect()) {
            if (!UserManager.getInstance().isLogin())
                UserManager.getInstance(this).autoLogin();
        }
    }

    @Override
    public void onStateChange(int state) {

    }

}
