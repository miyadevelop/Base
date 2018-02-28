package com.lf.view.tools.imagecache;

import com.lf.controler.tools.BitmapUtils.BitmapOptions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

class LocalBitmapFetch extends BitmapFetch {

	public LocalBitmapFetch(Context context, BitmapStatus status, String url, String saveFolder,BitmapOptions options) {
		super(context, status, url, null,saveFolder,options);
	}

	@Override
	public void bitmap(final CallBack<Bitmap> cb) {
		Thread thread = new Thread(new Runnable() {// 为什么本地已经有了，还要开启一个线程。因为后续可能还要对图片进行缩放，避免影响到主线程

					@Override
					public void run() {
						Bitmap bitmap = BitmapFactory.decodeFile(url);
						cb.onResult(bitmap);
					}
				});
		thread.start();
	}

}
