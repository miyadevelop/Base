package com.lf.controler.tools.download;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;

public class ApkDownloadManager {
	private static ApkDownloadManager instance;

	private Context context;

	private Map<String, InnerDownload> mother;

	private ApkDownloadManager(Context context) {
		this.context = context.getApplicationContext();
		mother = new HashMap<String, ApkDownloadManager.InnerDownload>();
	}

	public synchronized static ApkDownloadManager getInstance(Context context) {
		if (instance == null) {
			instance = new ApkDownloadManager(context);
		}
		return instance;
	}

	public MultiFunDownload create(String url) {
		InnerDownload download = null;
		synchronized (mother) {
			if ((download = mother.get(url)) == null) {
				download = new InnerDownload(context, url);
				if (!download.isDownloadFinish())// 下载完成的就不用管理的
				{
					mother.put(url, download);
				}
			}
		}
		return download;
	}

	class InnerDownload extends MultiFunDownload {
		String url;

		public InnerDownload(Context context, String url) {
			super(context, url);
			this.url = url;
		}

		@Override
		public void onDownloadOver(int flag, DownloadTask task, InputStream is) {
			release(url);
			super.onDownloadOver(flag, task, is);
		}

		@Override
		public void cancel() {
			release(url);
			super.cancel();
		}
	}

	private void release(String url) {
		synchronized (mother) {
			mother.remove(url);
		}
	}
}
