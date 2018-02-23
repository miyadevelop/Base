package com.lf.tools.comm;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.lf.controler.tools.JSONObjectTool;

/**
 * 与服务器通信的时候，服务器传过来的消息对象
 * @author hzc 2016-3-7
 *
 */
public class MsgBean {
	//应用id
	private String appKey;
	//消息是做什么的
	private String dowhat;
	//消息内容
	private String content;
	//发送时间
	private String sendTime;
	//接收该消息的id
	private String receiverId;
	
	public MsgBean(JSONObject jsonObject){
		initData(jsonObject);
		
	}
	
	public MsgBean(String s){
		try {
			JSONObject json = new JSONObject(s);
			initData(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * 数据初始化
	 */
	private void initData(JSONObject jsonObject){
		JSONObjectTool json = new JSONObjectTool(jsonObject);
		appKey = json.getString("app_key", "");
		dowhat = json.getString("do_what", "");
		content = json.getString("content", "");
		sendTime = json.getString("send_time", "");
		receiverId = json.getString("send_id", "");
	}
	
	public String getAppKey() {
		return appKey;
	}
	public void setAppKey(String appId) {
		this.appKey = appId;
	}
	public String getContent() {
		return content;
	}
	public void setContext(String function) {
		this.content = function;
	}

	public String getSendTime() {
		return sendTime;
	}

	public void setSendTime(String sendTime) {
		this.sendTime = sendTime;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String sendId) {
		this.receiverId = sendId;
	}

	public String getDowhat() {
		return dowhat;
	}

	public void setDowhat(String dowhat) {
		this.dowhat = dowhat;
	}
	
	

}
