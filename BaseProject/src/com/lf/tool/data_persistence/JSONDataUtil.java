package com.lf.tool.data_persistence;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON数据工具,对JSON字段进行管理
 * @author hzc 2015/12/03
 *
 */
public class JSONDataUtil {
	// 定义基本类型的常量
	protected JSONObject json;
	private DataEditor dataEditor;

	public JSONDataUtil(JSONObject json) {
		this.json = json;
	}

	public boolean contains(String key) {
		return json.has(key);
	}

	
	/**
	 * 获取对数据进行编辑的编辑器
	 * @return
	 */
	public DataEditor getDataEditor() {
		if (dataEditor == null)
			dataEditor = new DataEditor();
		return dataEditor;

	}

	public boolean getBoolean(String key, boolean value) {
		try {
			value = json.getBoolean(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public int getInt(String key, int value) {
		try {
			value = json.getInt(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

	public float getFloat(String key, Float value) {
		try {
			value = (Float) json.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public long getLong(String key, long value) {
		try {
			value = json.getLong(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	/**
	 * 获取指定key对应的值
	 * @param key 取值时的key
	 * @param value  取不到值时的默认值
	 * @return 要取的值
	 */
	public String getString(String key, String value) {
		try {
			value = json.getString(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}

	public double getDouble(String key, double value) {
		try {
			value = json.getDouble(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	public JSONObject getJSON(){
		return json;
	}
	
	
	/**
	 * 获取内部嵌套的JSON
	 * @param key 取值时的key
	 * @param value  取不到值时的默认值
	 * @return 要取的值
	 */
	public JSONObject getNestedJSON(String key, JSONObject value){
		JSONObject result = null;
		try {
			String s = json.getString(key);
			result = new JSONObject(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 将json转换为map
	 * 
	 * @return
	 */
	public Map<String, Object> jsonToMap() {
		Iterator<String> it = json.keys();
		Map<String, Object> result = new HashMap<String, Object>();
		while (it.hasNext()) {
			try {
				String key = (String) it.next();
				//由于这里的key都是类型_key的形式,所以需要做下处理
				Object value = json.get(key);
				result.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

}
