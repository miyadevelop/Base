package com.lf.view.tools.update;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.lf.controler.tools.JSONObjectTool;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.BeanLoader;
import com.lf.controler.tools.download.helper.NetEnumRefreshTime;
import com.lf.controler.tools.download.helper.NetLoader;
import com.lf.controler.tools.download.helper.NetRefreshBean;
import com.lf.controler.tools.download.helper.Result;

/**
 * 下载更新时，版本的下载器
 * @author ludeyuan
 *
 */
public class UpdateLoader extends BeanLoader<UpdateBean>{
	
	private UpdateNetLoad mNetLoader;
	private DownloadCheckTask mDownloadTask;	//更新请求的任务

	public UpdateLoader(Context context,DownloadCheckTask downloadTask) {
		super(new UpdateBean());
		mDownloadTask = downloadTask;
		mNetLoader = new UpdateNetLoad(context);
		//设置更新的频率，每次都更新
		NetRefreshBean refreshBean = new NetRefreshBean(NetEnumRefreshTime.Time_Second);
		refreshBean.setTimeValue(1);
		mNetLoader.setRefreshTime(refreshBean);
	}
	
	/**
	 * 加载网络端的信息
	 * @param downloadTask 向服务器提交更新请求的任务
	 */
	public void loadUpdateBean(DownloadCheckTask downloadCheckTask){
		mDownloadTask = downloadCheckTask;
		mNetLoader.loadWithParams(null);
	}

	@Override
	protected Result<UpdateBean> onParse(String json){
		
		Result<UpdateBean> result = new Result<UpdateBean>();
		//如果服务器对不同应用的格式不一样，这个方法的实现需要由子类调用
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObjectTool jsonTool = new JSONObjectTool(jsonObject);
			String status = jsonTool.getString("status",null,false);
//			UpdateBean parseBean = new UpdateBean();
			if(status!=null && status.equals("ok")){//获取数据成功
//				parseBean.setStatus(true);
				JSONArray jsonArray = jsonTool.getJSONArray("data",null);
				//获取到第一个对象中的值
				if(jsonArray!=null && !jsonArray.isNull(0)){
					JSONObject insideObject = jsonArray.getJSONObject(0);
					JSONObjectTool insideJsonTool = new JSONObjectTool(insideObject);
					UpdateBean updateBean = new UpdateBean();
					//设置版本信息
					updateBean.setSize(insideJsonTool.getString("size",""));//大小
					updateBean.setUpdateMessages(insideJsonTool.getString("info",""));//新功能介绍
					updateBean.setUpdateUrl(insideJsonTool.getString("apk",""));//更新网址
					updateBean.setVersion(insideJsonTool.getString("version",""));//版本
					updateBean.setMustUpdateCur(insideJsonTool.getInt("status", 1) == 0);
					
					result.mIsSuccess = true;
					result.mBean = updateBean;
					return result;
				}
			}else{//获取更新的数据失败
				result.mIsSuccess = false;
				result.mMessage = jsonTool.getString("message",null);
			}
		}   
		catch (Exception e) {
			result.mIsSuccess = false;
			result.mMessage = e.toString();
		}
//		
//		//用JSON方法解析失败，用XML的方法解析（兼容拉风锁屏）
//		ParseBean<UpdateBean> parseBean = onParsePre(json);
//		if(parseBean==null){
//			parseBean = new ParseBean<UpdateBean>();
//			parseBean.setStatus(false);
//		}
		return result;
	}
	
//	/**
//	 * 针对拉风版本，需要通过xml来解析
//	 * @param json
//	 * @return
//	 */
//	private UpdateBean onParsePre(String str){
//		try {
//			InputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
//			XmlPullParser parser = Xml.newPullParser();
//			parser.setInput(is,"UTF-8");
//			int event=parser.getEventType();
//			UpdateBean parseBean = new UpdateBean();
//			UpdateBean updateBean = null;
//			while(event!=XmlPullParser.END_DOCUMENT){
//				switch (event) {
//				case XmlPullParser.START_TAG:
//					if("name".equals(parser.getName()) && "dasi".equals(parser.nextText())){
//						updateBean = new UpdateBean();
//					}else if(updateBean!=null){//只解析dasi标签（拉风锁屏）
//						if("version".equals(parser.getName())){
//							updateBean.setVersion(parser.nextText());
//						}else if("info".equals(parser.getName())){
//							updateBean.setUpdateMessages(parser.nextText());
//						}else if("url".equals(parser.getName())){
//							updateBean.setUpdateUrl(parser.nextText());
//						}
//					}
//					break;
//				case XmlPullParser.END_TAG:
//					if("apk".equals(parser.getName()) && updateBean!=null){
//						parseBean.setStatus(true);
//						parseBean.setT(updateBean);
//						return parseBean;
//					}
//					break;
//				default:
//					break;
//				}
//				event=parser.next();
//			}
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}

	@Override
	protected void onParseOver(UpdateBean newT,Object...objects) {
		get().copyBean(newT);
	}
	
	@Override
	public String getAction() {
		return mNetLoader.getAction();
	}
	
	/**
	 * 检测更新的网络下载
	 * @author ludeyuan
	 *
	 */
	public class UpdateNetLoad extends NetLoader{
		public UpdateNetLoad(Context context) {
			super(context);
		}
		
		@Override
		public String parse(String jsonStr,Object... objects){
			Result<UpdateBean> result = UpdateLoader.this.parse(jsonStr);
			if(result.mIsSuccess)
			{
				return NetLoader.OK;
			}
			else
			{
				return result.mMessage;
			}
		}
	
		@Override
		public DownloadCheckTask initDownloadTask() {
			//TODO 这里需要返回一个新的对象
			mDownloadTask.mIsSimple = true;
			return mDownloadTask;
		}

//		@Override
//		public HashMap<String, String> broadcastParams(UpdateBean bean) {
//			return null;
//		}
	}
	
	
	@Override
	public void release() {
		super.release();
		if(null != mNetLoader)
			mNetLoader.release();
	}
}
