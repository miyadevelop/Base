package com.lf.tool.data_persistence;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class FileDataUtil extends JSONDataUtil {
	private Context context;
	private String fileName;

	public FileDataUtil(Context context, JSONObject json, String fileName) {
		super(json);
		this.context = context;
		this.fileName = fileName;
		
	}

	/**
	 * 提交,对修改完的数据进行提交,保存到文档中.
	 */
	public void commit() {
		Map<String, Object> resultMap = jsonToMap();
		Map<String, Object> map = getDataEditor().getMap();
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			resultMap.put(entry.getKey(), entry.getValue());
			//同时对json进行修改
			try {
				json.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		getDataEditor().clear();
		try {
			ReadAndWriteUtil.save(context, fileName,resultMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
