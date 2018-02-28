package com.lf.entry;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * 当前时间早于everydayFrom就会被过滤,不比较日期，仅比较HH:mm:ss
 * @author wangwei
 *
 */
public class EverydayFromFilter extends BaseFilter{

	SimpleDateFormat mFormat;
	public EverydayFromFilter(Context context) {
		super(context);
		 mFormat = new SimpleDateFormat("HH:mm:ss");//不比较日期
		 
		 IntentFilter filter = new IntentFilter();
		 filter.addAction(Intent.ACTION_TIME_TICK);
		 mContext.registerReceiver(mReceiver, filter);
	}


	@Override
	public boolean doFilter(Entry entry, String value) {
		
		try {
			Date date = mFormat.parse(value);
			Date now = mFormat.parse(mFormat.format(new Date()));
			return now.compareTo(date) < 0;//当前时间早于参数指定的时间
			 
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public String getKey() {
		return "everydayFrom";
	}
	
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {

			if(null != mOnFilterChangeListener)
			{
				for(String key: mEntrys.keySet())
				{
					mOnFilterChangeListener.onChange(key);
				}
				
			}
		}
	};
	
	
	public void release() {
		mContext.unregisterReceiver(mReceiver);
		super.release();
	};

}
