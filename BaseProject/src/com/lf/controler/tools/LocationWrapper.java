package com.lf.controler.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * 集成了百度定位</br> 相关jar包：locSDK_5.01.jar</br> so：liblocSDK5.so</br>
 * 
 * <pre>
 * 			<service
 *             android:name="com.baidu.location.f"
 *             android:enabled="true"
 *             android:process=":remote" >
 *             <intent-filter>
 *                 <action android:name="com.baidu.location.service_v2.2" >
 *                 </action>
 *             </intent-filter>
 *         </service>
 * 
 *         <!-- 填写申请的AK值 -->
 *         <meta-data
 *             android:name="com.baidu.lbsapi.API_KEY"
 *             android:value="6qEYI7mw7ThhnXctUOIw7VaG" />
 * </pre>
 * 
 * @author LinChen
 *
 */
public class LocationWrapper implements BDLocationListener {
	/**
	 * 当请求获取位置成功后发送的广播
	 */
	public static final String ACTION_GET_LOCATION = "com.wocao.ACTION_GET_LOCATION";

	/**
	 * 当请求获取位置失败后发送的广播
	 */
	public static final String ACTION_GET_LOCATION_FAIL = "com.wocao.ACTION_GET_LOCATION_FAIL";

	private static LocationWrapper instance;

	private Context context;
	private LocationClient client;
	private LocationMsg locationMsg;
	private List<ILocationListener> listeners;
	private Handler handler;

	private LocationWrapper(Context context) {
		this.context = context.getApplicationContext();
		listeners = Collections
				.synchronizedList(new ArrayList<LocationWrapper.ILocationListener>());
		client = new LocationClient(context, getClientOption());
		client.registerLocationListener(this);
		handler = new Handler(Looper.getMainLooper());
	}

	public static LocationWrapper getInstance(Context context) {
		if (instance == null) {
			instance = new LocationWrapper(context);
		}
		return instance;
	}

