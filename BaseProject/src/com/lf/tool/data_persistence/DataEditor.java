package com.lf.tool.data_persistence;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

/**
 * 数据编辑器,修改数据的时候,由编辑器编辑完后统一提交
 * 
 * @author hzc
 * 
 */
public class DataEditor {
	private Map<String, Object> mMap = new HashMap<String, Object>();
	

	/**
	 * 清除之前编辑的内容
	 * @return
	 */
	public DataEditor clear() {
		mMap.clear();
		return this;
	}
	
	protected Map<String, Object> getMap(){
		return mMap;
	}


	public DataEditor putBoolean(String arg0, boolean arg1) {
		mMap.put(arg0, arg1);
		return this;
	}

	public DataEditor putFloat(String arg0, float arg1) {
		mMap.put(arg0, arg1);
		return this;
	}

	public DataEditor putInt(String arg0, int arg1) {
		mMap.put(arg0, arg1);
		return this;
	}

	public DataEditor putLong(String arg0, long arg1) {
		mMap.put(arg0, arg1);
		return this;
	}

	/**
	 * 添加或者修改指定key的值
	 * @param key   要编辑的key
	 * @param arg1  编辑后的值
	 * @return DataEditor 编辑器
	 */
	public DataEditor putString(String key, String arg1) {
		mMap.put(key, arg1);
		return this;
	}
	
	
	/**
	 * 添加或者修改指定key的嵌套的json的值
	 * @param key   要编辑的key
	 * @param arg1  编辑后的值
	 * @return DataEditor 编辑器
	 */
	public DataEditor putNestedJSON(String arg0, JSONObject json) {
		mMap.put(arg0, json.toString());
		return this;
	}
	
	public DataEditor putDouble(String arg0, double arg1) {
		mMap.put(arg0, arg1);
		return this;
	}

	public DataEditor remove(String arg0) {
		// TODO Auto-generated method stub
		mMap.remove(arg0);
		return this;
	}
	
	/**
	 * 将编辑的内容转化为一个json对象
	 * @return
	 */
	public JSONObject toJSON(){
		return new JSONObject(mMap);
	}


}
