package com.lf.view.tools.imagecache;

import android.graphics.Bitmap;

/**
 * 请求获取图片的回调
 * 
 * @author LinChen,code when 2015年4月3日
 */
public interface BitmapRequestCallBack {
	public void onResult(Bitmap bitmap, Object obj);
}
