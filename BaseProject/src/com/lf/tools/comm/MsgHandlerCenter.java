package com.lf.tools.comm;

import android.content.Context;


/**
 * 消息处理中心，对消息进行处理
 * @author hzc 2016-3-4
 *
 */
public class MsgHandlerCenter {
	private Context mContext;
	private MsgHandler mMsgHandler;
	
	public MsgHandlerCenter(Context context,MsgHandler handler){
		mContext = context;
		mMsgHandler = handler;
	}
	
	
    
	public void handlerCmdMsg(String aciton){
		MsgBean bean = new MsgBean(aciton);
		mMsgHandler.handlerMsg(bean);
	}
	

}
