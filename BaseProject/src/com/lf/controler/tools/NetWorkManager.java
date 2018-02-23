package com.lf.controler.tools;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 网络工具。 需要的权限<code>android.permission.CHANGE_WIFI_STATE</code>
 * <code>android.permission.ACCESS_WIFI_STATE</code>
 * <code>android.permission.CHANGE_NETWORK_STATE</code>
 * <code>android.permission.ACCESS_NETWORK_STATE</code>
 * 
 * @author LinChen
 *
 */
public class NetWorkManager {

	/**
	 * 其他网络类型
	 */
	public static final int TYPE_OTHER = -1;
	/**
	 * 没有连接网络
	 */
	public static final int TYPE_DISCONNECT = 0;
	/**
	 * 手机网络
	 */
	public static final int TYPE_MOBILE = 1;
	/**
	 * wifi网络
	 */
	public static final int TYPE_WIFI = 2;

	public static NetWorkManager instance;

	private Context context;
	private BroadcastReceiver broadcastReceiver;
	private List<IWifiListener> wifiListeners;
	private List<IMobileDataListener> mobileDataListeners;

	public static NetWorkManager getInstance(Context context) {
		if (instance == null) {
			instance = new NetWorkManager(context);
		}
		return instance;
	}

	private NetWorkManager(Context context) {
		this.context = context;

		wifiListeners = new LinkedList<IWifiListener>();
		mobileDataListeners = new LinkedList<IMobileDataListener>();

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.RSSI_CHANGED_ACTION);

		broadcastReceiver = new InnerReceiver();

