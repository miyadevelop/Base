package com.lf.entry;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;


/**
 * 入口被点击后的响应匹配器，决定由哪一个 {@link #EntryMatcherInterface} 接收点击事件
 * @author wangwei
 *
 */
public class EntryMatcherControl {

	private HashMap<String, EntryMatcherInterface> mMatchers;

	public EntryMatcherControl()
	{
		mMatchers = new HashMap<String, EntryMatcherInterface>();
		//初始化、注册所有的matcher
		EntryMatcherForLauncher matcherForLauncher = new EntryMatcherForLauncher();
		mMatchers.put(matcherForLauncher.getKey(), matcherForLauncher);
		EntryMatcherForBroadCast entryMatcherForBroadCast = new EntryMatcherForBroadCast();
		mMatchers.put(entryMatcherForBroadCast.getKey(), entryMatcherForBroadCast);
	}


	/**
	 * 执行入口点击后的响应
	 * @param context
	 * @param entry
	 */
	public void goTo(Context context, Entry entry, Intent intent) 
	{
		//找到与entry所对应的matcher
		EntryMatcherInterface matcher = mMatchers.get(entry.getMatch());
		if(null != matcher)
		{
			matcher.goTo(context, intent);
		}
	}


	/**
	 * 执行界面跳转的入口匹配器
	 * @author wangwei
	 *
	 */
	public class EntryMatcherForLauncher implements EntryMatcherInterface
	{

		@Override
		public void goTo(Context context, Intent intent) {

			try {
				if(!(context instanceof Activity))
				{
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				if(null != intent.getComponent() &&
						(null == intent.getComponent().getPackageName() || "".equals(intent.getComponent().getPackageName()))
						&& null != intent.getComponent().getClassName())
					intent.setClassName(context, intent.getComponent().getClassName());

				context.startActivity(intent);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		public String getKey() {
			return "launch";
		}

	}


	/**
	 * 执行发送广播的入口匹配器
	 * @author wangwei
	 *
	 */
	public class EntryMatcherForBroadCast implements EntryMatcherInterface
	{

		@Override
		public void goTo(Context context, Intent intent) {
			context.sendBroadcast(intent);
		}

		@Override
		public String getKey() {
			// TODO Auto-generated method stub
			return "broadCast";
		}

	}
	
	
	public void release()
	{
		mMatchers.clear();
	}
}
