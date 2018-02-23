package com.lf.controler.tools.download;

import java.io.InputStream;

/**
 * 下载过程监听
 * 
 * @author ww
 * 
 */
public interface DownloadListener {

	// 下载结束后的状态
	public static final int SUCCESS = DownloadSharedState.STATE_SUCCESS; // 下载成功
	public static final int DOWNLOAD_REPEAT = DownloadSharedState.STATE_DOWNLOAD_REPEAT; // 下载失败：重新下载
	public static final int SERVER_ERR = DownloadSharedState.STATE_SERVER_ERR; // 服务器错误
	public static final int DOWNLOAD_ERR = DownloadSharedState.STATE_DOWNLOAD_ERR; // 下载过程中出现的错误
	public static final int INTERRUPTED = DownloadSharedState.STATE_INTERRUPTED; // 中断下载
	public static final int NET_ERR = DownloadSharedState.STATE_NET_ERR; // 网络异常
	public static final int TOAST = DownloadSharedState.STATE_TOAST;// 需要提示的错误

	/**
	 * 下载开始
	 * 
	 * @param id
	 *            下载任务的id
	 */
	public void onDownloadStart(DownloadTask task);

	/**
	 * 下载进度的更新
	 * 
	 * @param id
	 *            下载任务的id
	 * @param progress
	 *            下载进度
	 */
	public void onDownloadRefresh(DownloadTask task, int progress);

	/**
	 * 下载完成
	 * 
	 * @param flag
	 *            下载是否成功或者失败的原因的标识 取值：SUCCESS ，NO_NET， TIME_OUT
	 * @param id
	 *            下载任务的id
	 * @param is
	 *            下载下来的内容
	 */
	public void onDownloadOver(int flag, DownloadTask task, InputStream is);

	/**
	 * 下载暂停
	 * 
	 * @param id
	 *            下载任务的id
	 */
	public void onDownloadPause(DownloadTask task);
}