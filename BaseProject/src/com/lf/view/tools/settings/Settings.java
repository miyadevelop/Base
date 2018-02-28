package com.lf.view.tools.settings;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Handler;

/**
 * 设置的管理者，设置的值的存取，将每条设置进行关联
 * 
 * @author ww
 * 
 */
@SuppressLint("InlinedApi")
public class Settings {

	private SharedPreferences mSP; // 将设置存储到本地的工具
	private SharedPreferences mUpdateSp; // 将设置中需要更新提示的存储
	private SharedPreferences mSummarySP; // 将设置的摘要存储到本地的工具
	private static Settings mInstance;
	private final String TOTAL_UPDATE_NUMBER = "total_update_number"; // 一共更新的次数
	private Context mContext;

	// 通知设置更改的广播的Action的值
	public static final String ACTION_REFRESH = "action_settings_refresh";
	public static final String KEY_REFRESH = "key_settings_refresh";

	// 所有的设置数据
	private HashMap<String, BaseSetting> mSettings;

	private Settings(Context context) {
		mContext = context.getApplicationContext();
		String handSetInfo = android.os.Build.VERSION.RELEASE;
		if (handSetInfo.compareTo("3.0.0") > 0) {
			mSP = mContext.getSharedPreferences("settings", 
					Context.MODE_MULTI_PROCESS);
			mUpdateSp = mContext
					.getSharedPreferences("settings_update",
							Context.MODE_MULTI_PROCESS);
			mSummarySP = mContext.getSharedPreferences("settings_summary",
					Context.MODE_MULTI_PROCESS);
		} else {
			mSP = mContext.getSharedPreferences("settings", 0);
			mUpdateSp = mContext.getSharedPreferences("settings_update", 0);
			mSummarySP = mContext.getSharedPreferences("settings_summary", 0);
		}
		mSettings = SettingsLoader.load(context);
		revise();

		mContext.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				setBooleanSettingValue(SettingsConsts.SETTINGS_SCREEN_SWITCHER, true);
			}
		}, new IntentFilter(ResAction.SCREEN_SET_FINISH));

		// TIP：如果settings.data包下有扩展，一定要注意这里。在加入多进程后，为了使多进程中数据能同步，放弃了使用缓存
		// 同步一下数据。比如在第一次初始化的时候，mSP中尚未保存asset/setting.xml中的数据，数据不能保持同步
		if (mSettings != null) {
			Iterator<Entry<String, BaseSetting>> iterator = mSettings.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<String, BaseSetting> entry = iterator.next();
				String key = entry.getKey();
				BaseSetting baseSetting = entry.getValue();
				if (baseSetting instanceof BooleanSetting) {
					mSP.edit().putBoolean(key, ((BooleanSetting) baseSetting).getValue()).commit();
				} else if (baseSetting instanceof GroupSettings) {
					mSP.edit().putString(key, ((GroupSettings) baseSetting).getSaveValues())
					.commit();
				} else if (baseSetting instanceof IntSetting) {
					mSP.edit().putInt(key, ((IntSetting) baseSetting).getValue()).commit();
				} else if (baseSetting instanceof StringSetting) {
					mSP.edit().putString(key, ((StringSetting) baseSetting).getValue()).commit();
				}
			}
		}
	};

	/**
	 * 获取一个Settings的实例
	 * 
	 * @param context
	 * @return
	 */
	public static Settings getInstance(Context context) {
		if (null == mInstance)
			mInstance = new Settings(context);
		return mInstance;
	}

	/**
	 * 根据与设置对应的关键字，获取该条设置全部的信息
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @return
	 */
	public BaseSetting getSetting(String key) {
		if (mSettings.containsKey(key))
			return mSettings.get(key);
		return null;
	}

	/**
	 * 设置更新的条数
	 */
	private void addUpdateNum(String key) {
		if (mSettings.containsKey(key)) {
			if (mUpdateSp.getBoolean(key, false))
				return;// 默认值不会存储，只有值改变才存，且只会存false
			mUpdateSp.edit()
			.putInt(TOTAL_UPDATE_NUMBER, mUpdateSp.getInt(TOTAL_UPDATE_NUMBER, 0) + 1)
			.commit();
		}
	}

	/**
	 * 减少次数
	 */
	private void smallerUpdateNum() {
		int time = mUpdateSp.getInt(TOTAL_UPDATE_NUMBER, 0);
		time = time - 1 > 0 ? time - 1 : 0;
		mUpdateSp.edit().putInt(TOTAL_UPDATE_NUMBER, time).commit();
	}

	/**
	 * 获取一共有更新的次数
	 * 
	 * @return
	 */
	public int getUpdateNum() {
		return mUpdateSp.getInt(TOTAL_UPDATE_NUMBER, 0);
	}

	public Boolean getUpdateValue(String key) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			BooleanSetting setting = (BooleanSetting) mSettings.get(key);
			return setting.getNeedUpdate();
		}
		return null;
	}

	/**
	 * 修改需不需要更新的值
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @param value
	 *            true 需要显示new
	 */
	public void setBooleanUpdateValue(final String key,final boolean value) {
		if (mSettings.containsKey(key)) {
			// 先判断值是不是改变了,将true改成false
			if (mUpdateSp.getBoolean(key, false) && !value) {
				smallerUpdateNum();
			}

			BaseSetting setting = (BaseSetting) mSettings.get(key);
			setting.setNeedUpdate(value);

			mUpdateSp.edit().putBoolean(key, value).commit();
		}
	}

	/**
	 * 修改一条布尔型设置的值
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @param value
	 */
	public void setBooleanSettingValue(final String key,final boolean value) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			BooleanSetting setting = (BooleanSetting) mSettings.get(key);
			setting.setValue(value);

			mSP.edit().putBoolean(key, value).commit();

			Iterator<Map.Entry<String, BaseSetting>> iter = mSettings.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, BaseSetting> entry = (Map.Entry<String, BaseSetting>) iter.next();
				BaseSetting baseSetting = entry.getValue();

				if (baseSetting.getParentKey() != null && baseSetting.getParentKey().equals(key)) {
					baseSetting.setEnable(value);
					refreshSetting(baseSetting.getKey());
				}
			}
			refreshSetting(key);

		}
	}

	/**
	 * 获取一个布尔型设置（BooleanSetting）
	 * 
	 * @param key
	 * @return
	 */
	public Boolean getBooleanSettingValue(String key) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			//			BooleanSetting setting = (BooleanSetting) mSettings.get(key);
			// return setting.getValue();
			//			SharedPreferences sp = mContext.getSharedPreferences("settings", 0);
			return getSharedPreferences().getBoolean(key, false);
		}
		return null;
	}

	/**
	 * 修改一条单选设置的值
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @param value
	 */
	public void setIntSettingValue(final String key,final int value) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			IntSetting setting = (IntSetting) mSettings.get(key);
			setting.setValue(value);

			mSP.edit().putInt(key, value).commit();
			refreshSetting(key);

		}
	}

	/**
	 * 获取组件的单项里面存储值
	 * 
	 * @param key
	 * @return
	 */
	public String getGroupItemValue(String key) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			//			GroupSettings setting = (GroupSettings) mSettings.get(key);
			//			SharedPreferences sp = mContext.getSharedPreferences("settings", 0);
			return getSharedPreferences().getString(key, "");
		}
		return null;
	}

	/**
	 * 往组件的单项里面存储值
	 * 
	 * @param key
	 * @param value
	 */
	public void setGroupItemValues(final String key,final String value) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			GroupSettings setting = (GroupSettings) mSettings.get(key);
			setting.setSaveValues(value);

			mSP.edit().putString(key, value).commit();
			refreshSetting(key);
		}
	}

	/**
	 * 获取一个布尔型设置（BooleanSetting）
	 * 
	 * @param key
	 * @return
	 */
	public String getStringSettingValue(String key) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			//			StringSetting setting = (StringSetting) mSettings.get(key);
			//			// return setting.getValue();
			//			String handSetInfo = android.os.Build.VERSION.RELEASE;
			//			SharedPreferences sp;
			//			if (handSetInfo.compareTo("2.3.0") > 0) {
			//				sp = mContext.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS);
			//			} else {
			//				sp = mContext.getSharedPreferences("settings", 0);
			//			}
			return getSharedPreferences().getString(key, "");
		}
		return null;
	}

	/**
	 * 修改一条String设置的值
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @param value
	 */
	public void setStringSettingValue(final String key,final String value) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			StringSetting setting = (StringSetting) mSettings.get(key);
			setting.setValue(value);

			mSP.edit().putString(key, value).commit();
			refreshSetting(key);
			//			getSharedPreferences().edit().putString(key, value).commit();
			//			refreshSetting(key);
		}
	}

	/**
	 * 获取一个布尔型设置（BooleanSetting）
	 * 
	 * @param key
	 * @return
	 */
	public int getIntSettingValue(String key) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			//			IntSetting setting = (IntSetting) mSettings.get(key);
			//			// return setting.getValue();
			//
			//			SharedPreferences sp = mContext.getSharedPreferences("settings", 0);
			return getSharedPreferences().getInt(key, 0);
		}
		return 0;
	}

	/**
	 * 修改一条设置的摘要，通常设置的摘要可以通过设置的值获取 但有些设置的值与摘要的关系没法通过SDK或者配置表述，所以 在扩展的一些界面中
	 * 
	 * @param key
	 *            与设置对应的关键字
	 * @param value
	 */
	public void setSummary(String key, String summary) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			BaseSetting setting = mSettings.get(key);
			setting.setSummary(summary);
			mSummarySP.edit().putString(key, summary).commit();
		}
	}

	/**
	 * 修改一条设置是否有效
	 * 
	 * @param key
	 * @param enable
	 */
	public void setEnable(String key, boolean enable) {
		// 检查是否有该项设置，有该项设置，才进行操作
		if (mSettings.containsKey(key)) {
			BaseSetting setting = mSettings.get(key);
			setting.setEnable(enable);
			refreshSetting(key);
		}
	}

	/**
	 * 刷新设置
	 * 
	 * @param key
	 *            更新了哪一项设置
	 */
	private void refreshSetting(final String key) {
		
		/*-- 陆德元（15.3.11） 推迟通知刷新：如果立即通知，有可能正在改变Sha，
		 * 就会造成不同process数据不同步（即使用了MODE_PRIVATE）-- */
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// 发送设置更新的广播
				Intent intent = new Intent();
				intent.setAction(ACTION_REFRESH);
				intent.putExtra(KEY_REFRESH, key);
				mContext.sendBroadcast(intent);
			}
		},200);
		
		
	}

	/**
	 * 修正所有设置（可用状态、摘要）
	 */
	private void revise() {
		Iterator<Map.Entry<String, BaseSetting>> iter = mSettings.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, BaseSetting> entry = (Map.Entry<String, BaseSetting>) iter.next();
			BaseSetting baseSetting = entry.getValue();

			// 从本地读取摘要
			String summary = mSummarySP.getString(baseSetting.getKey(), null);
			baseSetting.setSummary(summary);

			// 修正可用状态
			if (baseSetting.getParentKey() != null) {
				BooleanSetting parent = (BooleanSetting) getSetting(baseSetting.getParentKey());
				baseSetting.setEnable(parent.getValue());
			}

			// 修改更新的次数
			boolean update = baseSetting.getNeedUpdate();
			if (update) {
				addUpdateNum(baseSetting.getKey());
				setBooleanUpdateValue(baseSetting.getKey(), true);
			}
		}
	}

	/**
	 * 获取SharedPreferences 因为跨进程调用，所以后缀必须是Context.MODE_MULTI_PROCESS
	 * 注意：每次获取数据的时候读需要重新getSharedPreferences，不然数据不会同步
	 * @return
	 */
	private SharedPreferences getSharedPreferences(){
		SharedPreferences sp=null;
		//		String handSetInfo = android.os.Build.VERSION.RELEASE;
		if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
			//		if (handSetInfo.compareTo("3.0.0") > 0) {
			try {
				Context context=mContext.createPackageContext(mContext.getPackageName(),  
						Context.CONTEXT_IGNORE_SECURITY);
				sp = context.getSharedPreferences("settings", Context.MODE_MULTI_PROCESS);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			} 
			if(sp==null){
				sp = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
			}
		}else{
			sp = mContext.getSharedPreferences("settings", Context.MODE_PRIVATE);
		}
		return sp;
	}

}