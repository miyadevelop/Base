package com.lf.tools.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Environment;
import android.view.View;

/**
 * 用户分享消息和图片
 * @author ludeyuan
 *
 */
public class LogShareManager {
	private static LogShareManager mLogShareManager;
	private long mLastNoticeTime=0;//上一次通知的时间,处理改变一次音量，多次发送广播的情况
	private int mMixVolum=0,mMaxVolum;//系统的嘴小、大音量
	private LogShareManager(Context context){
		
		//先记录当前的音量，监听到后比较得出按了“＋”、“－”键
		AudioManager activityManager = (AudioManager)context.getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolum = activityManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
		mLastNoticeTime = System.currentTimeMillis();
		//监听系统的声音键
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.media.VOLUME_CHANGED_ACTION");
		context.registerReceiver(mReceiver, intentFilter);
	}
	
	public static LogShareManager getInstance(Context context){
		if(mLogShareManager==null){
			mLogShareManager = new LogShareManager(context);
		}
		return mLogShareManager;
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("android.media.VOLUME_CHANGED_ACTION")){//接收到音量键的点击
				long curTime = System.currentTimeMillis();
				
				int preVolum = intent.getIntExtra("android.media.EXTRA_PREV_VOLUME_STREAM_VALUE",-1);
				int curVolum = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",-1);
				if(curVolum>preVolum){//点击了音量键“＋”,需要实现分享功能
					mLastNoticeTime = System.currentTimeMillis();
					shareLogFile(context);
					return;
				}else if(curTime<preVolum){//点击了音量键“－”，需要实现截图功能
					mLastNoticeTime = System.currentTimeMillis();
					captureScreen(context);
					return;
				}
				
				//第三种情况，音量为0或者最大
				//如果前后两次接收广播的时间间隔没有超过0.2s，说明是改变一次音量发出的，就不处理
				if(curTime-mLastNoticeTime<200)
					return;
				
				if(curVolum==mMixVolum){//用户按了音量键“－”，实现截屏
					mLastNoticeTime = System.currentTimeMillis();
					captureScreen(context);
				}else if(curVolum==mMaxVolum){//用户按了音量键“＋”,需要实现分享功能
					mLastNoticeTime = System.currentTimeMillis();
					shareLogFile(context);
				}
			}
		}
	};
	
	/**
	 * 分享Log文件
	 */
	private void shareLogFile(Context context){
		//广告开关关的时候，不实现点击音量键分享的功能
		if(!LogSwitcherManager.getInsatnce(context).shareSwitcher())
			return;
	}
	
	/**
	 * 截图
	 */
	private void captureScreen(Context context){
		//截图后的路径(SD)：/Lafeng/包名/月份＋日期＋小时＋分钟＋秒数.jpg
		//获取存储的路径
		String savePicturePath = Environment.getExternalStorageDirectory()
				+File.separator+"Lafeng"+File.separator
				+context.getPackageName()+File.separator;
		if(!new File(savePicturePath).exists()){//文件夹不存在，先创建文件夹
			new File(savePicturePath).mkdirs();
		}
		
		//获取当前的时间,生成图片的名称
		SimpleDateFormat formatter = new SimpleDateFormat("MMddHHmmss",Locale.CHINA);
		Date curDate = new Date(System.currentTimeMillis());
		String savePictureName = formatter.format(curDate)+".png";
		try {
			new File(savePicturePath+savePictureName).createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		View view = ((Activity)context).getWindow().getDecorView();
		view.buildDrawingCache();
		
		Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache(), 0,
                20, 720, 1000);
		FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savePicturePath+savePictureName);
            if (fos != null) {
                // 第一参数是图片格式，第二个是图片质量，第三个是输出流
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                // 用完关闭
                fos.flush();
                fos.close();
            }
        }catch(Exception e){
        	e.printStackTrace();
        }
        
	}
}
