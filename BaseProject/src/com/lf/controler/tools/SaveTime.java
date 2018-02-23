package com.lf.controler.tools;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.SystemClock;

/**
 * 时间工具
 * 
 * @author LinChen
 *
 */
public class SaveTime {
	private static SaveTime instance;
	private long netTime;
	private Context context;
	private AsynEventProcess aep;
	/**
	 * 是否是测试模式
	 */
	private boolean isTestMode = false;

	private SaveTime(Context context) {
		this.context = context.getApplicationContext();
		aep = new AsynEventProcess("trackTime");
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		this.context.registerReceiver(new InnerReceiver(), filter);
		if (NetWorkManager.getInstance(context).isConnect())// 初始化时间，注册广播后并不会立即获取网络时间
		{
			aep.add(new Runnable() {

				@Override
				public void run() {
					adjustTime();
				}
			});

		}
	}

	/**
	 * 是否是测试模式
	 * 
	 * @return
	 */
	public boolean isTestMode() {
		return isTestMode;
	}

	/**
	 * 设置测试模式，当设置为true时，获取到的时候都为本地时间
	 * 
	 * @param isTestMode
	 */
	public void setTestMode(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}

	public synchronized static SaveTime getInstance(Context context) {
		if (instance == null) {
			instance = new SaveTime(context);
		}
		return instance;
	}

	/**
	 * 获取当前时间，如果获取到了网络时间，则返回网络时间，没有就返回本地时间。如果是测试模式，返回本地时间
	 * 
	 * @return
	 */
	public long currentTimeMillis() {
		if (isTestMode) {
			return System.currentTimeMillis();
		} else {
			if (netTime == 0l) {
				return System.currentTimeMillis();
			} else {
				return netTime;
			}
		}
	}

	/**
	 * 格式化网络时间
	 * 
	 * @param format
	 *            例如YYYYMMdd
	 * @return 如果没有网络时间，返回null
	 */
	public String formatNetTime(String format) {
		if (isTestMode) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(new Date(netTime));
		} else {
			if (netTime == 0l) {
				return null;
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.format(new Date(netTime));
			}
		}
	}

	/**
	 * 格式化当前时间
	 * 
	 * @param format
	 *            例如YYYYMMdd
	 * @return
	 */
	public String formatCurrentTime(String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(new Date(currentTimeMillis()));
	}

	/**
	 * 校准时间
	 */
	private void adjustTime() {
		try {
			URL url = new URL("http://www.baidu.com");// 取得资源对象
			URLConnection uc = url.openConnection();// 生成连接对象
			uc.connect(); // 发出连接
			netTime = uc.getDate(); // 取得网站日期时间
			uc.getInputStream().close();

			// 对时间国际化处理
			TimeZone inZone = TimeZone.getTimeZone("Asia/Shanghai");// 这是从http://www.baidu.com上获取的服务器时区
			TimeZone localZone = TimeZone.getDefault();
			long timeOffset = inZone.getRawOffset() - localZone.getRawOffset();// 时差
			netTime -= timeOffset;

			// 跟踪时间
			aep.add(new Runnable() {

				@Override
				public void run() {
					if (netTime != 0l) {
						while (true) {
							netTime += 1000l;
							SystemClock.sleep(1000l);
						}
					}
				}
			});
		} catch (Exception e) {
		}
	}

	/**
	 * 用于更新服务器时间
	 * 
	 * @author LinChen
	 *
	 */
	class InnerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (netTime == 0l) {
				aep.add(new Runnable() {

					@Override
					public void run() {
						adjustTime();
					}
				});
			}
		}
	}
}