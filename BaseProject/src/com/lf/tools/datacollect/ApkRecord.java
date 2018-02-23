package com.lf.tools.datacollect;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * apk的启动次数，初始值为1
 * 
 * @author LinChen
 * 
 */
public class ApkRecord {
	private static int startCount = 1;

	private static int installTime;

	public static void init(Context context) {
		String processName = getCurProcessName(context);
		SharedPreferences sp = context.getSharedPreferences("lf_apprecord", Context.MODE_PRIVATE);
		startCount = sp.getInt(processName + "_start", 1);
		if (startCount == 1) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			int curTime = Integer.parseInt(sdf.format(new Date()));
			sp.edit().putInt("installTime", curTime).commit();
		}
		installTime = sp.getInt("installTime", -1);

		sp.edit().putInt(processName + "_start", startCount + 1).commit();
	}

	public static int getStartCount() {
		return startCount;
	}

	public static int getInstallTime() {
		return installTime;
	}

	/**
	 * 获取使用天数
	 * 
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public static int getUseDays() {
		if (installTime <= 0) {
			return 1;
		} else {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				Calendar cal = Calendar.getInstance();
				cal.setTime(sdf.parse(installTime + ""));
				long time1 = cal.getTimeInMillis();
				long time2 = System.currentTimeMillis();
				return Math.round((time2 - time1) / (1000 * 3600 * 24) + 0.5f);
			} catch (ParseException e) {
				e.printStackTrace();
				return 1;
			}
		}
	}

	/**
	 * 查询当前的进程名称
	 * 
	 * @param context
	 * @return 进程名称
	 */
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : manager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
}
