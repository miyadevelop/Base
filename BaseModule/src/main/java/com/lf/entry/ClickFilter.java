package com.lf.entry;


import android.content.Context;


/**
 * 点击过滤，入口点击过后就不再显示
 * @author wangwei
 *
 */
public class ClickFilter extends BaseFilter{

	public ClickFilter(Context context) {
		super(context);
	}


	@Override
	public boolean doFilter(Entry entry, String value) {

		if(Boolean.valueOf(value))//要进行点击过滤
		{
			long time = EntryStatistics.getInstance(mContext).getEventTime(entry, EntryStatistics.EVENT_CLICK);
			if(time > 0)//曾经点击过
				return true;
		}
		return false;
	}

	@Override
	public String getKey() {
		return "click";
	}


	public void release() {
		super.release();
	};
}
