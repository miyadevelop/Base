package com.lf.tools.log;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lf.controler.tools.NetWorkManager;

import java.io.File;
import java.util.List;

/**
 * 用户手机的基本信息
 * @author ludeyuan
 *
 */
public class MobileMessage {
	private Context mContext;
	private String mTag;
	private MobileLogInterface mLogInterface;
	
	public MobileMessage(Context context,String tag){
		mContext = context.getApplicationContext();
		mTag = tag;
		
		//第一次调用的时候，输出用户的基本信息
		i("Model:"+android.os.Build.MODEL);//输出机型
		i("Version:"+android.os.Build.VERSION.RELEASE);//输出系统版本

		WindowManager windowManager = (WindowManager)mContext.getSystemService(
				Context.WINDOW_SERVICE);
		Display display = windowManager.getDefaultDisplay();
		DisplayMetrics dm = new DisplayMetrics();
		display.getMetrics(dm);
		i("Resolution:"+ dm.widthPixels +"*" +dm.heightPixels);//输出手机分辨率

		TelephonyManager teleManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			i("IMEI:"+teleManager.getDeviceId());	//输出手机IMEI
		}catch (Exception e)
		{
			e.printStackTrace();
		}
		i("Jailbreak:"+isRoot());//输出手机越狱
		//输出内存信息
		logMemoryInfo();
		logSDCardInfo();				//输出Sd卡变化的信息
		//TODU输出网络的信息，并监听网络变化操作
		IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		mContext.registerReceiver(mLogBroadcastReceiver, intentFilter);
		
	}

	/**
	 * @param tag 输出用户基本信息的tag、以及输出时候的key
	 
	public static MobileMessageManager getMobileMessageManager(Context context,String tag){
		if(mMobileMessageManager==null){
			mMobileMessageManager = new MobileMessageManager(context,tag);
		}
		return mMobileMessageManager;
	}
	*/
	
	/**
	 * 输出用户（手机）变化的信息：软件占用的内存、SD(是否存在、剩余的空间)
	 */
	@SuppressLint("NewApi")
	public void logSDCardInfo(){
		

		String sdState = Environment.getExternalStorageState();
		String sdAvailCount = null;
		//有sd卡
		if(Environment.MEDIA_MOUNTED.equals(sdState)){
			File sdCardDir = Environment.getExternalStorageDirectory();
			StatFs statFs = new StatFs(sdCardDir.getPath());
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2){
				sdAvailCount = statFs.getBlockSizeLong() * statFs.getAvailableBlocksLong() / 1024 /1024+ "MB";
			}else{
				sdAvailCount = statFs.getBlockSize() * statFs.getAvailableBlocks() / 1024 /1024+ "MB";
			}
		}
		if(sdAvailCount == null){
			i("SD:不存在");//输出SD的信息（是否存在、剩余内存的大小）
		}else{
			i("SD:"+sdAvailCount);//输出SD的信息（是否存在、剩余内存的大小）
		}
	}
	
	/**
	 * 输出并保存Log信息
	 * @param content
	 */
	private void i(String content){
		if(mLogInterface!=null){
			mLogInterface.mobileLog(mTag, content);
		}
	}

	/**
	 * 判断当前手机是否有ROOT权限
	 * @return
	 */
	public boolean isRoot(){
		boolean bool = false;

		try{
			//没有root文件
			if ((!new File("/system/bin/su").exists()) && (!new File("/system/xbin/su").exists())){
				bool = false;
			//有root文件
			} else {
				bool = true;
			}
		} catch (Exception e) {

		} 
		return bool;
	}

	
	//输出内存信息
	public void logMemoryInfo() { 
		new Thread(){
			@Override
			public void run() {
				ActivityManager mActivityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE); 
				//获得系统里正在运行的所有进程 
				List<RunningAppProcessInfo> runningAppProcessesList = mActivityManager.getRunningAppProcesses(); 

				for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcessesList) { 
					// 进程ID号 
					int pid = runningAppProcessInfo.pid; 
					//  当前进程号
					int mypid = android.os.Process.myPid();
					//如果是当前进程
					if(mypid == pid){
						// 占用的内存 
						int[] pids = new int[] {pid}; 
						Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(pids); 
						int memorySize = memoryInfo[0].dalvikPrivateDirty; 
						i("SoftWareOccupanyMemory:"+memorySize+"kb");//输出软件占用内存大小
						break;
					}
				} 
			};
		}.start();

	}
	
	BroadcastReceiver mLogBroadcastReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			//网络变化(输出log信息)
			if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){
				i("net:" + NetWorkManager.getInstance(context).isConnect());
			}
		}
	};
	
	public void setLogInterfaceListener(MobileLogInterface logInterface){
		if(mLogInterface==null)
			mLogInterface = logInterface;
	}
	
	public interface MobileLogInterface{
		public void mobileLog(String tag,String content);
	}
}
