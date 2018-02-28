package com.lf.entry;

import com.lf.controler.tools.SoftwareData;

import android.content.Context;


/**
 * 启动次数少于指定值的，将会被过滤
 * @author wangwei
 *
 */
public class LaunchFromFilter extends BaseFilter{

	public LaunchFromFilter(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean doFilter(Entry entry, String value) {
		try {
			//当启动的次数 少于 设置的次数，就被过滤
			return SoftwareData.getAllOpenRecord(mContext).size() < Integer.parseInt(value);
		} catch (Exception e) {
		}
		 return false;
	}

	@Override
	public String getKey() {
		// TODO Auto-generated method stub
		return "launchFrom";
	}

}
