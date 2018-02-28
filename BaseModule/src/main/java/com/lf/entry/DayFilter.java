package com.lf.entry;


import android.content.Context;


/**
 * 展示过滤，在入口上一次展示时间N天内不再显示，也可以限制入口“仅展现一次”
 * @author wangwei
 *
 */
public class DayFilter extends BaseFilter{

	public DayFilter(Context context) {
		super(context);
	}


	@Override
	public boolean doFilter(Entry entry, String value) {
		long time = EntryStatistics.getInstance(mContext).getEventTime(entry, EntryStatistics.EVENT_SHOW);

		if(time < 0)//之前没展示过
			return false;

		//86400000 = 1000*60*60*24,一天的毫秒数
		time = time/86400000;
		long today = System.currentTimeMillis()/86400000;
		long delay = today - time;
		int valueInt = Integer.parseInt(value);
		if(delay >= valueInt)//间隔天数已超过指定的间隔天数，则不过滤
			return false;
		return true;
	}
	

	@Override
	public String getKey() {
		return "day";
	}


	public void release() {
		super.release();
	};
}
