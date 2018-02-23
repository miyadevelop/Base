package com.lf.controler.tools.download.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.Context;

import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.NetLoader.EnumLoadingStatus;


/**
 * 分类分页加载器
 * 此类继承与FenYeLoader，但此类从结构关系来说，并不应该继承FenYeLoader，仅是为了获取与FenYeLoader一样的函数，
 * 规定了一个固定格式，并且有部分函数的实现和FenYeLoader一致，为了编写简单，在此让其继承FenYeLoader。
 * FenYeMapLoader整体实现思路和功能和FenYeLoader一致，详细功能可以参考FenYeLoader的注释。
 * 在FenYeLoader的基础上，提供按分类id进行存储的功能，从而支持加载的数据有很多分类，每个分类下有一个列表数据的需求，
 * 例如加载商品，家电分类有一个id，此id对应一个列表的家电商品数据，服装分类有一个id，此id对应一个列表的服装商品数据，
 * 家电和服装数据，都是通过FenYeLoader实现分页加载的。
 * @see FenYeLoader
 * @author ww
 *
 * 2016-5-29
 */
public abstract class FenYeMapLoader<T> extends FenYeLoader<T>{

	//所有的加载各个分类的FenYeLoader，通过分类的id获取对应的FenYeLoader，通过FenYeLoader实现加载、保存数据、提供数据的功能
	private HashMap<String, FenYeLoader<T>> mLoaders = new HashMap<String, FenYeLoader<T>>();
	private Context mContext;
	
	
	public FenYeMapLoader(Context context) {
		super(context);
		mContext = context;
	}
	
	
	/**
	 * 加载一个指定分类的数据，如果本地有缓存，并且没有过期，就会直接从本地读取并返回通知
	 * @param classId 分类的id
	 */
	public void loadResource(String classId){
		if(null == mLoaders.get(classId))
		{
			addLoader(classId);
		}
		mLoaders.get(classId).loadResource();
	}


	/**
	 * 重新刷新一个指定分类的数据，不理会本地的缓存信息
	 * @param classId 分类的id
	 */
	public void refreshResource(String classId){
		if(null == mLoaders.get(classId))
		{
			addLoader(classId);
		}
		mLoaders.get(classId).refreshResource();
	}

	
	/**
	 * 获取一个分类下的数据列表
	 * @param classId 分类的id
	 * @return 一个分类下的数据列表
	 */
	public ArrayList<T> get(String classId) {
		if(null == mLoaders.get(classId))
		{
			addLoader(classId);
		}
		return mLoaders.get(classId).get();
	}
	
	
//	/**
//	 * 获取一个指定的数据T
//	 * @param classId 分类的id
//	 * @param index 数据T在列表中的缩影
//	 * @return T
//	 */
//	public T get(String classId, int index) {
//		if(null == mLoaders.get(classId))
//		{
//			addLoader(classId);
//		}
//		return mLoaders.get(classId).get().get(index);
//	}
	
	
	/**
	 * 添加一个分类的Loader
	 * @param classId 分类的id
	 */
	private void addLoader(String classId)
	{
		MyFenYeLoader loader = new MyFenYeLoader(mContext, classId, getClassIdKey());
		loader.setStartMethod(getStartMethod());
		loader.setPageCount(getPageCount());
		loader.setStartIndex(getStartIndex());
		loader.setRefreshTime(getRefreshTime());
		mLoaders.put(classId, loader);
	}
	

	/**
	 * 定义 分页请求时，向服务器请求数据的参数字段中 分类id字段的名称
	 * @return 分类id字段的名称
	 */
	protected abstract String getClassIdKey();
	
	
	/**
	 * 各个分类的分页加载器，重写FenYeLoader了，在FenYeLoader每次进行加载时，额外加上分类id字段
	 * @author ww
	 *
	 * 2016-5-29
	 */
	public class MyFenYeLoader extends FenYeLoader<T>
	{
		private String mClassId;//分类id
		private String mClassIdKey;//想服务器请求数据的网址中，参数分类id对应的key

