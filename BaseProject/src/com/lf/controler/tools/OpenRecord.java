package com.lf.controler.tools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 打开记录
 * 
 * @author LinChen
 *
 */
public class OpenRecord {
	String ver;
	long openTime;

	public OpenRecord(String ver, long time) {
		this.ver = ver;
		this.openTime = time;
	}

	public OpenRecord(JSONObject jsonObject) throws JSONException {
		ver = jsonObject.getString("ver");
		openTime = jsonObject.getLong("time");
	}

	/**
	 * 获取打开记录的App版本号
	 * 
	 * @return
	 */
	public String getVer() {
		return ver;
	}

	/**
	 * 获取打开App的额时间
	 * 
	 * @return
	 */
	public long getOpenTime() {
		return openTime;
	}

	public JSONObject toJson() throws JSONException {
		JSONObject object = new JSONObject();
		object.put("ver", ver);
		object.put("time", openTime);
		return object;
	}
}
