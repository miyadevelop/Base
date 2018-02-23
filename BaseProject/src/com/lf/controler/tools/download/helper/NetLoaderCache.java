package com.lf.controler.tools.download.helper;

import android.content.Context;

import com.lf.controler.tools.SaveTime;
import com.lf.tool.data_persistence.DataEditor;
import com.lf.tool.data_persistence.DataPersistenceUtil;
import com.lf.tool.data_persistence.FileDataUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/**
 * 网络加载的本地缓存路径管理
 * @author ww
 *
 * 2016-5-2
 */
public class NetLoaderCache {

	//TODO 目前存储和读取的权限尚未生效，后面要把文件读取工具的路径做规范之后，再完善
	//读取模式相关字段
	public static int MODE_PRIVATE = 0;//私有路径
	public static int MODE_WORLD_READABLE = 1;//外界可读，暂不支持可读可写的区分，非私有路径都可读可写
	public static int MODE_WORLD_WRITEABLE = 2;//外界可写
	public int mMode = MODE_PRIVATE;//缓存存储、读取模式

	private String mContentKey;//缓存文件名称前缀，整体名称有mContentKey+参数字符串拼接而成
	private Context mContext;


	/**
	 * 构造
	 * @param context
	 * @param contentKey 代表要缓存的内容的唯一标识
	 * @param mode 缓存模式，私有、外界可读、外界可写
	 */
	public NetLoaderCache(Context context ,String contentKey,int mode)
	{
		mContext = context;
		//将网址转为缓存文件名称
		mContentKey = contentKey.replace("http://", "");
		mContentKey = mContentKey.replace("/", "_");
		mContentKey = mContentKey.replace(".", "_");
		mMode = mode;
	}


	/**
	 * 构造
	 * @param context
	 * @param contentKey 代表要缓存的内容的唯一标识
	 */
	public NetLoaderCache(Context context ,String contentKey)
	{
		mContext = context;
		//将网址转为缓存文件名称
		mContentKey = contentKey.replace("http://", "");
		mContentKey = mContentKey.replace("/", "_");
		mContentKey = mContentKey.replace(".", "_");
	}


