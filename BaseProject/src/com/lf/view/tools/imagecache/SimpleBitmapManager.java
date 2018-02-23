package com.lf.view.tools.imagecache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;

public class SimpleBitmapManager {

	private static SimpleBitmapManager mInstance;

	private SimpleBitmapManager() {
		// TODO Auto-generated constructor stub
	}

	public static SimpleBitmapManager getInstance()
	{
		if(null == mInstance)
			mInstance = new SimpleBitmapManager();
		return mInstance;
	}


	/**
	 * 异步加载图片
	 * @param context 用来区分图片的分组和生成BitmapCell的id值； 可以是Activity上下文或ApplicationContext
	 * @param imagePath 图片的网址、磁盘上的路径或者assets下的目录
	 * @param callBack 图片加载完成后的回调
	 */
	public void getBitmap(final Context context,final String path,final BitmapLoadedCallBack callBack)
	{
		//先设置一个默认的路径
		String imageFolder;//文件存储文件夹的路径
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			imageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ "/"+context.getPackageName()+"/images";
		}else{
			String del = context.getFilesDir().toString().substring(0, context.getFilesDir().toString().lastIndexOf("/"));
			imageFolder = del + "/images";
		}
		BitmapManager.getInstance(context).getBitmap(context, path, null, imageFolder, callBack  , new BitmapOptions());
	}


	public Bitmap getBitmapNow(final Context context,String path)
	{
		//先设置一个默认的路径
		String imageFolder;//文件存储文件夹的路径
		if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
			imageFolder = Environment.getExternalStorageDirectory().getAbsolutePath() 
					+ "/"+context.getPackageName()+"/images";
		}else{
			String del = context.getFilesDir().toString().substring(0, path.lastIndexOf("/"));
			imageFolder = del + "/images";
		}
		return BitmapManager.getInstance(context).getBitmapNow(path, null, imageFolder);
	}


}
