package com.lf.entry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.my.m.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 入口管理，对外提供入口的初始化、加载、获取、入口数据变化监听、处理入口点击响应等功能
 * 除了入口的界面相关工具以外，所有对入口的操作都通过此类完成
 * @author wangwei
 *
 */
public class EntryManager  implements BaseFilter.OnFilterChangeListener{

	private EntryMatcherControl mEntryMatcherControl;//识别入口intent的匹配器，用于决定用哪一个
	private EntryFilterControl mEntryFilterControl;//入口过滤器
	private EntryLoader mEntryLoader;//入口数据加载
	private Context mContext;
	private HashMap<String, ArrayList<Entry>> mEntrys;//过滤后的入口集合

	//entry里的intent需要替换的本地参数，key替换的内容，value是替换的值，例如<@user_id , user id的值>
	//这里主要是入口在打开界面时需要提交一些参数，这些参数，可能只有本地才能获取到，服务端无法配置，所以由本地添加
	private HashMap<String, String> mIntentReplacement;

	public static EntryManager mInstance;
	public static String ENTRY_KEY;

	public EntryManager(Context context) {
		mContext = context.getApplicationContext();
		mEntryFilterControl = new EntryFilterControl(mContext, this);
		mEntryMatcherControl = new EntryMatcherControl();	
		mEntrys = new HashMap<String, ArrayList<Entry>>();
		mIntentReplacement = new HashMap<String, String>();
		//默认的Loader
		mEntryLoader = new EntryLoader(mContext);
		//171108修改，将初始化和构造放在一起
		// 一般情况下会在application的onCreate中调用init，然后在MainActivity中调用release
		// 这样会造成init和release不是成对调用，从而导致问题
		// 现在将初始化和构造放在一起，因为在release时会将mInstance变为空，所以必然可以使init和release成对调用
		init();
	}


	/**
	 * 单例
	 * @param context
	 * @return
	 */
	public static EntryManager getInstance(Context context)
	{
		if(null == mInstance)
			mInstance = new EntryManager(context);
		return mInstance;
	}


	/**
	 * 初始化
	 * @param entryKey 入口模块的key
	 */
	@Deprecated
	public void init(String entryKey)
	{
		EntryLoader.mKey = entryKey;
		EntryStatistics.mKey = entryKey;
		String host =  mContext.getString(R.string.entry_host);
		if(null == host)
			host = "http://www.lovephone.com.cn";//默认主机
		if(host.endsWith("/"))
			host = host.substring(0, host.length() - 1);
		mEntryLoader.setUrl(host + "/entrance/getEntrance.json");
		EntryStatistics.getInstance(mContext).setHost(host);
	}


	/**
	 * 初始化，配置入口的key和host
	 */
	public void init()
	{
		String key = mContext.getString(R.string.entry_key);
		if(null != key && key.length() > 0)
		{
			EntryLoader.mKey = key;
			EntryStatistics.mKey = key;
		}
		String host =  mContext.getString(R.string.entry_host);
		if(null == host)
			host = "http://www.lovephone.com.cn";//默认主机
		if(host.endsWith("/"))
			host = host.substring(0, host.length() - 1);
		mEntryLoader.setUrl(host + "/entrance/getEntrance.json");
		EntryStatistics.getInstance(mContext).setHost(host);
	}


	/**
	 * 让外界设置EntryLoader，可以让入口的数据来源和数据解析相对自由、可扩展
	 * @param entryLoader
	 */
	public void setEntryLoader(EntryLoader entryLoader)
	{
		mEntryLoader = entryLoader;
	}


	/**
	 * 加载一个入口集合
	 * @param entryListId 入口集合的id
	 */
	public void load(String entryListId)
	{
		mEntryLoader.load(entryListId);
	}