		public MyFenYeLoader(Context context, String classId, String classIdKey) {
			super(context);
			mClassId = classId;
			mClassIdKey = classIdKey;
		}

		@Override
		protected DownloadCheckTask initDownloadTask() {
			DownloadCheckTask task = FenYeMapLoader.this.initDownloadTask();
			task.addParams(mClassIdKey, mClassId);//请求时，添加上分类id参数
			return task;
		}

		@Override
		protected String getPageIndexNameOnWeb() {
			return FenYeMapLoader.this.getPageIndexNameOnWeb();
		}

		@Override
		protected String getPageCountNameOnWeb() {
			return FenYeMapLoader.this.getPageCountNameOnWeb();
		}

		@Override
		protected Result<ArrayList<T>> onParse(String json) {
			return FenYeMapLoader.this.onParse(json);
		}

		
		@Override
		public T onParseBean(JSONObject object) {
			return FenYeMapLoader.this.onParseBean(object);
		}
		
		
		@Override
		protected void onDataRefresh(ArrayList<T> newT, Object... objects) {
			FenYeMapLoader.this.onDataRefresh(mClassId, newT, objects);
		}
		
		
		@Override
		public String getAction() {
			// TODO Auto-generated method stub
			return FenYeMapLoader.this.getAction();
		}
	}
		
	
	
	/**
	 * 废弃父类中此函数，在FenYeMapLoader，每次加载时，要提供参数classId，故此函数已无意义
	 * 被loadResource(String classId)取代
	 */
	@Override
	public final void loadResource(){}
	
	
	/**
	 * 获取全部的数据
	 */
	@Override
	public final ArrayList<T> get() {

		ArrayList<T> ret = new ArrayList<T>();

		for(Map.Entry<String, FenYeLoader<T>> entrySet : mLoaders.entrySet())
		{
			FenYeLoader<T> loader = entrySet.getValue();
			if(null != loader)
				ret.addAll(loader.get());
		}
		return ret;
	}


	/**
	 * 废弃父类中此函数，在FenYeMapLoader，每次重新加载时，要提供参数classId，故此函数已无意义
	 * 被refreshResource(String classId)取代
	 */
	@Override
	public final void refreshResource(){}
	
	
	/**
	 * 废弃父类中此函数，在FenYeMapLoader，每次重新加载时，要提供参数classId，故此函数已无意义
	 * 被onDataRefresh(String classId, ArrayList<T> newT, Object... objects) 取代
	 */
	@Override
	protected final void onDataRefresh(ArrayList<T> newT, Object... objects) {}

//	/**
//	 * 废弃父类中此函数，在FenYeMapLoader，获取一个指定数据时，要提供参数classId，故此函数已无意义
//	 */
//	@Override
//	public T get(int index) {
//		return null;
//	}
	
	public EnumLoadingStatus getLoadingStatus(String classId)
	{
		if(null == mLoaders.get(classId))
		{
			return EnumLoadingStatus.UnLoad;
		}
		return mLoaders.get(classId).getLoadingStatus();
	}
	
	
	public boolean isReachBottom(String classId)
	{
		if(null == mLoaders.get(classId))
		{
			addLoader(classId);
		}
		return mLoaders.get(classId).isReachBottom();
	}
	
	
	protected void onDataRefresh(String classId, ArrayList<T> newT, Object... objects) {}
	
	
	@Override
	public void release() {
		super.release();
		for(Map.Entry<String, FenYeLoader<T>> entrySet : mLoaders.entrySet())
		{
			FenYeLoader<T> loader = entrySet.getValue();
			if(null != loader)
				loader.release();
		}
		mLoaders.clear();
	}
	
	
	/**
	 * 释放指定FenYeLoader
	 * @param classId
	 */
	public void release(String classId)
	{
		if(null != mLoaders.get(classId))
		{
			mLoaders.get(classId).release();
			mLoaders.remove(classId);
		}
	}

}
