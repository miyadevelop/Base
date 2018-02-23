package com.lf.tools.log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;

import com.lf.tool.data_persistence.ArrayDataEditor;
import com.lf.tool.data_persistence.DataEditor;
import com.lf.tool.data_persistence.DataPersistenceUtil;
import com.lf.tool.data_persistence.FileListDataUtil;
import com.lf.tools.log.MobileMessage.MobileLogInterface;

/**
 * 管理输出，包含输出的TAG，存储的位置、名称
 * @author ludeyuan
 *
 */
public class MyLogManager {
	private static MyLogManager mLoginManager;
	private MobileMessage mMobileMessage;//手机基本消息
	public ArrayList<String> mTagCollections;//存放哪些TAG可以输出
	private final String LOG_FILENAME = "LaFeng";//log保存文件时的名称
	private Context mContext; //上下文
	private MyLogManager(){
		mTagCollections = new ArrayList<String>();
		mTagCollections.add("Lafeng");	//默认的TAG,放在集合中的第一个位置

		//自动捕捉异常并打印
//		new UncaughtException(getDefaultTag());//捕捉异常
	}

	public static MyLogManager getInstance(){
		if(mLoginManager==null){
			mLoginManager = new MyLogManager();
		}
		return mLoginManager;
	}

	/**
	 * 初始化用户信息（时间、机型、系统、版本、分辨率、软件占用内存情况、
	 * 		imei、SD卡状况、网络状况、是否越狱等）
	 * @param context
	 */
	public void initMobileMessage(Context context){
		mContext = context.getApplicationContext();
		//判断Log开关 
		LogSwitcherManager.getInsatnce(mContext);
		
		if(mMobileMessage==null){
			//确保对象唯一。这里不用单例，因为输出都在这个类里面实现(需要判断开关)
			logSystemTime();
			mMobileMessage = new MobileMessage(mContext,mTagCollections.get(0));
			mMobileMessage.setLogInterfaceListener(mobileInterface);
		}
	}

	/**
	 * 实现手机用户信息类的回调，在接口中实现回调
	 */
	private MobileLogInterface mobileInterface = new MobileLogInterface() {

		@Override
		public void mobileLog(String tag, String content) {
			i(tag, content);
		}
	};

	/**
	 * 判断当前的tag是否是临时添加
	 * @return true：不是  no：临时添加
	 */
	public boolean tagExist(String tag){
		return mTagCollections.contains(tag);
	}

	/**
	 * 获取默认的tag
	 */
	public String getDefaultTag(){
		return mTagCollections.get(0);
	}

	/**
	 * 添加新的TAG，否则其他的tag则禁止输出
	 * 以集合的形式添加，即使只有一项也是这样
	 */
	public void addSubTag(ArrayList<String> subTags){
		if(subTags==null){
			//做判断空的处理，防止子类传过来空
			return;
		}
		//按顺序添加tag，保证集合中第一项为“Lanfeg(默认的tag)”
		for(String tag :subTags){
			mTagCollections.add(tag);
		}
	}

	/**
	 * 输出当前的系统时间
	 */
	public void logSystemTime(){
		// 输出系统时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		String content = "Time:"+formatter.format(curDate);
		Log.i(mTagCollections.get(0), content);
		saveLogToFile(mTagCollections.get(0), content);
	}

	/**
	 * 输出进程占用内存的大小
	 */
	public void logProcessMemorySize(Context context){
		mContext = context.getApplicationContext();
		LogSwitcherManager.getInsatnce(mContext);
		if(mMobileMessage==null){
			mMobileMessage = new MobileMessage(mContext,getDefaultTag());
			mMobileMessage.setLogInterfaceListener(mobileInterface);
		}
		mMobileMessage.logMemoryInfo();
	}

	/**
	 * 输出用户信息，并保存log信息到文件中
	 */
	public void i(String tag,String content){
		//如果输出时，传递的tag是临时添加（没有经过继承类添加），就不输出、记录
		if(tag==null || !tagExist(tag) || mContext==null ||
				!LogSwitcherManager.getInsatnce(mContext).logShowSwitcher()){
			return;
		}
		logSystemTime();//输出时间
		Log.i(tag, content);

		saveLogToFile(tag, content);
	}

	/**
	 * 输出用户信息，并保存log信息到文件中
	 */
	public void d(String tag,String content){
		//如果输出时，传递的tag是临时添加（没有经过继承类添加），就不输出、记录
		if(tag==null || !tagExist(tag) || mContext==null ||
				!LogSwitcherManager.getInsatnce(mContext).logShowSwitcher()){
			return;
		}
		logSystemTime();//输出时间
		Log.d(tag, content);

		saveLogToFile(tag, content);
	}

	/**
	 * 输出用户信息，并保存log信息到文件中
	 */
	public void e(String tag,String content){
		//如果输出时，传递的tag是临时添加（没有经过继承类添加），就不输出、记录
		if(tag==null || !tagExist(tag) || mContext==null ||
				!LogSwitcherManager.getInsatnce(mContext).logShowSwitcher()){
			return;
		}
		logSystemTime();//输出时间
		Log.e(tag, content);

		saveLogToFile(tag, content);
	}

	/**
	 * 把log信息保存到SD和内存中
	 */
	private void saveLogToFile(String tag,String content){
//		//如果没有Context对象，就不处理
//		if(mContext==null || 
//				!LogSwitcherManager.getInsatnce(mContext).logFileSwitcher())
//			return;
//		
//		//把log信息写入到文件中
//		FileListDataUtil fileListDataUtil = DataPersistenceUtil.getFileListDataUtil(
//				mContext,LOG_FILENAME);
//		
//		ArrayDataEditor arrayEditor = fileListDataUtil.getArrayDataEditor();
//		DataEditor editor = arrayEditor.getAppendDataEditor();
//		editor.putString(tag, content);
//		fileListDataUtil.commit();
	}
}
