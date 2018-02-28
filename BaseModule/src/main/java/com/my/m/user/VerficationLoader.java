package com.my.m.user;

import android.content.Context;

import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetLoader;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * 获取验证码
 * @author wangwei
 *
 */
public class VerficationLoader extends NetLoader{

	public VerficationLoader(Context context) {
		super(context);
	}

	public void getVerfication(String phone)
	{
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("phone", phone);
		loadWithParams(new HashMap<String, String>(), params);
	}
	
	
	@Override
	public String parse(String jsonStr, Object... objects) {
		try {
			JSONObject jsonObject = new JSONObject(jsonStr);

			String status = jsonObject.getString("status");
			if("ok".equals(status))
			{
				return NetLoader.OK;
			}
			else
			{
				return jsonObject.getString("message");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	@Override
	public DownloadCheckTask initDownloadTask() {
		DownloadCheckTask task = new DownloadCheckTask();
		task.mIsSimple  = true;
		task.mUrl = Consts.getHost(getContext()) + "/mall/obtainGet.json";
		task.addMustParams("phone");
		LoadUtils.addUniversalParam(getContext(), task);
		task.addParams("appKey", Consts.getAppKey(getContext()));
		return task;
	}

}
