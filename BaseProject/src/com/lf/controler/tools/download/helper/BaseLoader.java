package com.lf.controler.tools.download.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.content.Intent;

/**
 * 一种规范，限定了加载
 * @author ludeyuan
 */
public abstract class BaseLoader {


	//存放加载数据的结果，获取到的是boolean类型
	public static final String STATUS = "baseloader_status";

	//加载数据失败，广播中携带失败的原因（通过这里的key获取）
	public static final String MESSAGE = "message";

	
	/**
	 * 加载数据
	 */
	protected abstract void load(Object ...objects);

	
	/**
	 * 获取加载完数据后进行通知的广播的Action值
	 * @return Action值
	 */
	public abstract String getAction();


	protected final void sendBroadCast(Context context,boolean status, String message,HashMap<String,String> intentParams){
		Intent intent = new Intent();
		intent.setAction(getAction());	//设置广告的action
		intent.putExtra(STATUS,status);//设置加载的状态（成功／失败）
		intent.putExtra(MESSAGE, message);//设置失败的信息
		if(null != intentParams)
		{
			Iterator<Entry<String, String>> paramsIter = intentParams.entrySet().iterator();
			while (paramsIter.hasNext()) {
				Entry<String, String> param = paramsIter.next();
				intent.putExtra(param.getKey(), param.getValue());
			}
		}
		context.sendBroadcast(intent);
	}


	/**
	 * 销毁Loader对象时，释放loader中的数据
	 */
	public void release(){}
}
