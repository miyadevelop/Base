package com.lf.view.tools.imagecache;

import android.graphics.Bitmap;

/**
 * 获取到图像的bitmap后的回调
 * @author ludeyuan
 *
 */
public interface BitmapLoadedCallBack {
	
	/**
	 * 加载图像，获取到bitmap
	 * @param bitmap
	 * @param url 图像的网络地址
	 */
	public void loadOver(Bitmap bitmap,String url);
}
