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
public abstract class FenYeMapLoader2<T> extends FenYeLoader<T>{

	//所有的加载各个分类的FenYeLoader，通过分类的id获取对应的FenYeLoader，通过FenYeLoader实现加载、保存数据、提供数据的功能
	private HashMap<String, FenYeLoader<T>> mLoaders = new HashMap<String, FenYeLoader<T>>();
	private Context mContext;


	public FenYeMapLoader2(Context context) {
		super(context);
		mContext = context;
	}


	/**
	 * 加载资源
	 * @param param
	 */
	public void loadResource(LoadParam param){
		String key = param.toString();
		if(null == mLoaders.get(key))
		{
			addLoader(param);
		}
		mLoaders.get(key).loadResource();
	}


	/**
	 * 重新刷新一个指定分类的数据，不理会本地的缓存信息
	 * @param param 加载数据的参数
	 */
	public void refreshResource(LoadParam param){
		String key = param.toString();
		if(null == mLoaders.get(key))
		{
			addLoader(param);
		}
		mLoaders.get(key).refreshResource();
	}


	/**
	 * 获取一个分类下的数据列表
	 * @param param 加载数据的参数
	 * @return 一个分类下的数据列表
	 */
	public ArrayList<T> get(LoadParam param) {
		String key = param.toString();
		if(null == mLoaders.get(key))
		{
			addLoader(param);
		}
		return mLoaders.get(key).get();
	}


	/**
	 * 添加一个分类的Loader
	 * @param param 加载数据的参数
	 */
	private void addLoader(LoadParam downParam)
	{
		MyFenYeLoader loader = new MyFenYeLoader(mContext, downParam);
		loader.setStartMethod(getStartMethod());
		loader.setPageCount(getPageCount());
		loader.setStartIndex(getStartIndex());
		loader.setRefreshTime(getRefreshTime());
		mLoaders.put(downParam.toString(), loader);
	}


	/**
	 * 各个分类的分页加载器，重写FenYeLoader了，在FenYeLoader每次进行加载时，额外加上分类id字段
	 * @author ww
	 *
	 * 2016-5-29
	 */
	public class MyFenYeLoader extends FenYeLoader<T>
	{
		private LoadParam mDownParam;

		public MyFenYeLoader(Context context, LoadParam downParam) {
			super(context);
			mDownParam = downParam;
		}

		@Override
		protected DownloadCheckTask initDownloadTask() {
			DownloadCheckTask task = FenYeMapLoader2.this.initDownloadTask();
			for (Map.Entry<String, String> entry : mDownParam.getParams().entrySet()) {  
				task.addParams(entry.getKey(), entry.getValue());
			} 

			for (Map.Entry<String, String> entry : mDownParam.getPostParams().entrySet()) {  
				task.addPostParams(entry.getKey(), entry.getValue());
			} 

			for (Map.Entry<String, String> entry : mDownParam.getHeadParams().entrySet()) {  
				task.addHead(entry.getKey(), entry.getValue());
			} 

			return task;
		}

		@Override
		protected String getPageIndexNameOnWeb() {
			return FenYeMapLoader2.this.getPageIndexNameOnWeb();
		}

		@Override
		protected String getPageCountNameOnWeb() {
			return FenYeMapLoader2.this.getPageCountNameOnWeb();
		}

		@Override
		protected Result<ArrayList<T>> onParse(String json) {
			return FenYeMapLoader2.this.onParse(json);
		}


		@Override
		public T onParseBean(JSONObject object) {
			return FenYeMapLoader2.this.onParseBean(object);
		}


		@Override
		protected void onDataRefresh(ArrayList<T> newT, Object... objects) {
			FenYeMapLoader2.this.onDataRefresh(mDownParam, newT, objects);
		}
		
		
		@Override
		public String getAction() {
			return FenYeMapLoader2.this.getAction();
		}
	}



	/**
	 * 废弃父类中此函数，在FenYeMapLoader，每次加载时，要提供参数classId，故此函数已无意义
	 * 被loadResource(String classId)取代
	 */
	@Override
	@Deprecated
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
	@Deprecated
	public final void refreshResource(){}


	/**
	 * 废弃父类中此函数，在FenYeMapLoader，每次重新加载时，要提供参数classId，故此函数已无意义
	 * 被onDataRefresh(String classId, ArrayList<T> newT, Object... objects) 取代
	 */
	@Override
	@Deprecated
	protected final void onDataRefresh(ArrayList<T> newT, Object... objects) {}

	//	/**
	//	 * 废弃父类中此函数，在FenYeMapLoader，获取一个指定数据时，要提供参数classId，故此函数已无意义
	//	 */
	//	@Override
	//	public T get(int index) {
	//		return null;
	//	}

	
	/**
	 * 获取指定FenYeLoader的加载状态
	 * @param param
	 * @return
	 */
	public EnumLoadingStatus getLoadingStatus(LoadParam param)
	{
		String key = param.toString();
		if(null == mLoaders.get(key))
		{
			addLoader(param);
		}
		return mLoaders.get(key).getLoadingStatus();
	}


	/**
	 * 获取某个FenYeLoader是否已加载了全部的数据
	 * @param param
	 * @return
	 */
	public boolean isReachBottom(LoadParam param)
	{
		String key = param.toString();
		if(null == mLoaders.get(key))
		{
			addLoader(param);
		}
		return mLoaders.get(key).isReachBottom();
	}


	protected void onDataRefresh(LoadParam param, ArrayList<T> newT, Object... objects) {}


	/**
	 * 释放全部数据
	 */
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
	 * @param param
	 */
	public void release(LoadParam param)
	{
		String key = param.toString();
		if(null != mLoaders.get(key))
		{
			mLoaders.get(key).release();
			mLoaders.remove(key);
		}
	}

}
