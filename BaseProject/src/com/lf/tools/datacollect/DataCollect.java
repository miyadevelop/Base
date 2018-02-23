package com.lf.tools.datacollect;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

/**
 * 数据统计管理、对外接口
 * 
 * @author ww
 * 
 */
public class DataCollect {
	private static DataCollect instance;

	private Context context;
	/**
	 * 一次性统计的集合
	 */
	private Set<String> onceDCSet;
	private SharedPreferences dcPreferences;

	private DataCollect(Context context) {
		this.context = context.getApplicationContext();
		onceDCSet = new HashSet<String>();
		dcPreferences = this.context.getSharedPreferences("data_collect", Context.MODE_PRIVATE);
		String json = dcPreferences.getString("once_json", null);
		if (json != null) {
			try {
				JSONArray array = new JSONArray(json);
				int len = array.length();
				for (int i = 0; i < len; ++i) {
					onceDCSet.add(array.getString(i));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 单例，获取唯一实例
	 * 
	 * @param context
	 * @return
	 */
	public static synchronized DataCollect getInstance(Context context) {
		if (instance == null) {
			instance = new DataCollect(context);
		}
		return instance;
	}

	/**
	 * 开始事件
	 */
	public void onStart() {
	}

	/**
	 * 添加事件
	 * 
	 * @param lable
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 * @param value
	 *            事件的值
	 */
	public void addEvent(Context lable, String name, String value) {
		if (lable instanceof Activity) {
			PackageManager pm = context.getPackageManager();
			try {
				int descId = pm.getActivityInfo(((Activity) lable).getComponentName(), 0).descriptionRes;
				String str = null;
				if (descId != 0) {
					str = lable.getString(descId);
				}
				addEvent(str, name, value);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("参数不是Activity");
		}
	}

	/**
	 * 添加事件
	 * 
	 * @param lable
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 */
	public void addEvent(Context lable, String name) {
		addEvent(lable, name, null);
	}

	/**
	 * 添加事件
	 * 
	 * @param event
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 * @param value
	 *            事件的值
	 */
	public void addEvent(IDataCollectEvent event, String name, String value) {
		addEvent(event.getName(), name, value);
	}

	/**
	 * 添加事件
	 * 
	 * @param event
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 */
	public void addEvent(IDataCollectEvent event, String name) {
		addEvent(event, name, null);
	}

	/**
	 * 添加事件
	 * 
	 * @param event
	 * @param name
	 * @param value
	 */
	public void addEvent(String event, String name, String value) {
		if (TextUtils.isEmpty(event)) {
			onEvent(name, value);
		} else {
			onEvent(event + "_" + name, value);
		}
	}

	/**
	 * 添加事件
	 * 
	 * @param event
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 */
	public void addEvent(String event, String name) {
		addEvent(event, name, null);
	}

	/**
	 * 添加事件
	 * 
	 * @param lable
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 * @param value
	 *            事件的值
	 */
	public void onceEvent(Context lable, String name, String value) {
		if (lable instanceof Activity) {
			PackageManager pm = context.getPackageManager();
			try {
				int descId = pm.getActivityInfo(((Activity) lable).getComponentName(), 0).descriptionRes;
				String str = null;
				if (descId != 0) {
					str = lable.getString(descId);
				}
				onceEvent(str, name, value);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("参数不是Activity");
		}
	}

	/**
	 * 添加事件
	 * 
	 * @param lable
	 *            事件的模块名
	 * @param name
	 *            事件的事件名
	 */
	public void onceEvent(Context lable, String name) {
		onceEvent(lable, name, null);
	}

	/**
	 * 添加事件
	 * 
	 * @param event
	 *            事件的模块名
	 * @param name
	 * @param value
	 */
	public void onceEvent(IDataCollectEvent event, String name, String value) {
		onceEvent(event.getName(), name, value);
	}

	/**
	 * 添加一次性事件
	 * 
	 * @param event
	 * @param name
	 */
	public void onceEvent(IDataCollectEvent event, String name) {
		onceEvent(event, name, null);
	}

	/**
	 * 添加一次性事件
	 * 
	 * @param event
	 * @param name
	 * @param value
	 */
	public void onceEvent(String event, String name, String value) {
		String tag = event + "_" + name + "_" + value;
		if (!onceDCSet.contains(tag)) {
			onceDCSet.add(tag);
			JSONArray array = new JSONArray(onceDCSet);
			dcPreferences.edit().putString("once_json", array.toString()).commit();
			if (TextUtils.isEmpty(event)) {
				onEvent(name, value);
			} else {
				onEvent(event + "_" + name, value);
			}
		}
	}

	/**
	 * 添加一次性事件
	 * 
	 * @param event
	 * @param name
	 */
	public void onceEvent(String event, String name) {
		onceEvent(event, name, null);
	}

	public void release() {
	}

	public void forceUpload() {
	}

	/**
	 * 统计
	 * 
	 * @param name
	 *            事件名
	 * @param value
	 *            事件值，没有的话传入null
	 */
	private void onEvent(String name, String value) {
		if (value == null) {
			MobclickAgent.onEvent(context, name);
		} else {
			MobclickAgent.onEvent(context, name, value);
		}
	}

	public void notifyUpload() {
	}

	public void justAddSelfDCBugIsDeprecated(String name, String value) {
	}

	/**
	 * 广告安装的统计
	 * 
	 * @param id
	 * @param packageName
	 */
	public void adInstall(String id, String packageName) {
	}

	/**
	 * 推送安装的统计
	 * 
	 * @param id
	 * @param packageName
	 */
	public void pushInstall(String id, String packageName) {
	}
	
	public void onPause(Context context) {
		MobclickAgent.onPause(context);
	}

	public void onResume(Context context) {
		MobclickAgent.onResume(context);
	}
}
