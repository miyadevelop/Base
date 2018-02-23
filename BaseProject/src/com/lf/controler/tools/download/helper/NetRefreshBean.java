package com.lf.controler.tools.download.helper;

import android.content.Context;

/**
 * 定义加载网络数据时，需要用到数据格式
 * @author ludeyuan
 *
 */
public class NetRefreshBean {
	
	private NetEnumRefreshTime mRefreshTime;	//刷新的周期
	
	private int mTimeValue;					//按照时间刷新的间隔时长，用于按时间刷新
	
	private String mContextAddress;				//Context的地址，表示Activity没销毁，则不需要更新
	
	
	/**
	 * 把刷新的依据（刷新周期）传递进来
	 */
	public NetRefreshBean(NetEnumRefreshTime refreshTime){
		mRefreshTime = refreshTime;
	}
	
	/**
	 * 如果按照Context来刷新，调用这个方法
	 */
	public void setContext(Context context){
		mContextAddress = context.toString();
	}
	
	/**
	 * 如果按照时间的间隔刷新，必须调用这个方法
	 * @param value 间隔的值。例如：按照2天数刷新一次，这里传2
	 */
	public void setTimeValue(int value){
		mTimeValue = value;
	}
	
	/**
	 * 返回周期更新的值
	 * @return NetEnumRefreshTime类型
	 */
	public NetEnumRefreshTime getNetEnumRefreshTime(){
		return mRefreshTime;
	}
	
	/**
	 * 如果按照Context来刷新，返回Context的地址
	 * @return
	 */
	public String getContextAddres(){
		return mContextAddress;
	}
	
	/**
	 * 如果按照时间的间隔刷新，返回间隔的值
	 * @return 例如：按照2天数刷新一次，这里返回2
	 */
	public int getTimeValue(){
		return mTimeValue;
	}
}
