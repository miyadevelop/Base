package com.lf.view.tools.imagecache;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 图片的抓取类
 * 
 * @author LinChen,code when 2015年3月31日
 */
abstract class BitmapFetch {

	protected String url;
	protected String mBitmapName;	//图片的名称
	protected Context context;
	protected BitmapStatus status;
	protected String saveFolder;
	protected BitmapOptions options;	//修改：添加生成图片的时候的option

	public BitmapFetch(Context context, BitmapStatus status, String url,String bitmapName, String saveFolder,BitmapOptions options) {
		this.context = context;
		this.status = status;
		this.url = url;
		this.saveFolder = saveFolder;
		this.options = options;
		this.mBitmapName = bitmapName;
	}

	public abstract void bitmap(CallBack<Bitmap> cb);

}
