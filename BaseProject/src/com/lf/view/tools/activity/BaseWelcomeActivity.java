package com.lf.view.tools.activity;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
/**
 * 欢迎界面
 * 1.控制界面等待时间，时间到即跳转
 * 2.控制加载任务个数，满足个数才能跳转
 * 3.满足等待时间和加载条件，跳转相应界面
 * @author Administrator
 *
 */
public class BaseWelcomeActivity extends AppCompatActivity{
	
	private String mGoToClass = null;	// 跳转到的下一个界面
	private String mGuideClass = null;  //导向界面
	protected Bundle mGoToBundle = null; //跳转可携带的参数
	protected boolean mIsLoadOver = true;	// 标记初始化函数是否执行完,若需要加载完一些内容才能跳转到下一界面
	
	ArrayList<String> mProgressCount = new ArrayList<String>();
	
	private Handler mHandler = new Handler(); //循环执行任务
	private int mShortTime = 2000;	// 启动界面维持的最短时间
	
	private boolean	mIsPause=false; //是否中途离开了欢迎界面,如果中途离开就不执行跳转
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		// mTime后，跳转
		mHandler.postDelayed(mShortRunnable, mShortTime);
	}
	
	
	/**
	 * 设置要跳转到的界面
	 * @param className Activity的完整名称
	 * @param bundle 携带的数据
	 */
	public void setGoToClass(String className,Bundle bundle)
	{
		mGoToClass = className;
		mGoToBundle = bundle;
	}
	
	
	/**
	 * 设置要跳转的导向界面
	 */
	public void setGuideClass(String guideClassName){
		mGuideClass = guideClassName;
	}
	
	
	/**
	 * 设置启动界面维持的最短时间
	 */
	public void setShortTime(int time)
	{
		mShortTime = time;
	}
	
	
	/**
	 * 设置启动界面维持的最长时间
	 */
	public void setLongTime(int time)
	{
		mHandler.postDelayed(mLongRunnable, time);
	}
	
	
	/**
	 * 在维持最短时间后，检查需要加载的内容是否加载完毕
	 */
	private Runnable mShortRunnable = new Runnable() {
		@Override
		public void run() {
			if (mIsLoadOver)
				goToNext();
			else
				mHandler.postDelayed(this, 1000);
		}

	};
	
	
	/**
	 * 在维持最长时间后，将mIsLoadOver置为true
	 */
	private Runnable mLongRunnable = new Runnable() {
		@Override
		public void run() {
			mIsLoadOver = true;
		}
	};
	
	
	/**
	 * goToNext():跳转到下一个界面
	 */
	public void goToNext() {
		
		//如果现在最上面的界面已经不是欢迎界面了，就不处理，留给下次OnResume的时候处理
//		if(!ApplicationBaseManager.isTopActivity(this,"WelcomeActivity"))
//			return;
		if(mIsPause || isFinishing())
			return;
			
		Intent intent = new Intent();
		
		if (mGuideClass != null && isFirstLaunch(this)) {
			intent.setClassName(this, mGuideClass);
			intent.putExtra("go_to_class", mGoToClass);
		}
		else{
			intent.setClassName(this, mGoToClass);
		}
		if(null != mGoToBundle)
			intent.putExtras(mGoToBundle);
		intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		if(isTopActivity(this,"Welcome"));
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
		finish();

	}
	
	
	/**
	 * 设置要加载的项的名称(可自己定义.只要确保与loadProgressDone(progressName)的progressName一致即可)
	 */
	public void addLoadProgressName(String progessName)
	{
		mIsLoadOver = false;
		mProgressCount.add(progessName);
	}


	/**
	 * 增加已加载完的项的个数
	 */
	public void loadProgressDone(String progressName)
	{
		mProgressCount.remove(progressName);
		if(mProgressCount.size() == 0)
			mIsLoadOver = true;
	}
	
	
	@Override
	protected void onResume() {
		if(mIsLoadOver && mIsPause){
			mIsPause=false;
			goToNext();
		}
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		mIsPause = true;
		super.onPause();
	}
	
	
	/**
	 * getMyVersionName：获取版本号名称
	 * @param context
	 * @return
	 */
	private String getMyVersionName(Context context)
	{
		String packageName = context.getPackageName();
		try {
			return context.getPackageManager().getPackageInfo(packageName, 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * isFirstLaunch:根据存在本地的版本号与实际版本号进行对比判断是否是首次启动，
	 * 若是首次，还将实际版本号存本地
	 * @return 
	 */
	private  boolean isFirstLaunch(Context context)
	{
		String version = getMyVersionName(context);
		SharedPreferences sharedPreferences = context.getSharedPreferences("welcome", 0);
		String localVersion = sharedPreferences.getString("version", "");
		if(version.equals(localVersion))
			return false;
		
		sharedPreferences.edit().putString("version", version).commit();
		return true;
	}
	
	
	private boolean isTopActivity(Context context, String className) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
		if (cn.getClassName().contains(className))
			isTop = true;
		return isTop;
	}
	
	
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(mLongRunnable);
		mHandler.removeCallbacks(mShortRunnable);
		super.onDestroy();
		System.gc();
	}
}
