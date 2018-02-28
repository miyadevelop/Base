package com.lf.entry;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;


/**
 * 当前时间早于startTime就会被过滤
 * @author wangwei
 *
 */
public class StartTimeFilter extends BaseFilter{

	SimpleDateFormat mFormat;
	public StartTimeFilter(Context context) {
		super(context);
		 mFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		 
		 IntentFilter filter = new IntentFilter();
		 filter.addAction(Intent.ACTION_TIME_TICK);
		 mContext.registerReceiver(mReceiver, filter);
	}


	@Override
	public boolean doFilter(Entry entry, String value) {
		
		try {
			Date date = mFormat.parse(value);
			return new Date().compareTo(date) < 0;//当前时间早于参数指定的时间
			 
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public String getKey() {
		return "startTime";
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
