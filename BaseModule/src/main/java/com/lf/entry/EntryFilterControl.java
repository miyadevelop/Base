package com.lf.entry;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;

import com.lf.entry.BaseFilter.OnFilterChangeListener;
import com.lf.tools.log.MyLog;

/**
 * 入口过滤控制
 * @author wangwei
 *
 */
public class EntryFilterControl {

	private HashMap<String, BaseFilter> mFilters;

	public EntryFilterControl(Context context, OnFilterChangeListener listener)
	{
		mFilters = new HashMap<String, BaseFilter>();
		//注册过滤器
		{
			InstallFilter filter = new InstallFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			NotInstallFilter filter = new NotInstallFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			StartTimeFilter filter = new StartTimeFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			EndTimeFilter filter = new EndTimeFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			LaunchToFilter filter = new LaunchToFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			LaunchFromFilter filter = new LaunchFromFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}
		
		{
			EverydayFromFilter filter = new EverydayFromFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			EverydayToFilter filter = new EverydayToFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}
		
		{
			DayFilter filter = new DayFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}

		{
			ClickFilter filter = new ClickFilter(context);
			filter.setOnFilterChangeListener(listener);
			mFilters.put(filter.getKey(), filter);
		}
	}


	/**
	 * 执行过滤
	 * @param keyAndValue 过滤的键值对，key表示使用哪一个过滤器进行过滤，value表示过滤时需要用到的参数
	 * @return 被过滤掉，返回true，没有被过滤掉，返回false
	 */
	public boolean filter(Entry entry)
	{

		for (Map.Entry<String, String> mapEntry: entry.getFilters().entrySet()) {
			BaseFilter filterInterface = mFilters.get(mapEntry.getKey());
			if(null != filterInterface)
			{
				if(filterInterface.filter(entry, mapEntry.getValue()))
				{
					//这句输出不要删除，便于找到入口被过滤的原因
					MyLog.i("filter: entry=" + entry.getId() + ", " + mapEntry.getKey() + "=" + mapEntry.getValue());
					return true;
				}
			}
			else
			{
				//这句输出不要删除，便于找到入口不被过滤的原因
				MyLog.i("filter: no found filter " + mapEntry.getKey());
			}
		}
		return false;
	}

	
	/**
	 * 释放资源
	 */
	public void release()
	{
		for(BaseFilter filter : mFilters.values())
		{
			filter.release();
		}
		mFilters.clear();
	}
}
