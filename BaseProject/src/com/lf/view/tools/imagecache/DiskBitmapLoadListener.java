package com.lf.view.tools.imagecache;

import android.graphics.Bitmap;

/**
 * 加载磁盘上图片的接口
 * @author ludeyuan
 *
 */
public interface DiskBitmapLoadListener {
	
	/**
	 * 加载结束
	 * @param bitmap
	 * @param tag
	 */
	public void loadOver(Bitmap bitmap,String tag);
}
