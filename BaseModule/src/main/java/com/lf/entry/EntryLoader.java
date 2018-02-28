package com.lf.entry;

import android.content.Context;
import android.content.Intent;

import com.lf.controler.tools.JSONObjectTool;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.helper.FenYeMapLoader2;
import com.lf.controler.tools.download.helper.LoadParam;
import com.lf.controler.tools.download.helper.LoadUtils;
import com.lf.controler.tools.download.helper.NetEnumRefreshTime;
import com.lf.controler.tools.download.helper.NetLoader.EnumLoadingStatus;
import com.lf.controler.tools.download.helper.NetRefreshBean;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * 默认的入口加载
 * @author wangwei
 *
 */
public class EntryLoader extends FenYeMapLoader2<Entry>{

	//这里使用静态，是为了保证key的全局唯一性，并且它的生命周期不会随着Loader的释放而结束，protected的访问权限，使得外界不可以随意修改它
	protected static String mKey;
	private Context mContext;
	private String mUrl = "http://www.lovephone.com.cn/entrance/getEntrance.json";//默认的服务器地址
	private String mUserId;//用户的id，用来根据不同的用户id，让服务器返回不同的入口信息
	
	
	public EntryLoader(Context context/*, String entryKey*/) {
		super(context);
		mContext = context.getApplicationContext();
		NetRefreshBean bean = new NetRefreshBean(NetEnumRefreshTime.Server);
		setRefreshTime(bean);
	}


	/**
	 * 加载一个入口集合的数据
	 * @param entryListId 入口集合的id
	 */
	public void load(String entryListId)
	{
		LoadParam param = new LoadParam();
		param.addParams("id", entryListId);
		if(EnumLoadingStatus.Loading ==  getLoadingStatus(param) || EnumLoadingStatus.LoadSuccess ==  getLoadingStatus(param))
			return;
		loadResource(param);
	}


	/**
	 * 获取一个入口集合的数据
	 * @param entryListId 入口集合的id
	 */
	public ArrayList<Entry> get(String entryListId)
	{
		LoadParam param = new LoadParam();
		param.addParams("id", entryListId);
		return get(param);
	}


	@Override
	protected DownloadCheckTask initDownloadTask() {

		DownloadCheckTask task = new DownloadCheckTask();
		task.mUrl = mUrl;
		task.mIsSimple = true;
		LoadUtils.addUniversalParam(mContext, task);
		task.addParams("appKey", mKey);//入口模块的key
		task.addParams("codeVersion", Consts.CODE_VERSION);//入口模块的代码版本
		if(null != mUserId && !"".equals(mUserId))
			task.addParams("userId", mUserId);
		
		return task;
	}

	@Override
	protected String getPageIndexNameOnWeb() {
		return "page";
	}

	@Override
	protected String getPageCountNameOnWeb() {
		return "count";
	}


	@Override
	public Entry onParseBean(JSONObject object) {

		Entry entry = Entry.fromJson(new JSONObjectTool(object));

		if(null == entry)//多个入口集合解析的情况
		{
			try {
				String id = object.getString("id");
				ArrayList<Entry> entries = get(id);
				JSONArray jsonArray = object.getJSONArray("idlist");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject object1 = jsonArray.getJSONObject(i);
					entry = Entry.fromJson(new JSONObjectTool(object1));
					if(null != entry)
						entries.add(entry);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return entry;
	}


	/**
	 * 发送入口数据发生变化的广播，在加载时，会由Loader底层自动发广播，但在入口过滤条件发生变化时，需要通过上层调用这里，主动通知外界变化
	 * @param id
	 */
	public void sendBroadCast(String id)
	{
		Intent intent = new Intent(getAction());
		intent.putExtra("id", id);
		mContext.sendBroadcast(intent);
	}

	
	@Override
	protected void onDataRefresh(LoadParam param, ArrayList<Entry> newT,
			Object... objects) {
		
		for(Entry entry : newT)
		{
			entry.setParentId(param.getParams().get("id"));//设置集合id
		}
		super.onDataRefresh(param, newT, objects);
	}

	
	/**
	 * 修改入口服务器地址的前缀，便于在服务器迁移时，进行适配
	 * @param url
	 */
	public void setUrl(String url)
	{
		mUrl = url;
	}
	
	
	
	public void setUserId(String userId)
	{
		mUserId = userId;
	}
}
