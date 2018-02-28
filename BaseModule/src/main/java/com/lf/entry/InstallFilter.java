package com.lf.entry;

import android.content.Context;

import com.lf.controler.tools.ApkUtil;

/**
 * 如果安装了指定的应用，入口将会被过滤
 * @author wangwei
 *
 */
public class InstallFilter extends BaseFilter
{
	
	public InstallFilter(Context context) {
		super(context);
		
	}

	@Override
	public boolean doFilter(Entry entry, String value) {
//		if("com.lf.linhua".equals(value))
//			value = "com.lf.linghua";
		return ApkUtil.isInstall(mContext, value);
	}

	@Override
	public String getKey() {
		return "install";
	}

}