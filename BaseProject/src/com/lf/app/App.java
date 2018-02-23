package com.lf.app;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;

import com.lf.controler.tools.location.Location2;
import com.lf.tools.log.MyLogManager;
import com.mobi.tool.MyR;

;

public class App extends Application{



	public static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		if(null == mContext)
			mContext = this;
		//Log初始化
		MyLogManager.getInstance().initMobileMessage(mContext);
		Location2.getInstance(mContext).start();
	}
	
	
	public static void onCreate(Context applicationContext)
	{
		if(null == mContext)
			mContext = applicationContext;
//		//Log初始化
		MyLogManager.getInstance().initMobileMessage(mContext);
	}


	public static boolean bool(String paramString){
		return mContext.getResources().getBoolean(MyR.bool(mContext, paramString));
	}

	public static int integer(String paramString){
		return mContext.getResources().getInteger(MyR.integer(mContext, paramString));
	}

	public static float dimen(String paramString) {
		return mContext.getResources().getDimension(MyR.dimen(mContext, paramString));
	}

	public static XmlResourceParser anim(String paramString) {
		return mContext.getResources().getAnimation(MyR.anim(mContext, paramString));
	}

	public static int id(String paramString) {
		return MyR.id(mContext, paramString);
	}

	public static Drawable drawable(String paramString) {
		return mContext.getResources().getDrawable(MyR.drawable(mContext, paramString));
	}

	public static int layout(String paramString) {
		return MyR.layout(mContext, paramString);
	}

	public static int style(String paramString) {
		return MyR.style(mContext, paramString);
	}

	public static String string(String paramString) {
		return mContext.getResources().getString(MyR.string(mContext, paramString));
	}

	public static int array(String paramString) {
		return MyR.array(mContext, paramString);
	}

	public static int raw(String paramString) {
		return MyR.raw(mContext, paramString);
	}

	public static int color(String paramString) {
		return mContext.getResources().getColor(MyR.color(mContext, paramString));
	}

	public static XmlResourceParser xml(String paramString) {
		return mContext.getResources().getXml(MyR.xml(mContext, paramString));
	}

	public static int menu(String paramString) {
		return MyR.menu(mContext, paramString);
	}
	
	/**
	 * 查询当前的进程名称
	 * @param context
	 * @return 进程名称
	 */
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : 
			manager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
}
