package com.lf.view.tools.imagecache;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 加载图片的工具
 * 
 * <pre>
 * <b>example:</b>
 * BitmapBed.getInstance(getApplicationContext())
 * 		// 设置下载失败后显示的图片
 * 		.setErrBitmap(null)
 * 		// 设置图片下载的根目录，默认的目录为data/data/.../files/image下
 * 		.setSaveFolder(Environment.getExternalStorageDirectory() + "/test");
 * 
 * 		BitmapBed.getInstance(getApplicationContext())
 * 		// 加载的图片网址
 * 		.load("https://www.baidu.com/img/bd_logo1.png")
 * 		// 将图片缩放到指定大小
 * 		.size(100, 50)
 * 		// 异步获取图片，失败时返回null
 * 		.bitmap(new CallBack<Bitmap>() {
 * 
 * 			@Override
 * 			public void onResult(Bitmap t) {
 * 				System.out.println(t);
 * 			}
 * 		});
 * </pre>
 * 
 * @author LinChen,code when 2015年3月31日
 */
public class BitmapBed {

	private static BitmapBed instance;

	private Context context;
	private BitmapStatus bitmapStatus;

	public static BitmapBed getInstance(Context context) {
		if (instance == null) {
			instance = new BitmapBed(context);
		}
		return instance;
	}

	public BitmapBed(Context context) {
		this.context = context;
		bitmapStatus = new BitmapStatus(context);
	}

	/**
	 * 设置图片保存的目录
	 * 
	 * @param folder
	 *            存放图片的绝对路径
	 * @return
	 */
	// public BitmapBed setSaveFolder(String folder) {
	// bitmapStatus.setSaveFolder(folder);
	// return this;
	// }

	/**
	 * 设置出错时显示的图片，比如下载失败后显示的图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public BitmapBed setErrBitmap(Bitmap bitmap) {
		bitmapStatus.setErrBitmap(bitmap);
		return this;
	}

	/**
	 * 设置加载时的图片，暂时不支持
	 * 
	 * @param bitmap
	 * @return
	 */
	public BitmapBed setLoadingBitmap(Bitmap bitmap) {
		bitmapStatus.setLoadingBitmap(bitmap);
		return this;
	}
	
	public BitmapRequest load(String url,String saveFolder){
		return load(url,null, saveFolder, null);
	}
	
	/**
	 * 根据网址请求图片
	 * 
	 * @param url
	 *            图片网址
	 * @return
	 */
	public BitmapRequest load(String url, String bitmapName,String saveFolder){
		return load(url,bitmapName, saveFolder, null);
	}

	/**
	 * 根据网址请求图片
	 * 
	 * @param url
	 *            图片网址
	 * @return
	 */
	public BitmapRequest load(String url,String bitmapName, String saveFolder,BitmapOptions options) {

		BitmapFetch bitmapFetch = null;

		if (url == null || "".equals(url.trim())) {
			bitmapFetch = new NullBitmapFetch(context, bitmapStatus, url, saveFolder,options);
		} else if (url.startsWith("http")) {
			bitmapFetch = new HttpBitmapFetch(context, bitmapStatus, url,bitmapName, saveFolder,options);
		} else if (url.startsWith("assets/")){
			url = url.replace("assets/", "");
			bitmapFetch = new AssetsBitmapFetch(context, bitmapStatus, url, saveFolder, options);
		}else {
			bitmapFetch = new LocalBitmapFetch(context, bitmapStatus, url, saveFolder,options);
		}

		BitmapRequest request = new BitmapRequest(context, bitmapStatus, bitmapFetch);
		return request;
	}
}