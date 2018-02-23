package com.lf.controler.tools.location;

import android.content.Context;
import android.content.Intent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;


/**
 * 定位工具，对百度定位进行了封装
 * @author wangwei
 *
 */
public class Location {

	private LocationClient mLocClient;
	private static Location mInstance;
	private int mStatus = STATUS_STOP;//当前的定位状态，0停止，1暂停，2运行
	public final static int STATUS_STOP = 0;//停止
	public final static int STATUS_PAUSE = 1;//暂停
	public final static int STATUS_RUN = 2;//运行
	public BDLocation mLocation; 
	private Context mContext;

	public static Location getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new Location(context.getApplicationContext());
		}
		return mInstance;
	}

	private Location(final Context context) {
		mContext = context;
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(mLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000*60*60*5);
		option.setIsNeedAddress(true);
		mLocClient.setLocOption(option);
	}


	/**
	 * 开始定位
	 * 
	 * @param context
	 */
	public void start() {

		if(mStatus == STATUS_RUN)
			return;
		mStatus = STATUS_RUN;
		mLocClient.start();
	}



	/**
	 * 位置信息的监听
	 */
	BDLocationListener mLocationListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {

			mStatus = STATUS_STOP;
			if(null != location /*|| null == location.getAddress() || null == location.getAddress().city*/)
			{
				String lat = location.getLatitude() + "";
				if(!"4.9E-324".equals(lat))//没被定位到，被限定了权限
				{
					mLocation = location;	
				}
			}
			mContext.sendBroadcast(new Intent(getAction()));
		}
	};


	public String getAction()
	{
		return mContext.getPackageName() + ".location";
	}
}
