package com.lf.view.tools.update;

import java.util.ArrayList;

/**
 * 软件更新的对象,从服务器上获取
 * @author ludeyuan
 *
 */
public class UpdateBean {
	private String mSize;	//软件的大小
	private String mVersion;//版本号
	private String mUpdateMessages;//版本更新的信息
	private String mUpdateUrl;	//更新软件的网址
	private boolean mMustUpdateCur = false;//强制升级
	//需要强制升级的旧版本
	private ArrayList<String> mNeedMustUpdate = new ArrayList<String>();
	
	public void setSize(String size){
		mSize = size;
	}
	
	public void setVersion(String version){
		mVersion = version;
	}
	
	public void setUpdateMessages(String message){
		mUpdateMessages = message;
	}
	
	public void setUpdateUrl(String url){
		mUpdateUrl = url;
	}
	
	/**
	 * 获取到软件的大小
	 */
	public String getSize(){
		return mSize;
	}
	
	/**
	 * 获取到软件的版本号
	 */
	public String getVersion(){
		return mVersion;
	}
	
	/**
	 * 获取到更新的消息
	 * @return
	 */
	public String getUpdateMessage(){
		return mUpdateMessages;
	}
	
	/**
	 * 获取到下载软件的网址
	 */
	public String getUpdateUrl(){
		return mUpdateUrl;
	}
	
	/**
	 * 拷贝对象
	 */
	public void copyBean(UpdateBean bean){
		mSize = bean.getSize();
		mUpdateMessages = bean.getUpdateMessage();
		mUpdateUrl = bean.getUpdateUrl();
		mVersion = bean.getVersion();
		mMustUpdateCur = bean.isMustUpdateCur();
	}

	public ArrayList<String> getNeedMustUpdate() {
		return mNeedMustUpdate;
	}

	public void setNeedMustUpdate(ArrayList<String> mNeedMustUpdate) {
		this.mNeedMustUpdate = mNeedMustUpdate;
	}

	public boolean isMustUpdateCur() {
		return mMustUpdateCur;
	}

	public void setMustUpdateCur(boolean mMustUpdateCur) {
		this.mMustUpdateCur = mMustUpdateCur;
	}
}
