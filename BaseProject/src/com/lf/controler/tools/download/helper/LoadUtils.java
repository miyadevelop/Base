package com.lf.controler.tools.download.helper;

import android.content.Context;

import com.lf.controler.tools.DeviceData;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.location.Location2;

public class LoadUtils {

	/**
	 * 往DownloadCheckTask中添加常用的参数，这里仅添加get形式参数
	 * @param context
	 * @param task
	 */
	public static void addUniversalParam(Context context, DownloadCheckTask task)
	{
		task.addParams("imei", DeviceData.getImei(context));
		task.addParams("imsi", DeviceData.getImsi(context));
		task.addParams("mac", DeviceData.getMac(context));
		//手机机型
		task.addParams("phone_type", android.os.Build.MODEL.replaceAll(" ", ""));
		//手机系统版本
		task.addParams("phone_system", DeviceData.getOSUserVer().replaceAll(" ", ""));
		//位置坐标
		if(null != Location2.getInstance(context).mLocation)
		{

			task.addParams("latitude", Location2.getInstance(context).mLocation.getLatitude() + "");
			task.addParams("longitude", Location2.getInstance(context).mLocation.getLongitude() + "");
		}
		//包名
		task.addParams("package_name", context.getPackageName());
		//渠道
		String market = SoftwareData.getMetaData("UMENG_CHANNEL", context);
		if(null != market && !"".equals(market))
			task.addParams("market", market);
		//软件版本
		task.addParams("version", SoftwareData.getAppliactionVersion(context));
		//平台
		task.addParams("askSrc", "android");
	}

}
