package com.lf.entry;

import android.content.Context;

import com.lf.controler.tools.SoftwareData;


/**
 * 启动次数多于指定值的将会被过滤
 * @author wangwei
 *
 */
public class LaunchToFilter extends BaseFilter{

	public LaunchToFilter(Context context) {
		super(context);
	}

	@Override
	public boolean doFilter(Entry entry, String value) {
		try {
			//当启动的次数 多于 设置的次数，就被过滤
			return SoftwareData.getAllOpenRecord(mContext).size() > Integer.parseInt(value);
		} catch (Exception e) {
		}
		 return false;
	}

	@Override
	public String getKey() {
		return "launchTo";
	}

}
