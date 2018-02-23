package com.lf.tools.comm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.EMConnectionListener;
import com.easemob.EMError;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.chat.CmdMessageBody;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.util.NetUtils;
import com.lf.controler.tools.SoftwareData;


/**
 * 通讯中心，负责环信的登录注销以及接收消息并进行分发消息
 * @author hzc 2016-3-4
 *
 */
public class CommunicationCenter {
	private static CommunicationCenter mCommCenter;
	private Context mContext;
	private MsgHandlerCenter mMsgHandlerCenter;
	
	private EMEventListener eventListener;
	
	private CommunicationCenter(Context context){
		mContext = context;
	};
	
	public static CommunicationCenter getInstance(Context context){
		if(mCommCenter == null)
			mCommCenter = new CommunicationCenter(context);
		return mCommCenter;
		
	}
	
	/**
	 * 即时通讯在application中的初始化
	 * @param applicationContext
	 * @param processName
	 */
	public void initOnApplication(Context applicationContext,String processName){
		String processAppName = SoftwareData.getCurProcessName(applicationContext);
//		 如果APP启用了远程的service，此application:onCreate会被调用2次
//		 为了防止环信SDK被初始化2次，加此判断会保证SDK被初始化1次
//		 默认的app会在以包名为默认的process name下运行，如果查到的process name不是APP的process name就立即返回
		if (processAppName == null ||!processAppName.equalsIgnoreCase(processName)) {
		    return;
		}
		EMChat.getInstance().init(applicationContext);
		/**
		 * debugMode == true 时为打开，SDK会在log里输入调试信息
		 * @param debugMode
		 * 在做代码混淆的时候需要设置成false
		 */
		EMChat.getInstance().setDebugMode(false);
	}
	
	/**
	 * 登录
	 * @param name
	 * @param psd
	 */
	public void login(String name,String psd){
		NewMessageBroadcastReceiver msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		//接收普通消息
//		intentFilter.addAction(EMChatManager.getInstance().getNewMessageBroadcastAction());
//		接收透传消息
		intentFilter.addAction(EMChatManager.getInstance().getCmdMessageBroadcastAction());
		intentFilter.setPriority(3);
		mContext.registerReceiver(msgReceiver, intentFilter);
		
//		registerEventListener();
		//告知环信消息接收已经注册
		EMChat.getInstance().setAppInited();
		//取消消息在通知栏的通知
		EMChatManager.getInstance().getChatOptions().setShowNotificationInBackgroud(false);
		EMChatManager.getInstance().login(name,psd,new EMCallBack() {//回调
			@Override
			public void onSuccess() {
				
			}
		 
			@Override
			public void onProgress(int progress, String status) {
		 
			}
		 
			@Override
			public void onError(int code, String message) {
			}
		});
		
		
		
		
	}
	
	 /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        eventListener = new EMEventListener() {
            private BroadcastReceiver broadCastReceiver = null;
            
            @Override
            public void onEvent(EMNotifierEvent event) {
                EMMessage message = null;
                if(event.getData() instanceof EMMessage){
                    message = (EMMessage)event.getData();
                }
                
                switch (event.getEvent()) {
                case EventNewMessage:
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    break;
                case EventOfflineMessage:
                    break;
                // below is just giving a example to show a cmd toast, the app should not follow this
                // so be careful of this
                case EventNewCMDMessage:
                { 
                    //获取消息body
                    CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
                    final String action = cmdMsgBody.action;//获取自定义action
                    
                    //获取扩展属性 此处省略
                    //message.getStringAttribute("");
                    
                    final String CMD_TOAST_BROADCAST = "easemob.demo.cmd.toast";
                    IntentFilter cmdFilter = new IntentFilter(CMD_TOAST_BROADCAST);
                    
                    if(broadCastReceiver == null){
                        broadCastReceiver = new BroadcastReceiver(){

                            @Override
                            public void onReceive(Context context, Intent intent) {
                                // TODO Auto-generated method stub
                                Toast.makeText(mContext, intent.getStringExtra("cmd_value"), Toast.LENGTH_SHORT).show();
                            }
                        };
                        
                      //注册广播接收者
                        mContext.registerReceiver(broadCastReceiver,cmdFilter);
                    }

                    Intent broadcastIntent = new Intent(CMD_TOAST_BROADCAST);
                    broadcastIntent.putExtra("cmd_value", action);
                    mContext.sendBroadcast(broadcastIntent, null);
                    
                    break;
                }
                case EventDeliveryAck:
                    message.setDelivered(true);
                    break;
                case EventReadAck:
                    message.setAcked(true);
                    break;
                // add other events in case you are interested in
                default:
                    break;
                }
                
            }
        };
        
        EMChatManager.getInstance().registerEventListener(eventListener);
    }
	
	/**
	 * 开始监听消息
	 * @param handler
	 */
	public void handlerMsg(MsgHandler handler){
		mMsgHandlerCenter = new MsgHandlerCenter(mContext, handler);
	}
	
	
	
	/**
	 * 退出登录
	 * 
	 * @param unbindDeviceToken
	 *            是否解绑设备token(使用GCM才有)
	 * @param callback
	 *            callback
	 */
	public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
		//此方法为异步方法
		EMChatManager.getInstance().logout(new EMCallBack() {
			 
			@Override
			public void onSuccess() {
			    // TODO Auto-generated method stub
		 
			}
		 
			@Override
			public void onProgress(int progress, String status) {
			    // TODO Auto-generated method stub
		 
			}
		 
			@Override
			public void onError(int code, String message) {
			    // TODO Auto-generated method stub
		 
			}
		});
	}
	
	//登出的监听
	public void connection(){
		//注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
	}
	
	//实现ConnectionListener接口
	private class MyConnectionListener implements EMConnectionListener {
	    @Override
		public void onConnected() {
		//已连接到服务器
		}
		@Override
		public void onDisconnected(final int error) {
			if(error == EMError.USER_REMOVED){
				// 显示帐号已经被移除
			}else if (error == EMError.CONNECTION_CONFLICT) {
				// 显示帐号在其他设备登录
			} else {
			if (NetUtils.hasNetwork(mContext)){
				//连接不到聊天服务器
			}else{
				//当前网络不可用，请检查网络设置
			}
				
			}
		}
	}
		
		
		private class NewMessageBroadcastReceiver extends BroadcastReceiver {
			@Override
			public void onReceive(Context context, Intent intent) {
			    // 阻止广播继续传递
				abortBroadcast();
				// 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
				String msgId = intent.getStringExtra("msgid");
				
				//发送方
				String username = intent.getStringExtra("from");
				// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取message对象
//				EMMessage message = EMChatManager.getInstance().getMessage(msgId);//普通消息的获取
				EMMessage message = intent.getParcelableExtra("message");
				
				CmdMessageBody cmdMsgBody = (CmdMessageBody) message.getBody();
				String aciton = cmdMsgBody.action;//获取自定义action
				
				
				if(mMsgHandlerCenter!=null)
				mMsgHandlerCenter.handlerCmdMsg(aciton);
				
			}
		}
		

}
