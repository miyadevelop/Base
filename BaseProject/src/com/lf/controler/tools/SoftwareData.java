package com.lf.controler.tools;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.lf.tools.datacollect.ApkRecord;
import com.mobi.tool.MyR;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 软件信息
 * @author ludeyuan
 *
 */
public class SoftwareData {

	//安装的时间
	private static final String KEY_INSTALL_TIME = "install_time";
	//打开的纪录
	private static final String KEY_OPEN_RECORD = "open_record";

	/**
	 * 获取VersionName
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersionName(Context context) {
		String versionName = "";
		try {
			versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versionName = "";
		}
		return versionName;
	}

	/**
	 * 获取VersionCode
	 * 
	 * @param context
	 * @return
	 */
	public static int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			versionCode = 0;
		}
		return versionCode;
	}

	/**
	 * 获取应用的版本号，用于检测更新等
	 */
	public static String getAppliactionVersion(Context context){
		String localVersion = context.getResources().getString(MyR.string(context,"product_version"));
		return localVersion;
	}

	/**
	 * 记录用户打开了此软件，这个只需要在应用刚打开的时候调用一下，比如欢迎界面，该方法会统计app的使用情况
	 */
	public static void userOpen(Context context) {
		ApkRecord.init(context);
		File saveFile = getSaveFile(context);
		if (saveFile.exists()) {// 文件存在

			try {
				JSONObject root = new JSONObject(StringUtil.from(saveFile));

				if (!root.has(KEY_INSTALL_TIME)) {// 不存在该记录
					root.put(KEY_INSTALL_TIME, System.currentTimeMillis());// 设置启动时间
				}

				String ver = SoftwareData.getVersionName(context);// 获取App版本名称
				if (root.has(ver)) {// 如果存在，启动记录+1
					int count = root.getInt(ver);
					root.put(ver, count + 1);
				} else {
					root.put(ver, 1);// 设置当前版本启动次数
				}

				if (root.has(KEY_OPEN_RECORD)) {// 有记录，追加打开记录
					OpenRecord openRecord = new OpenRecord(ver, System.currentTimeMillis());
					JSONArray array = root.getJSONArray(KEY_OPEN_RECORD);
					array.put(openRecord.toJson());
					root.put(KEY_OPEN_RECORD, array);
				} else {
					JSONArray array = new JSONArray();
					OpenRecord openRecord = new OpenRecord(ver, System.currentTimeMillis());
					array.put(openRecord.toJson());
					root.put(KEY_OPEN_RECORD, array);
				}

				StringUtil.to(root.toString(), saveFile);

			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {// 文件不存在
			JSONObject root = new JSONObject();
			try {
				root.put(KEY_INSTALL_TIME, System.currentTimeMillis());// 设置启动时间

				String ver = SoftwareData.getVersionName(context);// 获取App版本名称
				root.put(ver, 1);// 设置当前版本启动次数

				// 设置打开记录
				JSONArray array = new JSONArray();
				OpenRecord openRecord = new OpenRecord(ver, System.currentTimeMillis());
				array.put(openRecord.toJson());
				root.put(KEY_OPEN_RECORD, array);
				StringUtil.to(root.toString(), saveFile);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取到缓存文件的地址
	 * @return
	 */
	public static File getSaveFile(Context context){
		return new File(context.getFilesDir() + File.separator + "myUserUsage" + File.separator
				+ MyMD5.generator(getCurProcessName(context)));// 进程的名称中可能会出现一些特殊字符，所以做特殊处理
	}

	/**
	 * 查询当前的进程名称
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
		return "null";
	}

	/**
	 * 获取安装时间
	 * 
	 * @return
	 */
	public static long getInstallTime(Context context) {
		File saveFile = getSaveFile(context);
		if (saveFile.exists()) {
			try {
				JSONObject root = new JSONObject(StringUtil.from(saveFile));
				return root.getLong(KEY_INSTALL_TIME);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return System.currentTimeMillis();
		} else {
			return System.currentTimeMillis();
		}
	}

	/**
	 * 获取使用的天数，最少返回1
	 * 
	 * @return
	 */
	public static int getUserDays(Context context) {
		long install = getInstallTime(context);
		long sjc = System.currentTimeMillis() - install;
		int userDays = 0;
		userDays = (int) (sjc / (1000 * 60 * 60 * 24));
		if (userDays <= 0) {
			return 1;
		} else {
			return userDays;
		}
	}

	/**
	 * 获取当前版本的打开次数
	 * 
	 * @return
	 */
	public static int getCurVerOpenCount(Context context) {
		File saveFile = getSaveFile(context);
		if (saveFile.exists()) {
			try {
				JSONObject root = new JSONObject(StringUtil.from(saveFile));
				return root.getInt(SoftwareData.getVersionName(context));
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return 0;
		} else {
			return 0;
		}
	}

	/**
	 * 获取所有的打开记录
	 * 
	 * @return
	 */
	public static List<OpenRecord> getAllOpenRecord(Context context) {
		File saveFile = getSaveFile(context);
		if (saveFile.exists()) {
			try {
				List<OpenRecord> result = new ArrayList<OpenRecord>();
				JSONObject root = new JSONObject(StringUtil.from(saveFile));
				JSONArray array = root.getJSONArray(KEY_OPEN_RECORD);

				int len = array.length();
				for (int i = 0; i < len; ++i) {
					JSONObject jsonObject = array.getJSONObject(i);
					OpenRecord openRecord = new OpenRecord(jsonObject);
					result.add(openRecord);
				}
				return result;
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return new ArrayList<OpenRecord>();
		} else {
			return new ArrayList<OpenRecord>();
		}
	}
	
	/**
	 * 从Manifest中获取到meta中的值
	 */
	public static String getMetaData(String key,Context context) {
		Bundle bundle;
		context.getPackageManager();
		try {
			bundle = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA).metaData;
			if (bundle != null) {
				String result = bundle.getString(key);
				if (result == null) {
					result = String.valueOf(bundle.getInt(key));
				}
				if ("0".equals(result)) {
					result = "";
				}
				return result;
			}
		} catch (NameNotFoundException e1) {
		}
		return "";
	}
}
