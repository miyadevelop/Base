package com.lf.view.tools.imagecache;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;
import com.lf.view.tools.imagecache.DiskBitmapLoadThread.ThreadRunOver;

/**
 * 磁盘上Bitmap的处理
 * @author ludeyuan
 *
 */
public class DiskBitmapLoadCenter {
	private Context mContext;
	private static DiskBitmapLoadCenter mInstance;
	private ArrayList<DiskBitmapLoadThread> mThreads;//等待获取Bitmap的线程
	private ArrayList<DiskBitmapLoadThread> mRunThread;	//正在加载的线程
	private final int MAX_RUNNING_TASK_COUNT = 3;	//最大的线程数量
	private final int MAX_TAK_COUNT = 30;			//最大的等待执行的进程
	
	private DiskBitmapLoadCenter(Context context){
		mContext = context.getApplicationContext();
		mThreads = new ArrayList<DiskBitmapLoadThread>();
		mRunThread = new ArrayList<DiskBitmapLoadThread>();
	}
	
	public static DiskBitmapLoadCenter getInstance(Context context){
		if(null == mInstance){
			mInstance = new DiskBitmapLoadCenter(context);
		}
		return mInstance;
	}
	
	public void startLoadBitmap(String tag,String imagePathSorce,
			BitmapOptions options,DiskBitmapLoadListener diskListener){
		synchronized (mThreads) {
			DiskBitmapLoadThread loadThread = getThreadIndexOnRun(tag);
			if(loadThread!=null){
				//在当前运行的任务中,替换掉回调函数
				loadThread.changeLoadListener(diskListener);
				return;
			}
			loadThread = getThreadIndex(tag);
			if(loadThread!=null){
				//把任务调整到队尾，最后加载
				mThreads.remove(loadThread);	
			}else{
				//生成线程
				loadThread = new DiskBitmapLoadThread(mContext, tag, imagePathSorce,
						options, diskListener, mRunOver);
			}
			mThreads.add(loadThread);
			//保证线程的数量不超标
			while(mThreads.size()>MAX_TAK_COUNT){
				mThreads.remove(0);
			}
			startThread();
		}
	}
	
	/**
	 * 开启新的线程
	 */
	private void startThread(){
		if(mThreads.size()==0){
			//没有等待的进程，标示全部加载完成
			return;
		}
		if(mRunThread.size() > MAX_RUNNING_TASK_COUNT){
			//超过规定的运行的数量
			return;
		}
		
		DiskBitmapLoadThread thread = mThreads.get(0);
		mThreads.remove(0);
		mRunThread.add(thread);
		thread.start();
	}
	
	/**
	 * 从正在运行的线程中，获取是否有相同的标示
	 * @param tag
	 * @return
	 */
	private DiskBitmapLoadThread getThreadIndexOnRun(String tag){
		if(TextUtils.isEmpty(tag)){
			return null;
		}
		for(int i=0;i<mRunThread.size();i++){
			if(tag.equals(mRunThread.get(i).getTag())){
				return mRunThread.get(i);
			}
		}
		return null;
	}
	
	/**
	 * 从正在等待的线程中，获取是否有相同的标示
	 */
	private DiskBitmapLoadThread getThreadIndex(String tag){
		if(TextUtils.isEmpty(tag)){
			return null;
		}
		
		for(int i=0;i<mThreads.size();i++){
			if(tag.equals(mThreads.get(i).getTag())){
				return mThreads.get(i);
			}
		}
		return null;
	}
	
	//一个线程加载结束了
	private ThreadRunOver mRunOver = new ThreadRunOver() {
		
		@Override
		public void runOver(String tag) {
			synchronized (mThreads) {
				for(DiskBitmapLoadThread thread:mRunThread){
					if(tag.equals(thread.getTag())){
						mRunThread.remove(thread);
						break;
					}
				}
				startThread();
			}
		}
	};
}
