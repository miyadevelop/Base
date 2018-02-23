package com.lf.controler.tools.download;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.content.Context;
import android.os.SystemClock;

class SimpleDownloadThread extends AbsDownload {
	private int curRetryTime = 0;

	public SimpleDownloadThread(Context context, DownloadTask task,
			DownloadListener downloadListener, RunOverListener runOverListener) {
		super(context, task, downloadListener, runOverListener);
	}

	@Override
	public void run() {
		Throwable throwable = null;
		int flag = DownloadListener.SUCCESS;
		InputStream resultIs = null;
		// 初始化路径
		File saveFile = null;
		while (true) {
			HttpURLConnection connection = null;
			try {
				if (downloadPath != null && !"".equals(downloadPath.trim())) {
					synchronized (DownloadFile.class) {
						saveFile = DownloadFile.create(downloadPath);
					}
				}
				connection = getConn();
				if (connection == null) {
					flag = DownloadListener.DOWNLOAD_ERR;
					runOverListener.onRunOver(this);
					return;
				}
				int responseCode = connection.getResponseCode();
				if (responseCode >= 500) {
					flag = DownloadListener.SERVER_ERR;
					runOverListener.onRunOver(this);
					return;
				}
				if (responseCode >= 400) {
					flag = DownloadListener.DOWNLOAD_ERR;
					runOverListener.onRunOver(this);
					return;
				}
				InputStream is = connection.getInputStream();
				downloadListener.onDownloadStart(downloadTask);
				resultIs = save(is, saveFile, true, false);
			} catch (InterruptedException e) {
				throwable = e;
				flag = DownloadListener.INTERRUPTED;
				e.printStackTrace();
			} catch (Exception e) {
				throwable = e;
				if (++curRetryTime < downloadTask.retryTime) {
					flag = DownloadListener.DOWNLOAD_REPEAT;
					SystemClock.sleep(500l);
					continue;
				} else {
					flag = DownloadListener.DOWNLOAD_ERR;
				}
				e.printStackTrace();
			} finally {
				if (saveFile != null && saveFile.length() == 0) {
					flag = DownloadListener.DOWNLOAD_ERR;
				}
				if (flag != DownloadListener.SUCCESS && saveFile != null) {
					saveFile.delete();
				}
				runOver(flag);
				if (flag == DownloadListener.SUCCESS) {
					// 去除ping的代码
					// WifiTool wifiTool = new WifiTool(context);
					// if (wifiTool.isOn() > 0)// 如果是wifi下，ping一下百度是否可以联网
					// {
					// if (!ConnectionCheck.pingConn()) {
					// flag = DownloadListener.DOWNLOAD_ERR;
					// if (downloadTask.mPath != null) {
					// new File(downloadTask.mPath).delete();
					// }
					// }
					// }
				}
				downloadTask.throwable = throwable;
				downloadListener.onDownloadOver(flag, downloadTask, resultIs);
			}
			isRunning = false;
			runOverListener.onRunOver(this);
			break;
		}
	}
}
