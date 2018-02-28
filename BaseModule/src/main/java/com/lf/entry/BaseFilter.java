package com.lf.entry;

import android.content.Context;

import java.util.HashMap;


/**
 * 入口过滤器接口规范
 * @author wangwei
 *
 */
public abstract class BaseFilter {
	
	protected OnFilterChangeListener mOnFilterChangeListener;
	protected Context mContext;
	protected HashMap<String, Entry> mEntrys;
	
	public BaseFilter(Context context)
	{
		mContext = context.getApplicationContext();
		mEntrys = new HashMap<String, Entry>();
	}
	
	public void setOnFilterChangeListener(OnFilterChangeListener listener)
	{
		mOnFilterChangeListener = listener;
	}
	
	
	/**
	 * 执行过滤
	 * @param value 过滤需要的参数，比如依据时间过滤，value可以代表指定的时间值
	 * @return 被过滤掉，返回true，没有被过滤掉，返回false
	 */
	public boolean filter(Entry entry, String value){
		mEntrys.put(entry.getParentId(), entry);
		return doFilter(entry, value);
	}
	
	
	/**
	 * 执行过滤
	 * @param value 过滤需要的参数，比如依据时间过滤，value可以代表指定的时间值
	 * @return 被过滤掉，返回true，没有被过滤掉，返回false
	 */
	protected abstract boolean doFilter(Entry entry, String value);
	
	/**
	 * 过滤器的标识，与服务端配置的过滤参数中的key的值一致
	 * @return
	 */
	public abstract String getKey();
	
	
	/**
	 * 释放资源
	 */
	public void release()
	{
		mOnFilterChangeListener = null;
		mContext = null;
	}
	
	
	/**
	 * 过滤的外在条件发生变化的监听
	 * @author wangwei
	 *
	 */
	public static interface OnFilterChangeListener
	{
		public void onChange(String entryListId);
	}

}
