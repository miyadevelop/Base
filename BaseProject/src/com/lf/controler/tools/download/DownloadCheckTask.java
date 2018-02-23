package com.lf.controler.tools.download;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 具有检查访问服务器参数功能的DownloadTask
 * 
 * @author hzc 2016-2-16
 * 
 */
public class DownloadCheckTask extends DownloadTask {
//	//该下载任务的名称,目前以添加参数之前的网址作为名称
//	private String name;
	// 访问服务器的时候，必须的参数
	private List<String> mMustParams;
	// 访问服务器的时候，非必须的参数－－－－暂不用
	private List<String> mFreeParams;
	
	//url上需要拼接的参数
	private HashMap<String, String> paramMap;
	//post中，提交的参数
	private HashMap<String, String> postParamMap;
	
	public DownloadCheckTask(){
		mMustParams = new ArrayList<String>();
		paramMap = new HashMap<String, String>();
		postParamMap = new HashMap<String, String>();
		mIsSimple = true;//默认是简单下载
		
//		/**
//		 * 设置一个默认值
//		 */
//		mId = mTag = System.currentTimeMillis();
		
	}
	
	public void addMustParams(String key){
		mMustParams.add(key);
	}
	
	public void addFreeParams(String key){
		mFreeParams.add(key);
	}
//
//	public void setName(String name){
//		this.name = name;
//	}
//	
//	public String getName(){
//		return name;
//	}

	/**
	 * 检查参数，如果缺少必要参数，则抛出异常
	 * @throws Exception
	 */
	public void checkParams() {
		for(String s:mMustParams){
			if(!paramMap.containsKey(s)&&!postParamMap.containsKey(s)){
				Exception e = new Exception("no params:"+s);
				 e.printStackTrace();
			}
		}
		//检查完毕后，将map转为url以及post中内容
		if (mUrl.indexOf("?") == -1) {
			mUrl += "?" + getMapString(paramMap);
		} else {
			mUrl += getMapString(paramMap);
		}
		//有需要post提交的内容
		if(postParamMap.size()>0){
			isPost = true;
			queryString = getMapString(postParamMap);
		}
		
	}
	
	/**
	 * 添加url上拼接的参数
	 * @param key
	 * @param value
	 */
	public void addParams(String key, String value){
		
//		try {
////			value = URLDecoder.decode(value, "UTF-8");
//			value = URLEncoder.encode(value, "UTF-8");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		paramMap.put(key, value);
	}
	
	/**
	 * 添加post请求中的参数
	 * @param key
	 * @param value
	 */
	public void addPostParams(String key, String value){
//		try {
////			value = URLDecoder.decode(value, "UTF-8");
//			value = URLEncoder.encode(value, "UTF-8");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		postParamMap.put(key, value);
	}
	
	/**
	 * 将map中的键值对拼接成String
	 * 
	 * @return
	 */
	public String getMapString(Map<String, String> map) {
		if(map.size()>0){
			StringBuilder sb = new StringBuilder();
			Iterator<Entry<String, String>> iter = map.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				sb.append(key).append("=").append(value);
				sb.append("&");
			}
			return sb.substring(0, sb.length() - 1);
		}else{
			return "";
		}
	}

	
	/**
	 * 获取参数
	 * @return 参数
	 * @author wangwei
	 */
	public HashMap<String, String> getParams()
	{
		return paramMap;
	}
	
	
	/**
	 * 获取post参数
	 * @return post参数
	 * @author wangwei
	 */
	public HashMap<String, String> getPostParams()
	{
		return postParamMap;
	} 
}
