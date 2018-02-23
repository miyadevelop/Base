package com.lf.controler.tools.weather;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.lf.controler.tools.JSONObjectTool;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.NetLoader;


/**
 * 天气加载
 * @author wangwei
 *
 */
public class WeatherLoader extends NetLoader{


	private Weather mWeather;


	public WeatherLoader(Context context) {
		super(context);
		mWeather = new Weather();
	}



//	public void loadWeather(String city)
//	{
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("cityname", city);
//		loadWithParams(params );
//	}
//	
//	
//	public void refreshWeather(String city)
//	{
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("cityname", city);
//		refresh(params );
//	}
//
//
//	@Override
//	public String parse(String jsonStr, Object... objects) {
//		try {
//
//			JSONObject jsonObject = new JSONObject(jsonStr);
//
//			String status = jsonObject.getString("errNum");
//			//
//			if("0".equals(status))
//			{
//				JSONObject data = jsonObject.getJSONObject("retData");
//				JSONObjectTool tool = new JSONObjectTool(data);
//				String city = tool.getString("city");
//				String cityCode = tool.getString("citycode");
//				String sky = tool.getString("weather");
//				String temp = tool.getString("temp");
//				String l_temp = tool.getString("l_tmp");
//				String h_temp = tool.getString("h_tmp");
//				String wd = tool.getString("WD");
//				String ws = tool.getString("WS");
//				String date = tool.getString("date");
//
//				mWeather.setCity(city);
//				mWeather.setCityCode(cityCode);
//				mWeather.setWeatherStr(sky);
//				mWeather.setTempNow(temp);
//				mWeather.setTempLow(l_temp);
//				mWeather.setTempHigh(h_temp);
//				mWeather.setWindDirection(wd);
//				mWeather.setWindSpeed(ws);
//				mWeather.setPublishTime(date);
//				mWeather.setCurrentTime(Long.toString(System.currentTimeMillis()));
//				mWeather.setValidateTime(Long.toString(System.currentTimeMillis()));
//
//				return OK;
//			}
//			else
//			{
//				return jsonObject.getString("errMsg");
//			}
//
//		} catch (Exception e) {
//			return e.toString();
//		}
//	}
//
//	@Override
//	public DownloadCheckTask initDownloadTask() {
//		DownloadCheckTask task = new DownloadCheckTask();
//		task.mIsSimple = true;
//		task.mUrl = "http://apis.baidu.com/apistore/weatherservice/cityname";
//		task.addMustParams("cityname");
//		task.addHead("apikey", "7b64f7ed05281e13f18c6e1d0c08eb92");
//		return task;
//	}
	
	
	public void loadWeather(String city)
	{
		city = city.replace("市", "");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("city", city);
		loadWithParams(params );
	}
	
	
	public void refreshWeather(String city)
	{
		city = city.replace("市", "");
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("city", city);
		refresh(params );
	}


	
	@Override
	public String parse(String jsonStr, Object... objects) {
		try {

			JSONObject jsonObject = new JSONObject(jsonStr).getJSONArray("HeWeather data service 3.0").getJSONObject(0);

//			String status = jsonObject.getString("status");
//			//
//			if("ok".equals(status))
			{
				
				JSONObject cityJson = jsonObject.getJSONObject("aqi").getJSONObject("city");
				String qlty = cityJson.getString("qlty");
				
				JSONObject basic = jsonObject.getJSONObject("basic");
				JSONObjectTool basicTool = new JSONObjectTool(basic);
				String city = basicTool.getString("city","");
	
				
//				String cityCode = tool.getString("citycode");
			
				JSONObject now = jsonObject.getJSONObject("now");
				JSONObjectTool nowTool = new JSONObjectTool(now);
				String sky = new JSONObjectTool(nowTool.getJSONObject("cond")).getString("txt","");
				String temp = nowTool.getString("tmp","N");
//				String l_temp = nowTool.getString("l_tmp");
//				String h_temp = nowTool.getString("h_tmp");
//				String wd = nowTool.getString("WD");
//				String ws = nowTool.getString("WS");
//				String date = nowTool.getString("date");

				mWeather.setCity(city);
//				mWeather.setCityCode(cityCode);
				mWeather.setWeatherStr(sky);
				mWeather.setTempNow(temp);
//				mWeather.setTempLow(l_temp);
//				mWeather.setTempHigh(h_temp);
//				mWeather.setWindDirection(wd);
//				mWeather.setWindSpeed(ws);
//				mWeather.setPublishTime(date);
				mWeather.setQuality(qlty);
				mWeather.setCurrentTime(Long.toString(System.currentTimeMillis()));
				mWeather.setValidateTime(Long.toString(System.currentTimeMillis()));

				return OK;
			}
//			else
//			{
//				return jsonObject.getString("errMsg");
//			}

		} catch (Exception e) {
			return e.toString();
		}
	}

	@Override
	public DownloadCheckTask initDownloadTask() {
		DownloadCheckTask task = new DownloadCheckTask();
		task.mIsSimple = true;
		task.mUrl = "http://apis.baidu.com/heweather/weather/free";
		task.addMustParams("city");
		task.addHead("apikey", "7b64f7ed05281e13f18c6e1d0c08eb92");
		return task;
	}
	
	
	public Weather getWeather() {
		return mWeather;
	}
}
