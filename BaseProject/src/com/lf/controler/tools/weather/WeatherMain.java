package com.lf.controler.tools.weather;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

/**
 * 天气对外接口
 * 
 * @author yzx
 * 
 */
public class WeatherMain {
	private WeatherLoader mWeatherLoader;
	LocationClient mLocClient;
	private static WeatherMain mInstance;
	private int mStatus = STATUS_STOP;//当前的定位状态，0停止，1暂停，2运行
	public final static int STATUS_STOP = 0;//停止
	public final static int STATUS_PAUSE = 1;//暂停
	public final static int STATUS_RUN = 2;//运行
	
	public static WeatherMain getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new WeatherMain(context.getApplicationContext());
		}
		return mInstance;
	}

	private WeatherMain(final Context context) {

		mWeatherLoader = new WeatherLoader(context);
		mLocClient = new LocationClient(context);
        mLocClient.registerLocationListener(mLocationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000*60*60*5);
        option.setIsNeedAddress(true);
//        option.setIsNeedLocationDescribe(true);
        mLocClient.setLocOption(option);
	}

	
	/**
	 * 开始定位
	 * 
	 * @param context
	 */
	public void getLocationStartDefine(Context context) {
		
		if(mStatus == STATUS_RUN)
			return;
		mStatus = STATUS_RUN;
		mLocClient.start();
	}

	
	/**
	 * 获取天气信息
	 * 
	 * @param context
	 * @return
	 */
	public Weather getWeather(Context context) {
		return mWeatherLoader.getWeather();
	}

	
	/**
	 * 位置信息的监听
	 */
	BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {

			mStatus = STATUS_STOP;
			
			if(null == location || null == location.getAddress() || null == location.getAddress().city)
				return;

			String lat = location.getLatitude() + "";
			if("4.9E-324".equals(lat))//没被定位到，被限定了权限
			{
				return;
			}
			
			mWeatherLoader.refreshWeather(location.getAddress().city);
			
		}
	};
	
	
	public String getAction()
	{
		return mWeatherLoader.getAction();
	}
	
	
	public Weather getWeather()
	{
		return mWeatherLoader.getWeather();
	}
}