	/**
	 * 希望这个方法能够动态的获取位置参数
	 * 
	 * @return
	 */
	private LocationClientOption getClientOption() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Battery_Saving);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02。当设置bd09ll时能无缝对接百度地图
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		return option;
	}

	/**
	 * 获取位置信息
	 * 
	 * @return
	 */
	public LocationMsg getLocationMsg() {
		return locationMsg;
	}

	/**
	 * 添加一个位置的监听。用于长期的监听，注意销毁
	 * 
	 * @param locationListener
	 */
	public void registerLocationListener(ILocationListener locationListener) {
		synchronized (listeners) {
			listeners.add(locationListener);
		}
	}

	/**
	 * 添加一个位置的监听。只用来监听一次，监听结束后自动销毁
	 * 
	 * @param locationListener
	 */
	public void registerLocationListenerOnce(ILocationListener locationListener) {
		synchronized (listeners) {
			locationListener.isOnce = true;
			listeners.add(locationListener);
		}
	}

	/**
	 * 注销一个位置监听。
	 * 
	 * @param locationListener
	 */
	public void unRegisterLocationListener(ILocationListener locationListener) {
		synchronized (listeners) {
			listeners.remove(locationListener);
		}
	}

	/**
	 * 百度获取位置后的回调
	 */
	@Override
	public void onReceiveLocation(BDLocation location) {
		LocationMsg msg = new LocationMsg(location);
		if (location.getLocType() >= 502) {
			/**
			 * 总结就是app配置文件中com.baidu.lbsapi.API_KEY没有配置正确 <br>
			 * 502：key参数错误 <br>
			 * 505：key不存在或者非法 <br>
			 * 601：key服务被开发者自己禁用 <br>
			 * 602：key mcode不匹配 <br>
			 * 501～700：key验证失败
			 */
			msg.isOK = false;
		} else if (location.getLocType() == BDLocation.TypeGpsLocation
				|| location.getLocType() == BDLocation.TypeNetWorkLocation
				|| location.getLocType() == BDLocation.TypeCacheLocation
				|| location.getLocType() == BDLocation.TypeOffLineLocation
				|| location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail) {
			/**
			 * 61 ： GPS定位结果<br>
			 * 65 ： 定位缓存的结果。<br>
			 * 66 ： 离线定位结果。通过requestOfflineLocaiton调用时对应的返回结果<br>
			 * 68 ： 网络连接失败时，查找本地离线定位时对应的返回结果<br>
			 * 161： 表示网络定位结果\n
			 */
			msg.isOK = true;
		} else {
			/**
			 * 各种的失败...<br>
			 * 62 ： 扫描整合定位依据失败。此时定位结果无效。<br>
			 * 63 ： 网络异常，没有成功向服务器发起请求。此时定位结果无效。<br>
			 * 67 ： 离线定位失败。通过requestOfflineLocaiton调用时对应的返回结果<br>
			 * 162~167： 服务端定位失败
			 */
			msg.isOK = false;
		}
		client.stop();
		locationMsg = msg;

		// 发送广播
		if (msg.isOK) {
			context.sendBroadcast(new Intent(ACTION_GET_LOCATION));
		} else {
			context.sendBroadcast(new Intent(ACTION_GET_LOCATION_FAIL));
		}

		ArrayList<ILocationListener> onceListeners = new ArrayList<ILocationListener>();
		// 唤起回调
		for (ILocationListener listener : listeners) {
			if (listener.isOnce) {
				onceListeners.add(listener);
			}
			listener.onReceiveLocation(msg);
		}
		// 注销一次性监听
		for (ILocationListener listener : onceListeners) {
			unRegisterLocationListener(listener);
		}
	}

	/**
	 * 准备好监听后，请求刷新位置信息。成功和失败都将会有回调。同时，成功后会发送ACTION_GET_LOCATION广播，失败后发送ACTION_GET_LOCATION_FAIL广播
	 */
	public void requestLocationMsg() {
		client.start();
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				synchronized (listeners) {
					ArrayList<ILocationListener> moveListeners = new ArrayList<ILocationListener>();
					for (ILocationListener listener : listeners) {
						if ((System.currentTimeMillis() - listener.requestTime) > 5500) {
							LocationMsg msg = new LocationMsg();
							msg.setOK(false);
							listener.onReceiveLocation(msg);
							moveListeners.add(listener);
						}
					}
					for (ILocationListener temp : moveListeners) {
						unRegisterLocationListener(temp);
					}
				}
			}
		}, 6000l);
	}

	/**
	 * 位置刷新的监听
	 * 
	 * @author LinChen,code when 2015年1月28日
	 */
	public static abstract class ILocationListener {

		public ILocationListener() {
			requestTime = System.currentTimeMillis();
		}

		long requestTime;

		boolean isOnce = false;

		public abstract void onReceiveLocation(LocationMsg msg);
	}

	/**
	 * 位置信息的包装
	 * 
	 * @author LinChen,code when 2015年1月28日
	 */
	public class LocationMsg {
		/**
		 * 返回的位置信息是否有效
		 */
		private boolean isOK;
		/**
		 * 获取位置信息的时间
		 */
		private String time;
		/**
		 * 维度
		 */
		private double latitude = 404d;
		/**
		 * 经度
		 */
		private double longitude = 404d;
		/**
		 * 定位精度，半径为米
		 */
		private float radius;
		/**
		 * 反地理编码，粗略的地址信息
		 */
		private String address;
		/**
		 * 省份
		 */
		private String province;
		/**
		 * 城市
		 */
		private String city;
		/**
		 * 区县
		 */
		private String district;

		private LocationMsg() {
		}

		private LocationMsg(BDLocation bdLocation) {
			time = bdLocation.getTime();
			latitude = bdLocation.getLatitude();
			longitude = bdLocation.getLongitude();
			radius = bdLocation.getRadius();
			address = bdLocation.getAddrStr();
			province = bdLocation.getProvince();
			city = bdLocation.getCity();
			district = bdLocation.getDistrict();
		}

		public boolean isOK() {
			return isOK;
		}

		public void setOK(boolean isOK) {
			this.isOK = isOK;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public double getLatitude() {
			return latitude;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public double getLongitude() {
			return longitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public float getRadius() {
			return radius;
		}

		public void setRadius(float radius) {
			this.radius = radius;
		}

		public String getAddress() {
			return address;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public String getProvince() {
			return province;
		}

		public void setProvince(String province) {
			this.province = province;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getDistrict() {
			return district;
		}

		public void setDistrict(String district) {
			this.district = district;
		}

	}
}
