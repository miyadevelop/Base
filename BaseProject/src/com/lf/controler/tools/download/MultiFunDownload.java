package com.lf.controler.tools.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.os.Environment;

import com.lf.controler.tools.ApkUtil;
import com.lf.controler.tools.MyEncryption;
import com.lf.controler.tools.NetWorkManager;
import com.mobi.tool.MyR;

import java.io.File;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * 多功能下载，用于下载APK后的自动安装和自动开启
 * 
 * @author LinChen
 * 
 */
public abstract class MultiFunDownload implements DownloadListener {
	/**
	 * 自动下载的状态值
	 */
	private int isAutoDown;
	/**
	 * 空闲
	 */
	public static final int STATE_IDLE = -10;
	/**
	 * 正在下载
	 */
	public static final int STATE_DOWNLOADING = -11;
	/**
	 * 暂停下载
	 */
	public static final int STATE_PAUSE = -12;
	/**
	 * 下载完成
	 */
	public static final int STATE_DOWNLOADED = -13;
	/**
	 * 下载失败
	 */
	public static final int STATE_FAIL = -14;

	private boolean isAutoInstall = true;

	private boolean isAutoStartApk = true;

	private boolean isShowNotification = true;

	protected Context context;
	protected DownloadTask downloadTask;
	protected String downloadPath;
	private int downloadState = STATE_IDLE;
	/**
	 * 如果需要安装完成后自动打开，需要设置包名
	 */
	private String packageName;
	/**
	 * 自动开启的监听
	 */
	private AutoStart autoStartReceiver;
	/**
	 * 自动开启监听的销毁线程
	 */
	private Thread autoStartThread;
	/**
	 * 监听存活的最大时间
	 */
	private static final long listenerAliveTime = 1000 * 60 * 20;

	// 通知栏
	private int notifyID;
	private Notification notification;
	private NotificationManager notificationManager;

	/**
	 * 下载监听
	 */
	private MultiDownloadListener downloadListener;

	private static Set<String> autoStartSet = new HashSet<String>();

	public MultiFunDownload(Context context, String url) {
		this.context = context.getApplicationContext();

		downloadPath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+ File.separator + MyEncryption.genMD5(url) + ".apk";
		downloadTask = new DownloadTask();
		downloadTask.mId = url;
		downloadTask.mIsBreakPoint = true;
		downloadTask.mUrl = url;
		downloadTask.mPath = downloadPath;
	}

	/**
	 * 设置下载后文件的保存位置
	 * 
	 * @param downloadPath
	 */
	public void setDownloadFileName(String downloadPath) {
		this.downloadPath = downloadPath;
		downloadTask.mPath = downloadPath;
	}

