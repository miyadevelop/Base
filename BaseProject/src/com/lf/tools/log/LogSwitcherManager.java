package com.lf.tools.log;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;

import com.lf.base.R;

/**
 * Log的控制开关，决定信息是否可以输出、只显示Log信息、只输出log信息等
 * @author ludeyuan
 * 广告开关里面的数据的读取顺序：
 * 	1、先从显示Log的apk中读取；
 * 	2、步骤1没有读取到数据，才从本地读取；
 */
public class LogSwitcherManager {
	private static LogSwitcherManager mManager;
	private boolean mSwitcher;			//总开关,一旦关闭了，整个Log输出就全部关闭
	private boolean mLogShowSwitcher;	//输出到控制台的开关,true时信息可以在编辑工具（Eclipse）显示
	private boolean mLogFileSwitcher;	//输出到文件的开关，true时会在手机SD卡上生成文件
	private boolean mShareSwitcher;		//分享开关  true才有权限分享

	//LogDisplay工程中提供外部访问程序的接口,以及参数
	private final String CONTENT_URL="content://com.lf.logdisplay/log_switcher";
	private final String SWITCHER_PACKAGENAME="packagename";
	private final String SWITCHER_TOTAL = "totalswitcher";
	private final String SWITCHER_LOG_SHOW = "logshowswitcher";
	private final String SWITCHER_FILE_SHOW = "logfileswitcher";
	private final String SWITCHER_SEND = "logsendswitcher";

	private LogSwitcherManager(Context context){
		//注册监听开关变化的广播
		IntentFilter inflater = new IntentFilter();
		inflater.addAction("com.lf.logdisplay.switcher.change");
		context.getApplicationContext().registerReceiver(mReceiver,inflater);

		//1、先从显示Log的apk中读取开关值
		if(refreshSwitcherFromDB(context))
			return;
		/*
		//查询的条件
		String queryCondition = SWITCHER_PACKAGENAME+"=?";
		//查询的条件中的参数（对应？）
		String[] queryParameter = new String[]{context.getPackageName()};
		Cursor cursor = null;
		//需要处理分享数据的应用没有安装或者挂起的情况
		try{
			cursor = context.getContentResolver().query(Uri.parse(CONTENT_URL),null
					,queryCondition, queryParameter, null);
		}catch(Exception e){
		}

		if(cursor!=null){
			cursor.moveToFirst();
			if(!cursor.isAfterLast()){
				mSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_TOTAL))!=0?true:false;
				mLogShowSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_LOG_SHOW))!=0?true:false;
				mLogFileSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_FILE_SHOW))!=0?true:false;
				mShareSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_SEND))!=0?true:false;
				cursor.close();

				return;
			}
			cursor.close();
		}
		*/
		//2、步骤1中没有读取到值（无值=/=false）
		mSwitcher = context.getResources().getBoolean(R.bool.log_switcher);
//		int logSwitcher = MyR.string(context,"log_switcher");
//		if(logSwitcher>0){//防止在string.xml中这个字段被删除
//			mSwitcher = Boolean.valueOf(context.getString(logSwitcher));
//		}

		if(!mSwitcher){//如果总开关关闭了，下面的开关就不需要处理了
			return;
		}
		
		mLogShowSwitcher = context.getResources().getBoolean(R.bool.log_show_switcher);
//		int logShow = MyR.string(context,"log_show_switcher");
//		if(logShow>0){//防止在string.xml中这个字段被删除
//			mLogShowSwitcher = Boolean.valueOf(context.getString(logShow));
//		}
		
		mLogFileSwitcher = context.getResources().getBoolean(R.bool.log_file_switcher);
//		int logFile = MyR.string(context,"log_file_switcher");
//		if(logFile>0){//防止在string.xml中这个字段被删除
//			mLogFileSwitcher = Boolean.valueOf(context.getString(logFile));
//		}
		
		mShareSwitcher = context.getResources().getBoolean(R.bool.log_share_switcher);
//		int logShare = MyR.string(context, "log_share_switcher");
//		if(logShare>0){
//			mShareSwitcher = Boolean.valueOf(context.getString(logShare));
//		}
	}

	public static LogSwitcherManager getInsatnce(Context context){
		if(mManager==null){
			mManager = new LogSwitcherManager(context);
		}
		return mManager;
	}

	/**
	 * log是否可以输出到控制台(Eclipse)
	 * @return true:log信息会在Eclipse中显示
	 */
	public boolean logShowSwitcher(){
		return mSwitcher && mLogShowSwitcher;
	}

	/**
	 * log是否可以保存到文件中
	 * @return true:log信息可以保存到文件中
	 */
	public boolean logFileSwitcher(){
		return mSwitcher && mLogFileSwitcher;
	}

	/**
	 * log文件是否可以分享
	 * @return true:用户具有分享的权限
	 */
	public boolean shareSwitcher(){
		return mSwitcher && mShareSwitcher;
	}

	/**
	 * 从数据库中刷新开关状态
	 * return false:初始化开关位失败  true：初始化开关位成功
	 */
	private boolean refreshSwitcherFromDB(Context context){
		//查询的条件
		String queryCondition = SWITCHER_PACKAGENAME+"=?";
		//查询的条件中的参数（对应？）
		String[] queryParameter = new String[]{context.getPackageName()};
		Cursor cursor = null;
		//需要处理分享数据的应用没有安装或者挂起的情况
		try{
			cursor = context.getContentResolver().query(Uri.parse(CONTENT_URL),null
					,queryCondition, queryParameter, null);
		}catch(Exception e){
		}

		if(cursor==null)
			return false;
		//移到第一条数据之前
		cursor.moveToFirst();
		//没有记录
		if(cursor.isAfterLast()){
			cursor.close();
			return false;
		}

		mSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_TOTAL))!=0?true:false;
		mLogShowSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_LOG_SHOW))!=0?true:false;
		mLogFileSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_FILE_SHOW))!=0?true:false;
		mShareSwitcher =cursor.getInt(cursor.getColumnIndex(SWITCHER_SEND))!=0?true:false;
		cursor.close();
		return true;
	}

	/**
	 * 处理接收到开关改变
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("com.lf.logdisplay.switcher.change")){
				refreshSwitcherFromDB(context);
			}
		}
	};
}
