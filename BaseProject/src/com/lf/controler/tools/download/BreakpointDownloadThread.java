package com.lf.controler.tools.download;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.UnknownHostException;

import android.content.Context;
import android.os.SystemClock;

/**
 * 断点下载一般都是大文件的下载，所以继承ComplexDownloadThread，但是必须完全重载run的方法才行
 * 
 * @author LinChen
 * 
 */
class BreakpointDownloadThread extends ComplexDownloadThread {

	public BreakpointDownloadThread(Context context, DownloadTask task,
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
				if (saveFile != null) {
					downloadLength = saveFile.length();
					connection.addRequestProperty("RANGE", "bytes=" + saveFile.length() + "-");
				}
				int responseCode = connection.getResponseCode();
				if (responseCode == 416)// 服务器不支持断点续传
				{
					downloadLength = 0l;
					saveFile.delete();
				}
				if (responseCode >= 500) {
					flag = DownloadListener.SERVER_ERR;
					runOverListener.onRunOver(this);
					return;
				}
				if (responseCode >= 400 && responseCode != 416) {
					flag = DownloadListener.DOWNLOAD_ERR;
					runOverListener.onRunOver(this);
					return;
				}
				netSize = connection.getContentLength() + downloadLength;
				downloadTask.setFileLength((long) netSize);
				downloadListener.onDownloadStart(downloadTask);
				if (saveFile.length() != downloadTask.getFileLength()) {
					InputStream is = connection.getInputStream();
					// TODO 暂时不给断点下载，最后一个参数改为了false
					resultIs = save(is, saveFile, false, true);
				}
			} catch (InterruptedException e) {
				throwable = e;
				flag = DownloadListener.INTERRUPTED;
				e.printStackTrace();
			} catch (UnknownHostException e) {
				throwable = e;
				flag = DownloadListener.DOWNLOAD_ERR;
				if (saveFile != null) {
					saveFile.delete();
				}
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
				if ((flag != DownloadListener.SUCCESS && saveFile != null)
						|| (saveFile != null && saveFile.length() == 0)) {
					// 如果都支持断点下载了，还需要删除文件吗？？？？？
					// TODO 暂时不开放断点下载
					// saveFile.delete();
				}
				if (flag == DownloadListener.SUCCESS) {
					// 将下载记录的文件删除
					downloadTask.delPropertyFile();
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
			// 这句代码之所以放到这里是因为onRunOver里会把备份文件给还原
			break;
		}
	}
}