	@SuppressWarnings("deprecation")
	public void start() {
		if (downloadState == STATE_PAUSE) {
			keepOn();
			return;
		}
		// 初始化自动打开的监听
		synchronized (autoStartSet) {
			if (autoStartReceiver == null && !autoStartSet.contains(downloadPath)) {
				autoStartSet.add(downloadPath);
				IntentFilter filter = new IntentFilter();
				filter.addAction(Intent.ACTION_PACKAGE_ADDED);
				filter.addDataScheme("package");
				autoStartReceiver = new AutoStart(context, downloadPath);
				context.registerReceiver(autoStartReceiver, filter);

				autoStartThread = new Thread(new Runnable() {
					String tempPath = downloadPath;

					@Override
					public void run() {
						try {
							Thread.sleep(listenerAliveTime);
							synchronized (autoStartSet) {
								autoStartSet.remove(tempPath);
							}
							context.unregisterReceiver(autoStartReceiver);
						} catch (Exception e) {
							// 有可能在这销毁之前已经被销毁了
						}
					}
				});
				autoStartThread.start();
			}
		}

		if (downloadListener != null) {
			downloadListener.onDownloadStart(downloadTask);
		}

		if (packageName != null && ApkUtil.isInstall(context, packageName)) {
			if (downloadListener != null) {
				downloadListener.onDownloadOver(DownloadListener.SUCCESS, downloadTask, null);
			}
			if (isAutoStartApk) {
				Intent start = ApkUtil.getLaunchIntent(context, packageName);
				context.startActivity(start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
				if (downloadListener != null) {
					downloadListener.onStartActivity(downloadTask);
				}
			}

		} else if (ApkUtil.isComplete(context, downloadPath)) {

			downloadState = STATE_DOWNLOADED;
			if (downloadListener != null) {
				downloadListener.onDownloadOver(DownloadListener.SUCCESS, downloadTask, null);
			}
			if (packageName != null) {// 有包名，尝试去打开应用
				Intent start = ApkUtil.getLaunchIntent(context, packageName);
				if (start != null) {
					context.startActivity(start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
					if (downloadListener != null) {
						downloadListener.onStartActivity(downloadTask);
					}
					return;
				}
			} else {
				PackageInfo info = ApkUtil.getPackageInfo(context, downloadPath);
				if (info != null) {
					Intent start = ApkUtil.getLaunchIntent(context, info.packageName);
					if (start != null) {
						context.startActivity(start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
						if (downloadListener != null) {
							downloadListener.onStartActivity(downloadTask);
						}
						return;
					}
				}
			}
			if (isAutoInstall) {
				ApkUtil.install(context, downloadPath);
			}
		} else if (!NetWorkManager.getInstance(context).isConnect()) {
			if (downloadListener != null) {
				downloadListener.onDownloadOver(DownloadListener.DOWNLOAD_ERR, downloadTask, null);
			}
			downloadState = STATE_FAIL;
		} else {
			if (isShowNotification && notification == null) {
				notificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				notifyID = new Random().nextInt();
				notification = new Notification(MyR.drawable(context, "icon"), "开始下载",
						System.currentTimeMillis());
				notification.flags = Notification.FLAG_INSISTENT;
				// 将此通知放到通知栏的"Ongoing"即"正在运行"组中
				CharSequence contentTitle = "下载中..."; // 通知栏标题
				CharSequence contentText = "进度：0%";
				// PendingIntent contentIntent = PendingIntent.getActivity
				// (this, 0,new Intent(""), 0);
				Intent notificationIntent = new Intent(
						"com.mobi.entrance.ad_download.notification.no_action");
				PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
						notificationIntent, 0);
//				notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
				notificationManager.notify(notifyID, notification);
			}

			DownloadCenter.getInstance(context).start(downloadTask, this);
			downloadState = STATE_DOWNLOADING;
		}
	}

	public boolean isAutoInstall() {
		return isAutoInstall;
	}

	public void setAutoInstall(boolean isAutoInstall) {
		this.isAutoInstall = isAutoInstall;
	}

	public boolean isAutoStartApk() {
		return isAutoStartApk;
	}

	public void setAutoStartApk(boolean isAutoStartApk) {
		this.isAutoStartApk = isAutoStartApk;
	}

	public boolean isShowNotification() {
		return isShowNotification;
	}

	public void setShowNotification(boolean isShowNotification) {
		this.isShowNotification = isShowNotification;
	}

	public void setDownloadListener(MultiDownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}

	public void pause() {
		DownloadCenter.getInstance(context).pause(downloadTask.mId);
		downloadState = STATE_PAUSE;
	}

	public void keepOn() {
		DownloadCenter.getInstance(context).keepOn(downloadTask.mId);
		downloadState = STATE_DOWNLOADING;
	}

	public void cancel() {
		DownloadCenter.getInstance(context).cancel(downloadTask.mId);
		downloadState = STATE_IDLE;
	}

	class AutoStart extends BroadcastReceiver {
		String installPath;
		Context mContext;

		/**
		 * 把apk路径传入，防止此类占有外部的变量
		 * 
		 * @param path
		 */
		public AutoStart(Context context, String path) {
			mContext = context;
			installPath = path;
		}

		@Override
		public void onReceive(Context context, Intent intent) {

			String dataString = intent.getDataString();
			PackageInfo packageInfo = ApkUtil.getPackageInfo(context, installPath);
			if (packageInfo != null) {
				String pckName = packageInfo.packageName;
				if (dataString.equals("package:" + pckName)) {
					if (downloadListener != null) {
						downloadListener.onInstall(downloadTask);
					}
					if (isAutoStartApk) {
						context.startActivity(ApkUtil.getLaunchIntent(context, pckName));
						if (downloadListener != null) {
							downloadListener.onStartActivity(downloadTask);
						}
						synchronized (autoStartSet) {
							autoStartSet.remove(installPath);
						}
						mContext.unregisterReceiver(this);
					}
				}
			}
		}
	}

	public MultiDownloadListener getDownloadListener() {
		return downloadListener;
	}

	@Override
	public void onDownloadStart(DownloadTask task) {
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onDownloadRefresh(DownloadTask task, int progress) {
		downloadState = progress;
		if (isShowNotification) {
			CharSequence contentTitle = "下载中..."; // 通知栏标题
			CharSequence contentText = "进度：" + progress + "%";
			Intent notificationIntent = new Intent(
					"com.mobi.entrance.ad_download.notification.no_action");
			PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0,
					notificationIntent, 0);
//			notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
			notificationManager.notify(notifyID, notification);
		}

		if (downloadListener != null) {
			downloadListener.onDownloadRefresh(task, progress);
		}
	}

	@Override
	public void onDownloadPause(DownloadTask task) {
		if (downloadListener != null) {
			downloadListener.onDownloadPause(task);
		}
	}

	@Override
	public void onDownloadOver(int flag, DownloadTask task, InputStream is) {
		if (isShowNotification) {
			notificationManager.cancel(notifyID);
		}

		if (flag == DownloadListener.SUCCESS) {
		}

		if (flag == DownloadListener.SUCCESS && isAutoInstall) {
			downloadState = STATE_DOWNLOADED;
			if (ApkUtil.isComplete(context, downloadPath))
				ApkUtil.install(context, downloadPath);
		} else if (flag != DownloadListener.DOWNLOAD_REPEAT) {
			downloadState = STATE_FAIL;
		}

		if (downloadListener != null) {
			downloadListener.onDownloadOver(flag, task, is);
		}

	}

	public boolean isDownloading() {
		if (downloadState == STATE_DOWNLOADING || downloadState > 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isDownloadFinish() {
		return ApkUtil.isComplete(context, downloadPath);
	}

	public int getDownloadState() {
		return downloadState;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public int getAutoDown() {
		return isAutoDown;
	}

	public void setAutoDown(int isAutoDown) {
		this.isAutoDown = isAutoDown;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public interface MultiDownloadListener {

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

		/**
		 * 安装完成
		 * 
		 * @param task
		 */
		public void onInstall(DownloadTask task);

		/**
		 * 打开应用的监听
		 * 
		 * @param task
		 */
		public void onStartActivity(DownloadTask task);

	}
}