	/**
	 * 获取一个已过滤的入口集合
	 * @param entryListId 入口集合的id
	 * @return 该id对应的入口集合
	 */
	public ArrayList<Entry> get(String entryListId)
	{
		ArrayList<Entry> entries = mEntrys.get(entryListId);
		if(null == entries)
		{
			entries = new ArrayList<Entry>();
		}

		entries.clear();
		for(Entry entry: mEntryLoader.get(entryListId))
		{
			if(!mEntryFilterControl.filter(entry))//入口过滤，过滤掉的入口，不加入到返回的入口集合中
				entries.add(entry);
		}
		mEntrys.put(entryListId, entries);

		return entries;
	}


	/**
	 * 获取一个未过滤的入口集合
	 * @param entryListId 入口集合的id
	 * @return 该id对应的入口集合
	 */
	public ArrayList<Entry> getEntryListNoFilter(String entryListId)
	{
		return mEntryLoader.get(entryListId);

	}


	/**
	 * 跳转到入口代码
	 * 
	 * @param entry
	 */
	public void goTo(Context context, Entry entry) {

		Intent intent = entry.getIntent();
		//将entry中的intent里面包含的需要替换参数字段，替换成对应的参数值
		for(Map.Entry<String, String> item : mIntentReplacement.entrySet())
		{
			String dataString = intent.getDataString();
			if(null != dataString)
			{
				dataString = dataString.replace(item.getKey(), item.getValue());
				intent.setDataAndType(Uri.parse(dataString), intent.getType());
			}

			Bundle bundle = intent.getExtras();
			if(null != bundle)
			{
				for(String key : bundle.keySet())
				{
					String value = bundle.getString(key);
					if(null != value)
					{
						value = value.replace(item.getKey(), item.getValue());
						intent.putExtra(key, value);
					}
				}
			}
		}

		//调用Matcher执行intent要进行的操作
		mEntryMatcherControl.goTo(context, entry, intent);
		//统计入口点击
		EntryStatistics.getInstance(mContext).onEvent(entry, EntryStatistics.EVENT_CLICK);
	}


	/**
	 * 展示时进行调用，用来对入口的展示进行统计和记录，没有其他功能作用，非必须调用
	 * @param entry
	 */
	public void onShow(Entry entry)
	{
		EntryStatistics.getInstance(mContext).onEvent(entry, EntryStatistics.EVENT_SHOW);
	}


	/**
	 * 当入口过滤条件发生变化时调用这里
	 */
	@Override
	public void onChange(String entryListId) {
		//通知外界入口发生变化
		if(null != mEntryLoader)
		{
			mEntryLoader.sendBroadCast(entryListId);
		}
	}


	/**
	 * 设置用户id，用来根据用户id获取不同的入口数据，并且在通过入口打开一些指定界面时，可以将用户id传入入口的intent
	 * @param userId
	 */
	public void setUserId(String userId)
	{
		if(null != mEntryLoader)
		{
			mEntryLoader.setUserId(userId);
		}
		//统计模块，用来统计每个用户的行为
		EntryStatistics.getInstance(mContext).setUserId(userId);
		addIntentReplacement("@userId", userId);
	}


	/**
	 * 添加intent中替换的参数
	 * @param key 替换的字段值
	 * @param value 替换后的值
	 */
	private void addIntentReplacement(String key, String value)
	{
		if(key == null || "".equals(key) || value == null || "".equals(value))
			return;
		mIntentReplacement.put(key, value);
	}


	/**
	 * 入口加载完成的广播
	 * @return
	 */
	public String getAction()
	{
		return mEntryLoader.getAction();
	}


	/**
	 * 设置加载入口的网址的前缀
	 * @param url
	 */
	@Deprecated
	public void setUrl(String url)
	{
		mEntryLoader.setUrl(url);
	}


	/**
	 * 释放资源
	 */
	public void release()
	{
		mEntryFilterControl.release();
		mEntryMatcherControl.release();
		if(null != mEntryLoader)
			mEntryLoader.release();
		mEntrys.clear();
		mIntentReplacement.clear();
		mInstance = null;
	}

}
