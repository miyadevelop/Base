package com.lf.tools.datacollect;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 手机统计
 * 
 * @author LinChen
 *
 */
public class MobStatistics {

	/**
	 * 初始化统计，不是所有统计都需要初始化。比如友盟就不需要
	 * 
	 * @param context
	 */
	public static void init(Context context) {

	}

	/**
	 * 统计界面的onResume事件
	 * 
	 * @param context
	 */
	public static void onResume(Context context) {
		MobclickAgent.onResume(context);
	}

	/**
	 * 统计界面的onPause事件
	 * 
	 * @param context
	 */
	public static void onPause(Context context) {
		MobclickAgent.onPause(context);
	}

	/**
	 * 统计事件名
	 * 
	 * @param context
	 * @param event
	 */
	public static void onEvent(Context context, String event) {
		MobclickAgent.onEvent(context, event);
	}

	/**
	 * 统计事件名和事件值
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	public static void onEvent(Context context, String event, String value) {
		MobclickAgent.onEvent(context, event, value);
	}

	/**
	 * 统计一次，统计事件名
	 * 
	 * @param context
	 * @param event
	 */
	public static void onceEvent(Context context, String event) {
		String tag = event;
		if (!isExit(context, tag)) {
			MobclickAgent.onEvent(context, event);
			addRecord(context, tag);
		}
	}

	/**
	 * 统计一次，统计事件名和事件值
	 * 
	 * @param context
	 * @param event
	 * @param value
	 */
	public static void onceEvent(Context context, String event, String value) {
		String tag = event + "_" + value;
		if (!isExit(context, tag)) {
			MobclickAgent.onEvent(context, event, value);
			addRecord(context, tag);
		}
	}

	/**
	 * 查看是否存在相应的统计
	 * 
	 * @param context
	 * @param tag
	 * @return
	 */
	private static boolean isExit(Context context, String tag) {
		SharedPreferences sp = context.getSharedPreferences("mobclick_once_record",
				Context.MODE_PRIVATE);
		return sp.contains(tag);
	}

	private static void addRecord(Context context, String tag) {
		SharedPreferences sp = context.getSharedPreferences("mobclick_once_record",
				Context.MODE_PRIVATE);
		sp.edit().putString(tag, "exit").commit();
	}
}
