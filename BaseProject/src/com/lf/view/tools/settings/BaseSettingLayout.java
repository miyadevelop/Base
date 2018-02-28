package com.lf.view.tools.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * 设置布局基类
 *
 * @param <T>
 * @author ww
 */
public abstract class BaseSettingLayout<T extends BaseSetting> extends RelativeLayout{

	private T mSetting;//设置内容
	public final static String ATTR_KEY = "settings_key";//在布局文件中表示设置的key的属性名称（namespace）

	public BaseSettingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//获取设置数据
		initSetting(attrs);

		//初始化控件
		initView();
	}
	
	
	public BaseSettingLayout(Context context, T setting) {
		super(context);
		
		//获取设置数据
		setSetting(setting);

		//初始化控件
		initView();
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
	 * 收到某一项设置改变后，判断是否是与本布局相关的设置改变了，如果是，调用刷新
	 * @param key 设置的key
	 */
	public void onSettingRefresh(String key)
	{
		String selfKey = getSetting().getKey();
		if(null != selfKey &&  selfKey.equals(key))
		{
			onSelfRefresh();
		}
	}

	
	/**
	 * 初始化设置数据
	 * @param attrs 通过获取在布局文件中的属性，来得到与本布局关联的设置的key，从而获取设置内容
	 */
	@SuppressWarnings("unchecked")
	public void initSetting(AttributeSet attrs)
	{
		String settingKey = attrs.getAttributeValue(null, ATTR_KEY);	
		T setting = (T)Settings.getInstance(getContext()).getSetting(settingKey);
		setSetting(setting);
	}
	
	
	/**
	 * 在这里初始化布局，往布局里添加子控件
	 */
	public abstract void initView();
	
	
	/**
	 * 当与自己关联的设置改变后，会自动调用这里
	 * 可以在这里添加一些刷新控件显示的代码
	 */
	public abstract void onSelfRefresh();
	
	/**
	 * 设置标题
	 */
	public abstract void setTitle(String title);
	
	
	/**
	 * 设置标题下面的说明文字
	 */
	public abstract void setSummary(String summary);
	
}
