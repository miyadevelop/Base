package com.lf.view.tools.imagecache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

import com.lf.controler.tools.BitmapUtils;
import com.lf.controler.tools.BitmapUtils.BitmapOptions;
import com.lf.controler.tools.download.DownloadTask;

public class HttpBitmapFetch extends BitmapFetch {

	public HttpBitmapFetch(Context context, BitmapStatus status,String url,String bitmapName ,String saveFolder,BitmapOptions options) {
		super(context, status, url,bitmapName, saveFolder,options);
	}

	@Override
	public void bitmap(final CallBack<Bitmap> cb) {

//		final String fileName = saveFolder + File.separator + BitmapSimpleName.gen(url);
//		final String fileName = HttpImagePath.getPathOnSD(url, saveFolder);
		final String fileName = HttpImagePath.getPathOnSD(url,mBitmapName, saveFolder);
		final File imageFile = new File(fileName);
		if (imageFile.length() > 0) {// 本地已经存在
			Thread thread = new Thread(new Runnable() {// 为什么本地已经有了，还要开启一个线程。因为后续可能还要对图片进行缩放，避免影响到主线程

						@Override
						public void run() {
							Bitmap bitmap = BitmapUtils.getBitmapFromSD(fileName, options);
							if (bitmap == null) {
								imageFile.delete();
							}
							cb.onResult(bitmap);
						}
					});
			thread.start();

		} else {

			if (url == null || "".equals(url.trim())) {
				cb.onResult(null);
				return;
			}

			DownloadTask task = new DownloadTask();
			task.mUrl = url;
			task.mId = fileName;
			task.mTag = fileName; 
			task.mIsSimple = true;
			task.mPath = fileName;

			HttpBitmapHunter.getInstance(context).start(task, cb);

		}
	}
}
