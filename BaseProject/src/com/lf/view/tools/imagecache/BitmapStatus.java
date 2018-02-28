package com.lf.view.tools.imagecache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

/**
 * 用于保存一些全局的变量
 * 
 * @author LinChen,code when 2015年3月31日
 */
class BitmapStatus {
	private Handler handler;
	private Bitmap errBitmap;
	private Bitmap loadingBitmap;

	public BitmapStatus(Context context) {
		handler = new Handler(Looper.getMainLooper());
	}

	public Handler getHandler() {
		return handler;
	}

	public Bitmap getErrBitmap() {
		return errBitmap;
	}

	public Bitmap getLoadingBitmap() {
		return loadingBitmap;
	}

	public void setErrBitmap(Bitmap errBitmap) {
		this.errBitmap = errBitmap;
	}

	public void setLoadingBitmap(Bitmap loadingBitmap) {
		this.loadingBitmap = loadingBitmap;
	}

}
