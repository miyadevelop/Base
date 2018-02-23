package com.lf.controler.tools;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;

/**
 * APK操作工具
 * 
 * @author LinChen
 *
 */
public class ApkUtil {
	/**
	 * 检查是否安装了该包名的应用
	 * 
	 * @param context
	 * @param pckName
	 * @return
	 */
	public static boolean isInstall(Context context, String pckName) {
		if (pckName == null) {
			return false;
		}
		Intent intent = context.getPackageManager().getLaunchIntentForPackage(pckName);
		if (intent == null) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取打开应用的Intent
	 * 
	 * @param context
	 * @param pckName
	 * @return
	 */
	public static Intent getLaunchIntent(Context context, String pckName) {
		return context.getPackageManager().getLaunchIntentForPackage(pckName);
	}

	/**
	 * 获取已安装应用的应用图标
	 * 
	 * @param context
	 * @param pckName
	 * @return
	 */
	public static Drawable getAppIcon(Context context, String pckName) {
		try {
			return context.getPackageManager().getApplicationIcon(pckName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 安装指定路径的Apk
	 * 
	 * @param context
	 * @param path
	 */
	public static void install(Context context, String path) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(new File(path)),
				"application/vnd.android.package-archive");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 卸载apk
	 * 
	 * @param context
	 * @param pck
	 */
	public static void unInstall(Context context, String pck) {
		Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + pck))
		.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	/**
	 * 根据应用名在市场上查找应用
	 * 
	 * @param context
	 * @param name
	 */
	public static void searchByName(Context context, String name) {
		try {
			Uri uri = Uri.parse("market://search?q=" + name);
			Intent it = new Intent().setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据指定的市场搜索
	 * 
	 * @param context
	 * @param name
	 * @param tarMarket
	 */
	public static void searchByName(Context context, String name, String tarMarket) {
		try {
			Uri uri = Uri.parse("market://search?q=" + name);
			Intent it = new Intent().setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
					.setPackage(tarMarket);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据包名在市场上查找应用
	 * 
	 * @param context
	 * @param packageName
	 */
	public static void searchByPackage(Context context, String packageName) {
		try {
			Uri uri = Uri.parse("market://details?id=" + packageName);
			Intent it = new Intent().setData(uri).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(it);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 检查apk文件是否完整
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static boolean isComplete(Context context, String path) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
			if (info == null)
				return false;
			else
				return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取安装包的信息
	 * 
	 * @param context
	 * @param path
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context context, String path) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
			return info;
		} catch (Exception e) {
			return null;
		}
	}
}
