package com.lf.view.tools.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;


/**
 * 设置跳转activity的基类
 * @author ww
 *
 */
public class BaseSettingActivity<T extends BaseSetting> extends Activity{
	
	private T mSetting;	//设置内容
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initSetting();
	}
	
	
	/**
	 * 获取设置
	 * @return
	 */
	public T getSetting()
	{
		return mSetting; 
	}
	
	
	/**
	 * 设置设置
	 * @param setting
	 */
	private void setSetting(T setting)
	{
		mSetting = setting;
	}

	
	/**
	 * 初始化设置数据
	 * @param attrs 通过获取在布局文件中的属性，来得到与本布局关联的设置的key，从而获取设置内容
	 */
	@SuppressWarnings("unchecked")
	public void initSetting()
	{
		Intent intent = getIntent();
        String key = intent.getStringExtra(BaseSetting.ATTR_KEY);	
		T setting = (T)Settings.getInstance(this).getSetting(key);
		setSetting(setting);
	}
}