package com.lf.tool.data_persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * 
 * @author hzc 20151203
 * 
 */
public class JSONArrayDataUtil {
	private JSONArray jsonArray;
	private ArrayDataEditor dataEditor;

	public JSONArrayDataUtil(JSONArray jsonArray) {
		this.jsonArray = jsonArray;
	}

	/**
	 * 根据下标获取具体某一条数据的管理对象
	 * @param index 要获取的数据的下标
	 * @return 某一条数据的管理对象
	 */
	public JSONDataUtil getJSODataUtil(int index) {
		try {
			return new JSONDataUtil(jsonArray.getJSONObject(index));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 获取集合数据编辑器
	 * @return ArrayDataEditor 集合数据编辑器
	 */
	public ArrayDataEditor getArrayDataEditor() {
		if (dataEditor == null)
			dataEditor = new ArrayDataEditor();
		return dataEditor;
	}
	
	public JSONArray getJSONArray(){
		return jsonArray;
	}

	protected List<Map<String, Object>> jsonArrayToMapList() {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		try {
			for (int i = 0; i < jsonArray.length(); i++) {
                JSONDataUtil util = new JSONDataUtil(jsonArray.getJSONObject(i));
                list.add( util.jsonToMap());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

}
