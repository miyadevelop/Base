package com.lf.tools.share;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

import com.lf.controler.tools.MyMD5;
import com.lf.controler.tools.download.DownloadCenter;
import com.lf.controler.tools.download.DownloadListener;
import com.lf.controler.tools.download.DownloadTask;
import com.umeng.socialize.media.UMImage;

/**
 * 分享的图片
 * 
 * @author LinChen,code when 2015年2月11日
 */
public class ShareImage extends UMImage {

	public static final int TYPE_HTTP = 1;
	public static final int TYPE_DRAWABLE = 2;
	public static final int TYPE_FILE = 3;
	public static final int TYPE_BYTE = 4;
	public static final int TYPE_BITMAP = 5;

	private File cacheFile;

	/**
	 * 图片类型
	 */
	private int imageType = 0;

	/**
	 * 分享网络图片，必须是http开头
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ShareImage(Context arg0, String arg1) {
		super(arg0, arg1);
		cacheFile = new File(getSaveFolder(arg0), MyMD5.generator(arg1) + ".png");
		cacheFile.getParentFile().mkdirs();
		DownloadTask task = new DownloadTask();
		task.mId = System.currentTimeMillis();
		task.mPath = cacheFile.getAbsolutePath();
		task.mTag = System.currentTimeMillis();
		task.mUrl = arg1;
		if (!cacheFile.exists()) {
			DownloadCenter.getInstance(arg0).start(task, new DownloadListener() {

				@Override
				public void onDownloadStart(DownloadTask task) {

				}

				@Override
				public void onDownloadRefresh(DownloadTask task, int progress) {

				}

				@Override
				public void onDownloadPause(DownloadTask task) {

				}

				@Override
				public void onDownloadOver(int flag, DownloadTask task, InputStream is) {
					if (flag != DownloadListener.SUCCESS) {
						new File(task.mPath).delete();
					}
				}
			});
		}
		imageType = TYPE_HTTP;
	}

	/**
	 * 分享drawable下的资源图片
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ShareImage(Context arg0, int arg1) {
		super(arg0, arg1);
		cacheFile = new File(getSaveFolder(arg0), arg1 + ".png");
		cacheFile.getParentFile().mkdirs();
		try {
			BitmapFactory.decodeResource(arg0.getResources(), arg1).compress(CompressFormat.PNG,
					75, new FileOutputStream(cacheFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imageType = TYPE_DRAWABLE;
	}

	/**
	 * 分享图片文件
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ShareImage(Context arg0, File arg1) {
		super(arg0, arg1);
		cacheFile = new File(getSaveFolder(arg0), arg1.getName() + ".png");
		cacheFile.getParentFile().mkdirs();
		try {
			BitmapFactory.decodeFile(arg1.getAbsolutePath()).compress(CompressFormat.PNG, 75,
					new FileOutputStream(cacheFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imageType = TYPE_FILE;
	}

	/**
	 * 分享图片
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ShareImage(Context arg0, byte[] arg1) {
		super(arg0, arg1);
		cacheFile = new File(getSaveFolder(arg0), System.currentTimeMillis() + ".png");
		cacheFile.getParentFile().mkdirs();
		try {
			BitmapFactory.decodeByteArray(arg1, 0, arg1.length).compress(CompressFormat.PNG, 75,
					new FileOutputStream(cacheFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imageType = TYPE_BYTE;
	}

	/**
	 * 分享图片
	 * 
	 * @param arg0
	 * @param arg1
	 */
	public ShareImage(Context arg0, Bitmap arg1) {
		super(arg0, arg1);
		cacheFile = new File(getSaveFolder(arg0), System.currentTimeMillis() + ".png");
		cacheFile.getParentFile().mkdirs();
		try {
			arg1.compress(CompressFormat.PNG, 75, new FileOutputStream(cacheFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		imageType = TYPE_BITMAP;
	}

	/**
	 * 获取本地的图片
	 * 
	 * @throws IOException
	 */
	public File getImageFile() throws IOException {
		if (cacheFile == null || !cacheFile.exists()) {
			throw new IOException("分享图片文件不存在");
		} else {
			return cacheFile;
		}
	}

	/**
	 * 保存缓存图片的目录
	 * 
	 * @param context
	 * @return
	 */
	private File getSaveFolder(Context context) {
		File root = new File(context.getExternalFilesDir(null) + File.separator + ".share_image");
		root.mkdirs();
		return root;
	}

	public int getImageType() {
		return imageType;
	}

}
