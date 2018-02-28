package com.lf.view.tools.imagecache;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.lf.controler.tools.download.DownloadCenter;
import com.lf.controler.tools.download.DownloadListener;
import com.lf.controler.tools.download.DownloadTask;

/**
 * 这个是为了解决相同的图片同时下载的问题。重复的DownloadTask会被过滤掉，这样就会导致一些问题
 * 
 * @author LinChen
 *
 */
public class HttpBitmapHunter {

	private static HttpBitmapHunter instance;

	private List<DataHolder> dataHolders;
	private DownloadCenter downloadCenter;

	private HttpBitmapHunter(Context context) {
		dataHolders = new LinkedList<DataHolder>();
		downloadCenter = DownloadCenter.getInstance(context);
	}

	public static HttpBitmapHunter getInstance(Context context) {
		if (instance == null) {
			instance = new HttpBitmapHunter(context);
		}
		return instance;
	}

	public void start(DownloadTask task, CallBack<Bitmap> cb) {
		synchronized (dataHolders) {
			DataHolder holder = new DataHolder(task, cb);
			dataHolders.add(holder);
			downloadCenter.start(task, new InnerDownloadListener());
		}
	}

	/**
	 * 持有下载所需要的数据
	 * 
	 * @author LinChen
	 *
	 */
	class DataHolder {
 
		public DataHolder(DownloadTask task, CallBack<Bitmap> cb) {
			this.task = task;
			this.callBack = cb;
		}

		private DownloadTask task;
		private CallBack<Bitmap> callBack;
	}

	class InnerDownloadListener implements DownloadListener {

		Bitmap bitmap = null;

		public InnerDownloadListener() {
		}

		@Override
		public void onDownloadStart(DownloadTask task) {
		}

		@Override
		public void onDownloadRefresh(DownloadTask task, int progress) {
		}

		@Override
		public void onDownloadOver(int flag, DownloadTask task, InputStream is) {

			if (flag == DownloadListener.SUCCESS) {
				bitmap = BitmapFactory.decodeFile(task.mPath);
				if (bitmap == null) {// 图片文件有了，但是不能解析
					new File(task.mPath).delete();
				}
			} else if (flag == DownloadListener.DOWNLOAD_ERR || flag == DownloadListener.SERVER_ERR
					|| flag == DownloadListener.INTERRUPTED) {
			}

			List<DataHolder> del = new ArrayList<HttpBitmapHunter.DataHolder>();// 需要删除的数据
			synchronized (dataHolders) {
				for (DataHolder holder : dataHolders) {
					DownloadTask holderTask = holder.task;
					if (holderTask.mUrl.equals(task.mUrl)) {// 相同的网址
						del.add(holder);
						holder.callBack.onResult(bitmap);
					}
				}
				for (DataHolder holder : del) {// 移除已经处理过的数据
					dataHolders.remove(holder);
				}
			}
		}

		@Override
		public void onDownloadPause(DownloadTask task) {
		}

	}
}
