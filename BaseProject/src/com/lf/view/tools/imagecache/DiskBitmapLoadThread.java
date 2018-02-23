package com.lf.view.tools.imagecache;

import android.content.Context;
import android.graphics.Bitmap;

import com.lf.controler.tools.BitmapUtils;
import com.lf.controler.tools.BitmapUtils.BitmapOptions;

/**
 * 磁盘上图片加载的进程
 * @author ludeyuan
 *
 */
public class DiskBitmapLoadThread extends Thread{
	
	private String mTag;		//加载完成后，回调的时候使用，了解当前加载是哪个
	private String mImagePath;	//在磁盘上的路径
	private Context mContext;
	private BitmapOptions mOptions;
	private DiskBitmapLoadListener mListener;//用来通知外界Bitmap加载完成
	private ThreadRunOver mRunOverListener;
	private boolean mImportantTask = false;	//重要的下载任务
	
	public DiskBitmapLoadThread(Context context,String tag,String imagePathSorce,
			BitmapOptions options,DiskBitmapLoadListener listener,ThreadRunOver runOver){
		mTag = tag;
		mImagePath = imagePathSorce;
		mListener = listener;
		mContext = context.getApplicationContext();
		mOptions = options;
		mRunOverListener = runOver;
	}
	
	public void changeLoadListener(DiskBitmapLoadListener listener){
		mListener = listener;
	}
	
	public String getTag(){
		return mTag;
	}
	
	/**
	 * 设置为重要的下载,会优先下载
	 */
	public void setToImportant(){
		mImportantTask = true;
	}
	
	/**
	 * 是否是重要的任务
	 * @return true:重要的任务
	 */
	public boolean isImportantTask(){
		return mImportantTask;
	}
	
	@Override
	public void run() {
		//区分是SD、内存还是Assets下面的文件
		Bitmap bitmap = null;
		if(mImagePath==null){
		}else if(mImagePath.startsWith("assets/")){
			//加载结束后，回调
			bitmap = BitmapUtils.getBitmapFromAssets(
					mContext,mImagePath, mOptions);
		}else{
			bitmap = BitmapUtils.getBitmapFromSD(mImagePath, mOptions);
		}
		mListener.loadOver(bitmap, mTag);
		mRunOverListener.runOver(mTag);
	}
	
	/**
	 * 线程运行结束
	 * @author ludeyuan
	 *
	 */
	public interface ThreadRunOver{
		public void runOver(String tag);
	}
}
