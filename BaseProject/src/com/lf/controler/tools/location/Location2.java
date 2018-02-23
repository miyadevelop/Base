package com.lf.controler.tools.location;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * 定位工具，对系统的定位接口封装
 * @author wangwei
 *
 */
public class Location2 {

	private static Location2 mInstance;
	private int mStatus = STATUS_STOP;//当前的定位状态，0停止，1暂停，2运行
	public final static int STATUS_STOP = 0;//停止
	public final static int STATUS_PAUSE = 1;//暂停
	public final static int STATUS_RUN = 2;//运行
	public Location mLocation; 
	public Address mAddress;
	private Context mContext;

	public static Location2 getInstance(Context context) {
		if (null == mInstance) {
			mInstance = new Location2(context.getApplicationContext());
		}
		return mInstance;
	}

	private Location2(final Context context) {
		mContext = context;
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

		LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
		//		LocationListener locationListener=new LocationListener() {
		//
		//			public void onLocationChanged(Location location) {
		//				// Called whena new location is found by the network location provider.
		//				//makeUseOfNewLocation(location);
		//				if(null != location)
		//					mLocation = location;
		//				getAddrByhandler(location);
		//				mContext.sendBroadcast(new Intent(getAction()));
		//				mStatus = STATUS_STOP;
		//			}
		//
		//			public void onStatusChanged(String provider,int status, Bundle extras) {
		//			}
		//
		//			public void onProviderEnabled(String provider) {
		//			}
		//
		//			public void onProviderDisabled(String provider) {
		//				//定位失败
		//				mStatus = STATUS_STOP;
		//			}
		//		};
		//		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, 0,locationListener);
		try {
			Location location =locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			if(null != location)
			{
				mLocation = location;
				getAddrByhandler(location);
			}
		}catch (Exception e)
		{
			e.printStackTrace();
		}

		mContext.sendBroadcast(new Intent(getAction()));
		mStatus = STATUS_STOP;
	}



	public String getAction()
	{
		return mContext.getPackageName() + ".location";
	}


	private void getAddrByhandler(Location location){

		Geocoder geocoder = new Geocoder(mContext,Locale.getDefault());
		List<Address>addresses = null;
		try{
			addresses =geocoder.getFromLocation(location.getLatitude(),
					location.getLongitude(),1);
		} catch (IOException ioException) {
		} catch (IllegalArgumentException illegalArgumentException) {
		}

		if (addresses == null ||addresses.size()  == 0) {
		} else {
			mAddress = addresses.get(0);
		}
	}
}
