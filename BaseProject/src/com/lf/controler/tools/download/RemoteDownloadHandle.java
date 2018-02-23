package com.lf.controler.tools.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * 此类用于接收下载的任务广播，放在主进程中，避免子进程杀死后就不下载了
 * 
 * @author LinChen
 * 
 */
public class RemoteDownloadHandle {
	public static String ACTION_REMOTEDOWNLOAD = "com.mobi.ACTION_REMOTEDOWNLOAD";

	private static RemoteDownloadHandle instance;

	private RemoteDownloadHandle(Context context) {
		final Context mContext = context.getApplicationContext();

		IntentFilter filter = new IntentFilter(ACTION_REMOTEDOWNLOAD
				+ mContext.getPackageName());
		mContext.registerReceiver(new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String uri = intent.getStringExtra("download_url");
				if (uri != null) {
					NotificationAdDownload nad = new NotificationAdDownload(
							mContext, uri);
					nad.start();
				}
			}
		}, filter);
	}

	public synchronized static void init(Context context) {
		if (instance == null) {
			instance = new RemoteDownloadHandle(context);
		}
	}

	public static void notifyDownload(Context context, String url) {
		Intent intent = new Intent(RemoteDownloadHandle.ACTION_REMOTEDOWNLOAD
				+ context.getPackageName());
		intent.putExtra("download_url", url);
		context.getApplicationContext().sendBroadcast(intent);
	}
}
