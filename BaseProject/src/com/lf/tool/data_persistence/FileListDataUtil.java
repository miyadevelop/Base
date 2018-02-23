package com.lf.tool.data_persistence;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import android.content.Context;

public class FileListDataUtil extends JSONArrayDataUtil{
	private Context context;
	private String fileName;

	public FileListDataUtil(Context context,JSONArray jsonArray,String fileName) {
		super(jsonArray);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.fileName = fileName;
	}
	
	public void commit(){
		List<Map<String, Object>> list = jsonArrayToMapList();
		Map<Integer,DataEditor> map = getArrayDataEditor().getMap();
		
		//TODO  这边有个疑问,提交的时候,原来的list长度改变了怎么办
		for (Map.Entry<Integer,DataEditor> entry : map.entrySet()) {
			//找到要修改的那一项
			Map<String, Object> changeMap = list.get(entry.getKey());
			Map<String, Object> editMap = entry.getValue().getMap();
			 for (Map.Entry<String, Object> e : editMap.entrySet()) {
				 changeMap.put(e.getKey(), e.getValue());
		        }  
			 //增加添加的字段
	        }  
		List<DataEditor> appendList = getArrayDataEditor().getAppendList();
		for(DataEditor editor:appendList){
			list.add( editor.getMap());
		}
		try {
			ReadAndWriteUtil.save(context, fileName,list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
