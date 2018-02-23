package com.lf.tool.data_persistence;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

/**
 * 数据持久化工具,对文件管理对象以及集合管理对象进行一个总的管理
 * 具有获取文件管理对象以及获取文件集合管理对象的方法
 * @author hzc 2015/12/03
 * 
 */
public class DataPersistenceUtil {
	private static Map<String,FileDataUtil> map = new HashMap<String,FileDataUtil>();

	/**
	 * 获取文件管理对象
	 * @param context 上下文,用于文件的读取以及修改
	 * @param name 要读写的文件的名称
	 * @return 文件的管理对象,用于具体的内容的编辑
	 */
	public static FileDataUtil getFileDataUtil(Context context,String name) {
		if(!map.containsKey(name)){
			map.put(name, new FileDataUtil(context, ReadAndWriteUtil.getJsonObject(context, name), name));
		}
		return map.get(name);
	}
	
	/**
	 * 获取文件集合管理对象
	 * @param context 上下文,用于文件的读取以及修改
	 * @param name 要读写的文件的名称
	 * @return  文件集合的管理对象,用于具体的内容的编辑
	 */
	public static FileListDataUtil getFileListDataUtil(Context context,String name) {
		return new FileListDataUtil(context, ReadAndWriteUtil.getJsonArray(context, name), name);
	}
	
	public static JSONArray getJSONArray(Context context,String name,String packageName) {
		return ReadAndWriteUtil.getJsonArray(context, name, packageName);
	}
	

	public static void deleFile(Context context,String name){
		ReadAndWriteUtil.clearFile(context,name);
	}
	
	public static void deleFile(Context context,String name,String packageName){
		ReadAndWriteUtil.clearFile(context,name,packageName);
	}
	//TODO 序列化对象的处理

}