		context.getApplicationContext().registerReceiver(broadcastReceiver, filter);
	}

	/**
	 * 是否联网
	 * 
	 * @return
	 */
	public boolean isConnect() {
		NetworkInfo networkInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnectedOrConnecting());
	}

	/**
	 * 获取网络类型，返回值为NetWorkManager.TYPE_XXX
	 * 
	 * @return  NetWorkManager.TYPE_OTHER : 其他网络类型
	 * 			NetWorkManager.TYPE_DISCONNECT : 没有连接网络
	 * 			NetWorkManager.TYPE_MOBILE : 手机网络
	 * NetWorkManager.TYPE_WIFI : wifi网络
	 */
	public int getConType() {
		NetworkInfo networkInfo = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isAvailable()) {
			return TYPE_DISCONNECT;
		} else {
			String type = networkInfo.getTypeName();
			if ("wifi".equalsIgnoreCase(type)) {
				return TYPE_WIFI;
			} else if ("mobile".equalsIgnoreCase(type)) {
				return TYPE_MOBILE;
			} else {
				return TYPE_OTHER;
			}
		}
	}
	
	/**
	 * 获取wifi的状态，如果是0则表示wifi关闭，如果>0则返回wifi的信号强度
	 * @return
	 */
	public int getWifiStatue() {
		WifiManager wm = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			WifiInfo info = wm.getConnectionInfo();
			if (info.getBSSID() != null) {
				// 如果wifi是连接状态，获取wifi信号强度
				return WifiManager.calculateSignalLevel(info.getRssi(), 4);
			}
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 手机移动网络是否打开
	 * 
	 * @return
	 */
	public boolean isMobileDataOn() {
		boolean result = false;

		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method getMobileDataEnabled = null; // setMobileDataEnabled方法
		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			getMobileDataEnabled = iConMgrClass.getDeclaredMethod("getMobileDataEnabled");
			// 设置setMobileDataEnabled方法可访问
			getMobileDataEnabled.setAccessible(true);
			// 调用setMobileDataEnabled方法
			result = (Boolean) getMobileDataEnabled.invoke(iConMgr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 打开移动网络
	 */
	public void doMobileDataOn() {
		toggleMobileData(context, true);
	}

	/**
	 * 关闭移动网络
	 */
	public void doMobileDataOff() {
		toggleMobileData(context, false);
	}

	/**
	 * 移动网络开关
	 */
	private void toggleMobileData(Context context, boolean enabled) {
		ConnectivityManager conMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		Class<?> conMgrClass = null; // ConnectivityManager类
		Field iConMgrField = null; // ConnectivityManager类中的字段
		Object iConMgr = null; // IConnectivityManager类的引用
		Class<?> iConMgrClass = null; // IConnectivityManager类
		Method setMobileDataEnabledMethod = null; // setMobileDataEnabled方法
		try {
			// 取得ConnectivityManager类
			conMgrClass = Class.forName(conMgr.getClass().getName());
			// 取得ConnectivityManager类中的对象mService
			iConMgrField = conMgrClass.getDeclaredField("mService");
			// 设置mService可访问
			iConMgrField.setAccessible(true);
			// 取得mService的实例化类IConnectivityManager
			iConMgr = iConMgrField.get(conMgr);
			// 取得IConnectivityManager类
			iConMgrClass = Class.forName(iConMgr.getClass().getName());
			// 取得IConnectivityManager类中的setMobileDataEnabled(boolean)方法
			setMobileDataEnabledMethod = iConMgrClass.getDeclaredMethod("setMobileDataEnabled",
					Boolean.TYPE);
			// 设置setMobileDataEnabled方法可访问
			setMobileDataEnabledMethod.setAccessible(true);
			// 调用setMobileDataEnabled方法
			setMobileDataEnabledMethod.invoke(iConMgr, enabled);
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (IMobileDataListener listener : mobileDataListeners) {
			listener.onSwitch(enabled);
			listener.onStateChange(enabled ? 1 : 0);
		}
	}

	/**
	 * 获取Wifi信号，返回0时说明Wifi为关，大于0时，为Wifi的信号强度
	 * 
	 * @return
	 */
	public int getWifiSignal() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			WifiInfo info = wm.getConnectionInfo();
			if (info.getBSSID() != null) {
				// 如果wifi是连接状态，获取wifi信号强度
				return WifiManager.calculateSignalLevel(info.getRssi(), 4);
			}
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * 检查Wifi是否为开，注意：Wifi开着不代表有网
	 * 
	 * @return
	 */
	public boolean isWifiOn() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 打开Wifi
	 * 
	 * @return 操作是否成功
	 */
	public boolean doWifiOn() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		boolean result = wm.setWifiEnabled(true);

		if (result) {
			for (IWifiListener wifiListener : wifiListeners) {
				wifiListener.onSwitch(true);
			}
		}

		return result;
	}

	/**
	 * 关闭Wifi
	 * 
	 * @return 操作是否成功
	 */
	public boolean doWifiOff() {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		boolean result = wm.setWifiEnabled(false);

		if (result) {
			for (IWifiListener wifiListener : wifiListeners) {
				wifiListener.onSwitch(false);
			}
		}

		return result;
	}

	/**
	 * ping百度，会阻塞
	 * 
	 * @return
	 */
	public boolean ping() {
		return ping("www.baidu.com");
	}

	/**
	 * ping网络是否连通，会阻塞
	 * 
	 * @param url
	 * @return
	 */
	public boolean ping(String url) {

		if (url.startsWith("http")) {// http开头的话，就把http://去掉
			url = url.substring(7, url.length());
		}

		boolean result = false;
		try {
			Process one = Runtime.getRuntime().exec("ping -c 1 -w 5 " + url);
			int one_status = one.waitFor();
			if (one_status == 0) {
				result = true;
			} else {
				result = false;
			}
		} catch (Exception e) {
		}
		return result;
	}

	/**
	 * 添加Wifi监听，注意添加后需要注销
	 * 
	 * @param listener
	 */
	public void addWifiListener(IWifiListener listener) {
		wifiListeners.add(listener);
	}

	/**
	 * 注销Wifi监听
	 * 
	 * @param listener
	 */
	public void removeWifiListener(IWifiListener listener) {
		wifiListeners.remove(listener);
	}

	/**
	 * 添加监听，注意添加后需要注销
	 * 
	 * @param listener
	 */
	public void addMobileDataListener(IMobileDataListener listener) {
		mobileDataListeners.add(listener);
	}

	/**
	 * 注销监听
	 * 
	 * @param listener
	 */
	public void removeMobileDataListener(IMobileDataListener listener) {
		mobileDataListeners.remove(listener);
	}

	/**
	 * 监听网络
	 * 
	 * @author LinChen
	 *
	 */
	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

				if (isWifiOn()) {
					boolean isConn = isConnect();
					for (IWifiListener listener : wifiListeners) {
						if (!isConn) {
							listener.onStateChange(-1);
						}
						listener.onSwitch(true);
					}
				} else {
					for (IWifiListener listener : wifiListeners) {
						listener.onStateChange(0);
						listener.onSwitch(false);
					}
				}
			} else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
				WifiManager wifiManager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				int value = 0;
				try {
					WifiInfo info = wifiManager.getConnectionInfo();
					if (info.getBSSID() != null) {
						// 如果wifi是连接状态，获取wifi信号强度
						value = WifiManager.calculateSignalLevel(info.getRssi(), 4) + 1;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					for (IWifiListener listener : wifiListeners) {
						listener.onStateChange(value);
					}
				}
			}
		}
	}

	/**
	 * Wifi监听
	 * 
	 * @author LinChen
	 *
	 */
	public interface IWifiListener {
		/**
		 * 工具开关的监听
		 * 
		 * @param isOpen
		 */
		public void onSwitch(boolean isOpen);

		/**
		 * 工具状态的监听
		 * 
		 * @param state
		 */
		public void onStateChange(int state);
	}

	/**
	 * 手机移动网络监听
	 * 
	 * @author LinChen
	 *
	 */
	public interface IMobileDataListener {
		/**
		 * 工具开关的监听
		 * 
		 * @param isOpen
		 */
		public void onSwitch(boolean isOpen);

		/**
		 * 工具状态的监听
		 * 
		 * @param state
		 */
		public void onStateChange(int state);
	}

}
