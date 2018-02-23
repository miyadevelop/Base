package com.lf.controler.tools.download.helper;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;

import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.NetLoader.EnumLoadingStatus;


/**
 * 可被解析的Bean的加载器
 * @author wangwei
 *
 */
public abstract class BeanNetLoader extends BeanLoader<Object>{

	private MyNetLoad mMyNetLoader;//网络加载器

	public BeanNetLoader(Context context, Object t) {
		super(t);
		mMyNetLoader = new MyNetLoad(context);
	}



	/**
	 * 加载数据，如果本地有缓存，并且没有过期，就会直接从本地读取并返回通知
	 */
	public void loadResource(){
		mMyNetLoader.loadWithParams(new HashMap<String, String>());
	}


	/**
	 * 重新刷新数据，不理会本地的缓存信息
	 */
	public void refreshResource(){

		if(mMyNetLoader.getLoadingStaus()==EnumLoadingStatus.Loading){
			//正在加载，就不处理；且updateBean中替换还是更新数据，也会依据这个值
			return;
		}

		mMyNetLoader.refresh(new HashMap<String, String>());
	}



	/**
	 * 设置从网络端加载数据的刷新周期（间隔时间内不会访问服务器）
	 * @param refreshTime 数据从网络端更新的周期
	 */
	protected final void setRefreshTime(NetRefreshBean refreshBean){
		mMyNetLoader.setRefreshTime(refreshBean);
	}


	/**
	 * 获取从网络端加载数据的刷新周期（间隔时间内不会访问服务器）
	 * @return refreshTime 数据从网络端更新的周期
	 */
	protected final NetRefreshBean getRefreshTime(){
		return mMyNetLoader.getRefreshTime();
	}


	public EnumLoadingStatus getLoadingStaus()
	{
		return mMyNetLoader.getLoadingStaus();
	}


	@Override
	protected final void onParseOver(Object newT,Object... objects) {
		if(null != newT)
			mBean = newT;
	}


	/**
	 * 重写onParse，如果子类对解析错误或者正确的的情况没有特殊的处理需求，就不需要重写onParse处理返回值，仅需处理解析正确的结果
	 */
	@Override
	protected Result<Object> onParse(String json) {
		Result<Object> ret = new Result<Object>();
		ret.mBean = new ArrayList<Object>();
		try {

			JSONObject jsonObject = new JSONObject(json);

			String status = jsonObject.getString("status");
			if("ok".equals(status))
			{

				String dataString = jsonObject.getString("data");
				if(!"null".equals(dataString))//data里的内容不为null
				{
					ret.mBean = onParseBean(jsonObject.getJSONObject("data"));
				}
				ret.mIsSuccess = true;
			}
			else
			{
				ret.mIsSuccess = false;
				ret.mMessage = jsonObject.getString("message");
			}
		} catch (Exception e) {

			ret.mIsSuccess = false;
			ret.mMessage = e.toString();
			e.printStackTrace();
		}
		return ret;
	}
	
	
	/**
	 * 解析列表中的Bean，一般情况下，如果子类对整个解析没有特殊需求，就只需重写本方法来处理解析结果。
	 * @param object
	 * @return
	 */
	public Object onParseBean(JSONObject object)
	{
		return null;
	}
	
	
	/**
	 * 释放分页下载器下面的数据
	 */
	public void release(){
		super.release();
		mMyNetLoader.release();
	}


//	/**
//	 * 获取加载完成后，发通知的广播的Action值
//	 */
//	@Override
//	public String getAction() {
//		return mMyNetLoader.getAction();
//	}
	
	
	private String mAction;
	/**
	 * 获取广播发送的名称
	 * @return
	 */
	@Override
	public String getAction() {
		if(null == mAction)
		{
			mAction = new String(initDownloadTask().mUrl);
		}
		return mAction;
	}


	/**
	 * 初始化要进行加载的任务，页码的参数可以不添加进来
	 * @return 下载任务的任务描述
	 */
	protected abstract DownloadCheckTask initDownloadTask();



	public class MyNetLoad extends NetLoader{

		public MyNetLoad(Context context) {
			super(context);
		}

		@Override
		public String parse(String jsonStr,Object... objects) {
			//调用ListLoader中的解析方法
			Result<Object> result = BeanNetLoader.this.parse(jsonStr, objects);

			if(result.mIsSuccess)//解析成功，返回ok
				return NetLoader.OK;
			return result.mMessage;//解释失败，返回失败的原因
		}

		@Override
		public DownloadCheckTask initDownloadTask() {
			//调用FenYeLoader中的initDownloadTask方法
			return BeanNetLoader.this.initDownloadTask();
		}

		@Override
		public String getAction() {
			return BeanNetLoader.this.getAction();
		}
	}

}
