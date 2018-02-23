package com.lf.view.tools.imagecache;

import com.lf.controler.tools.BitmapUtils;
import com.lf.controler.tools.BitmapUtils.BitmapOptions;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * 从Assets下面获取到图片 
 * @author ludeyuan
 *
 */
public class AssetsBitmapFetch extends BitmapFetch{

	public AssetsBitmapFetch(Context context, BitmapStatus status, String url,
			String saveFolder, BitmapOptions options) {
		super(context, status, url,null, saveFolder, options);
	}

	@Override
	public void bitmap(final CallBack<Bitmap> cb) {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				cb.onResult(BitmapUtils.getBitmapFromAssets(context,url, options));
			}
		});
		thread.start();
	}

}