	/**
	 * 保存缓存
	 * @param content 保存的内容
	 * @param params 访问网页所携带参数，通过此参数和mFilters做对比达到过滤的目的
	 */
	public void save(String content, HashMap<String, String> params)
	{
		//		Log.i("Lafeng", "save name:" + mContentKey);
		//		Log.i("Lafeng", "save filter:" + getFilter(params));
		//		Log.i("Lafeng", "save content:" + content);
		//使用存储工具，存储数据到手机的SD卡和内存上
		String name = mContentKey + getFilter(params);
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext,name);
		//获取编辑器
		DataEditor editor = fileDataUtil.getDataEditor();
		editor.putString("content", content);//存储服务器上解析到的数据
		fileDataUtil.commit();	//提交数据到本地
		//存储保存的时间
		saveLoadTime(name);
	}


	/**
	 * 读取缓存
	 * @param params 访问网页所携带参数，通过此参数和mFilters做对比达到过滤的目的
	 * @return 读取到的字符串，如果没有读取到
	 */
	public String read(HashMap<String, String> params)
	{
		//使用存储工具，从手机上读取上次的缓存信息
		String name = mContentKey + getFilter(params);
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext, name);
		String content = fileDataUtil.getString("content", "");
		//		Log.i("Lafeng", "read name:" + mContentKey);
		//		Log.i("Lafeng", "read filter:" + getFilter(params));
		//		Log.i("Lafeng", "read content:" + content);
		return content;
	}



	/**
	 * 清除缓存
	 * @param params 访问网页所携带参数，通过此参数和mFilters做对比达到过滤的目的
	 */
	public void clean(HashMap<String, String> params)
	{

	}


	/**
	 * 根据上次存储的时间，返回是否需要重新刷新
	 * @param refreshBean 控制刷新的条件，例如每天更新一次
	 * @return
	 */
	protected boolean needReload(HashMap<String, String> params,NetRefreshBean refreshBean){
		String key = mContentKey + getFilter(params);
		return needReload(key, refreshBean);
	}


	/**
	 * 根据上次存储的时间，返回是否需要重新刷新
	 * @param refreshBean 控制刷新的条件，例如每天更新一次
	 * @return
	 */
	public boolean needReload(String key,NetRefreshBean refreshBean){
		//读取缓存中的值
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext, key+"_save_time");
		String saveValue = fileDataUtil.getLong("save_time", 0) + "";
		//		Log.i("Lafeng", "saveKey:"+ key+"_save_time");
		//		Log.i("Lafeng", "saveValue:"+ saveValue);
		//上次没有纪录值，就不需要处理
		if(saveValue==null || "0".equals(saveValue)){
			return true;
		}
		//判断是按照Context更新
		if(refreshBean.getNetEnumRefreshTime()==NetEnumRefreshTime.Activity){
			//和上次的地址的值相同，就返回false，表示不需要更新
			if(saveValue.equals(refreshBean.getContextAddres()))
				return false;
			else
				return true;
		}else{
			long lastTime = Long.valueOf(saveValue);
			//获取存储的日期和当前的日期
			long curTime = SaveTime.getInstance(mContext).currentTimeMillis();
			long duration = curTime - lastTime;
			long durationUnit;	//间隔的单位
			if(refreshBean.getNetEnumRefreshTime()==NetEnumRefreshTime.Time_Second){
				durationUnit = 1000;
			}else if(refreshBean.getNetEnumRefreshTime()==NetEnumRefreshTime.Time_Hour){
				durationUnit = 60 * 60 * 1000;
			}else{//按照天来计算
				durationUnit = 24 * 60 * 60 * 1000;
			}

			//			Log.i("Lafeng", "lastTime:"+ lastTime + " curTime" + curTime + " durationUnit" + durationUnit + " duration/durationUnit:" + duration/durationUnit);
			//间隔的日期超过了规定的时间，实现刷新
			if(duration/durationUnit>=refreshBean.getTimeValue()){
				return true;
			}else{
				return false;
			}
		}
	}


	/**
	 * 存储当前下载数据的时间，为下次判断是否需要刷新做准备
	 */
	private void saveLoadTime(String key){
		//读取缓存中的值
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext, key+"_save_time");
		//		Log.i("Lafeng", "save saveKey:"+ key+"_save_time");
		//获取编辑器
		DataEditor editor = fileDataUtil.getDataEditor();
		editor.putLong("save_time", SaveTime.getInstance(mContext).currentTimeMillis());
		fileDataUtil.commit();
	}


	/**
	 * 存储当前下载数据的时间，为下次判断是否需要刷新做准备
	 */
	public void saveServerTime(HashMap<String, String> params, String time){
		String key = mContentKey + getFilter(params);
		//读取缓存中的值
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext, key+"_server_time");
		//获取编辑器
		DataEditor editor = fileDataUtil.getDataEditor();
		editor.putString("server_time", time);
		fileDataUtil.commit();
	}


	public String getServerTime(HashMap<String, String> params){
		String key = mContentKey + getFilter(params);
		//读取缓存中的值
		FileDataUtil fileDataUtil = DataPersistenceUtil.getFileDataUtil(mContext, key+"_server_time");
		return fileDataUtil.getString("server_time", "").replaceAll(" ", "");
	}



	/**
	 * 将参数拼接成String，获取map中的值，将值以_+值+_值
	 * @param params 访问网页所携带参数
	 * @return 参数拼接成的字符串
	 */
	private String getFilter(HashMap<String, String> params)
	{
		if(params.size()>0){
			List<Map.Entry<String, String>> list =new ArrayList<Map.Entry<String, String>>(params.entrySet());
			Collections.sort(list, mComparator);
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> entry : list) {
				String value = entry.getValue();
				sb.append("_").append(value);
			};
			return sb.toString();
		}else{
			return "";
		}
	}


	private Comparator<Map.Entry<String, String>> mComparator = new Comparator<Map.Entry<String,String>>() {

		@Override
		public int compare(Entry<String, String> arg0, Entry<String, String> arg1) {
			try {
				return arg0.getValue().compareTo(arg1.getValue());
			} catch (Exception e) {
				return 0;
			}
		}
	};
}
