package com.lf.controler.tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/***
 * 此类用来避免解析json时出的异常导致程序中断(值不存在，返回null字符串) 1.当解析异常时可抛出原异常 2.当设置默认值后，解析异常时返回默认值 3.输出异常提醒
 * */
public class JSONObjectTool /* extends JSONObject */{

	private JSONObject mJSONObject;

	/**
	 * 初始化
	 * @throws JSONException 
	 * **/
	public JSONObjectTool(String jsonString) throws JSONException {
		mJSONObject = new JSONObject(jsonString);
	}


	/**
	 * 初始化
	 * **/
	public JSONObjectTool(JSONObject jsonObject) {
		mJSONObject = jsonObject;
	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public int getInt(String name, int defaultValue, boolean needError) throws Exception {
		try {
			return mJSONObject.getInt(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}

	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public String getString(String name, String defaultValue, boolean needError) throws Exception {
		try {
			return mJSONObject.getString(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}

	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public boolean getBoolean(String name, Boolean defaultValue, boolean needError)
			throws Exception {
		try {
			return mJSONObject.getBoolean(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}
	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public Double getDouble(String name, Double defaultValue, boolean needError) throws Exception {
		try {
			return mJSONObject.getDouble(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}
	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public Long getLong(String name, Long defaultValue, boolean needError) throws Exception {
		try {
			return mJSONObject.getLong(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}
	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public JSONArray getJSONArray(String name, JSONArray defaultValue, boolean needError)
			throws Exception /* throws JSONException */{
		try {
			return mJSONObject.getJSONArray(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}
	}

	/**
	 * 当有值时直接得到值，当有默认值时给默认值
	 * 
	 * @param name
	 *            要取的属性的名称
	 * @param defaultValue
	 *            要返回的默认值
	 * @param needError
	 *            取值异常时是否要抛异常
	 * @throws Exception
	 * **/
	@Deprecated
	public JSONObject getJSONObject(String name, JSONObject defaultValue, boolean needError)
			throws Exception /* throws JSONException */{
		try {
			return mJSONObject.getJSONObject(name);
		} catch (Exception e) {// 值不存在、返回值为null字符串转int失败
			if (needError) {
				throw e;
			}
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String name, int defaultValue) {
		try {
			return mJSONObject.getInt(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public String getString(String name, String defaultValue) {
		try {
			return mJSONObject.getString(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public boolean getBoolean(String name, Boolean defaultValue) {
		try {
			return mJSONObject.getBoolean(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public Long getLong(String name, Long defaultValue) {
		try {
			return mJSONObject.getLong(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public Double getDouble(String name, Double defaultValue) {
		try {
			return mJSONObject.getDouble(name);
		} catch (JSONException e) {
			e.printStackTrace();
			return defaultValue;
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public JSONArray getJSONArray(String name, JSONArray defaultValue) {
		try {
			return mJSONObject.getJSONArray(name);
		} catch (JSONException e) {
			e.printStackTrace();
			//如果没有这个jsonArray，则尝试从JsonString中获取String再转为jsonArray
			try {
				String arrayString = mJSONObject.getString(name);
				return new JSONArray(arrayString);
			} catch (Exception e2) {
				e.printStackTrace();
				return defaultValue;
			}
		}
	}

	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public JSONObject getJSONObject(String name, JSONObject defaultValue) {
		try {
			return mJSONObject.getJSONObject(name);
		} catch (JSONException e) {
			e.printStackTrace();
			//如果没有这个JSONObject，则尝试从JsonString中获取String再转为JSONObject
			try {
				String arrayString = mJSONObject.getString(name);
				return new JSONObject(arrayString);
			} catch (Exception e2) {
				e.printStackTrace();
				return defaultValue;
			}
		}
	}


	/**
	 * 尝试获取值，没有则返回默认值
	 * 
	 * @param name
	 * @param defaultValue
	 * @return
	 */
	public JSONObjectTool getJSONObjectTool(String name, JSONObjectTool defaultValue) {
		try {
			return new JSONObjectTool(getJSONObject(name));
		} catch (Exception e) {
			e.printStackTrace();
			return defaultValue;
		}
	}


	public int getInt(String name) throws Exception {
		return mJSONObject.getInt(name);
	}

	public String getString(String name) throws Exception {

		return mJSONObject.getString(name);
	}


	public boolean getBoolean(String name)throws Exception {
		return mJSONObject.getBoolean(name);
	}


	public Double getDouble(String name) throws Exception {
		return mJSONObject.getDouble(name);
	}


	public Long getLong(String name) throws Exception {
		return mJSONObject.getLong(name);

	}


	public JSONArray getJSONArray(String name)throws Exception{
		//如果没有这个JSONArray，则尝试从JsonString中获取String再转为JSONArray
		try {
			return mJSONObject.getJSONArray(name);
		} catch (Exception e) {
			e.printStackTrace();
			String arrayString = mJSONObject.getString(name);
			return new JSONArray(arrayString);
		}
	}


	public JSONObject getJSONObject(String name)throws Exception /* throws JSONException */{

		//如果没有这个JSONObject，则尝试从JsonString中获取String再转为JSONObject
		try {
			return mJSONObject.getJSONObject(name);
		} catch (Exception e) {
			e.printStackTrace();
			String arrayString = mJSONObject.getString(name);
			return new JSONObject(arrayString);
		}

	}

	public JSONObjectTool getJSONObjectTool(String name)throws Exception /* throws JSONException */{
		return new JSONObjectTool(getJSONObject(name));
	}
}
