package com.lf.entry;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;


/**
 * 入口操作行为的本地统计
 * @author wangwei
 *
 */
public class EntryStatistics extends NetLoader{

	public static final String EVENT_SHOW = "EVENT_SHOW";//显示事件的Tag
	public static final String EVENT_CLICK = "EVENT_CLICK";//点击事件的Tag
	public static final String KEY = "ENTRY";

	private String mUrl = "http://www.lovephone.com.cn/statistics/statisticsAdd.json";
	public static String mKey;
	private String mUserId;//用户的id，用来统计每个用户的操作行为


	private static EntryStatistics mInstance;


	public EntryStatistics(Context context)
	{
		super(context);
	}

	public static EntryStatistics getInstance(Context context) {
		if(null == mInstance)
		{
			mInstance = new EntryStatistics(context);
		}
		return mInstance;
	}


	/**
	 * 记录事件发生时调用此函数
	 * @param entry 
	 * @param event 事件标识
	 */
	public void onEvent(Entry entry, String event)
	{
		//用id作为key的组成部分，是为了减小每个SharedPreferences文件的大小，不会因为入口数据过多造成读取效率低
		SharedPreferences sp = getContext().getSharedPreferences(KEY+"_" + entry.getId(), Context.MODE_PRIVATE);
		sp.edit().putLong(event, System.currentTimeMillis()).commit();

		if(event.equals(EVENT_CLICK))//暂时只统计点击事件
		{
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("etc_id", entry.getId());
			params.put("etclist_id", entry.getParentId());
			loadWithParams(params);
		}
	}


	/**
	 * 获取某事件最近一次发生的时间
	 * @param entry
	 * @param event
	 * @return
	 */
	public long getEventTime(Entry entry, String event)
	{
		SharedPreferences sp = getContext().getSharedPreferences(KEY + "_" + entry.getId(), Context.MODE_PRIVATE);
		return sp.getLong(event, -1);
	}


	public void release()
	{
		mInstance = null;
	}

	@Override
	public String parse(String jsonStr, Object... objects) {
		return "ok";
	}

	@Override
	public DownloadCheckTask initDownloadTask() {

		DownloadCheckTask task = new DownloadCheckTask();
		task.mUrl = mUrl;
		task.mIsSimple = true;
		LoadUtils.addUniversalParam(getContext(), task);
		task.addParams("appKey", mKey);//入口模块的key
		task.addParams("codeVersion", Consts.CODE_VERSION);//入口模块的代码版本
		if(null != mUserId && !"".equals(mUserId))
			task.addParams("userId", mUserId);
		return task;
	}


	public void setHost(String host)
	{
		mUrl = host + "/statistics/statisticsAdd.json";
	}

	
	public void setUserId(String userId)
	{
		mUserId = userId;
	}
}
