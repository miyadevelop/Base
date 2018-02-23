package com.lf.view.tools.update;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.lf.base.R;
import com.lf.controler.tools.ApkUtil;
import com.lf.controler.tools.MyEncryption;
import com.lf.controler.tools.SoftwareData;
import com.lf.controler.tools.download.ApkDownloadManager;
import com.lf.controler.tools.download.DownloadCheckTask;
import com.lf.controler.tools.download.DownloadListener;
import com.lf.controler.tools.download.DownloadTask;
import com.lf.controler.tools.download.MultiFunDownload;
import com.lf.controler.tools.download.MultiFunDownload.MultiDownloadListener;
import com.lf.controler.tools.download.helper.BaseLoader;
import com.lf.view.tools.CommonDialog;
import com.lf.view.tools.DialogClickListener;
import com.lf.view.tools.DialogManager;
import com.mobi.tool.MyR;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * 软件的版本更新管理
 * @author ludeyuan
 *
 */
public class UpdateManager {
	private Activity mActivity;
	private UpdateLoader mLoader;
	private final String DIALOG_TAG = "update_dialog";//更新的dialog
	private final int NOTIFY_ID = 1000;
	private boolean mAutoUpdate;//自动更新，决定在没有新版本或者更新失败的时候，弹出提示信息
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotifyManager;
	public UpdateManager(Activity activity){
		mActivity = activity;
		mBuilder = new NotificationCompat.Builder(mActivity);
		mNotifyManager = (NotificationManager)mActivity.getSystemService(
				Context.NOTIFICATION_SERVICE);
	}

	/**
	 * 检测是否有更新的版本
	 * @param downloadCheckTask 统一从TaskCenterTest中获取
	 * @param autoUpdate:true 自动更新,更新失败或者最新版本的时候不提示
	 */
	public final void checkUpdate(DownloadCheckTask c,boolean autoUpdate){
		mAutoUpdate = autoUpdate;
		if(mLoader == null){
			mLoader = new UpdateLoader(mActivity, c);
			//注册广播
			IntentFilter inflater = new IntentFilter();
			inflater.addAction(mLoader.getAction());
			mActivity.getApplicationContext().registerReceiver(mReceiver, inflater);
		}
		//调用加载的代码
		mLoader.loadUpdateBean(c);
	}

	/**
	 * 返回更新的消息,可能为空
	 */
	public UpdateBean getUpdateBean(){
		if(mLoader==null)
			return null;

		return mLoader.get();
	}

	/**
	 * 显示“软件更新”的对话框，子类可以覆盖
	 * @param updateBean 新版本中信息
	 */
	@SuppressLint("UseSparseArrays")
	protected void showUpdateDialog(UpdateBean updateBean){
		//显示出更新升级的对话框
		View dialogXml = (View)LayoutInflater.from(mActivity).inflate(
				MyR.layout(mActivity,"base_layout_update_dialog"), null);
		HashMap<Integer, String> idAndValues = new HashMap<Integer,String>();
		idAndValues.put(MyR.id(mActivity,"update_dialog_text_title"),
				mActivity.getString(MyR.string(mActivity,"check_update_title")));
		idAndValues.put(MyR.id(mActivity,"update_dialog_text_message"), updateBean.getUpdateMessage());
		if(TextUtils.isEmpty(updateBean.getSize())){
			idAndValues.put(MyR.id(mActivity,"update_dialog_text_button"),
					mActivity.getString(MyR.string(mActivity,"check_update_btn_update")));
		}else{
			idAndValues.put(MyR.id(mActivity,"update_dialog_text_button"),
					mActivity.getString(MyR.string(mActivity,"check_update_btn_update"))
					+" ("+updateBean.getSize() + ")");
		}

		CommonDialog dialog = DialogManager.getDialogManager().init(mActivity, dialogXml, idAndValues, DIALOG_TAG, mUpdateListener);
		dialog.setCancelable(!updateBean.isMustUpdateCur());
		dialog.onShow();
	}

	/**
	 * dialog上按钮点击的监听
	 */
	private DialogClickListener mUpdateListener = new DialogClickListener() {

		@Override
		public void onDialogItemClick(View view, String dialogId) {

			if(view.getId() == MyR.id(mActivity, "update_dialog_text_button"))
			{
				//默认只有一个按钮，点击后下载新版本apk
				startDownloadApk();
				if(!getUpdateBean().isMustUpdateCur())//不是强制升级，就关闭对话框
					DialogManager.getDialogManager().onCancel(mActivity,DIALOG_TAG);
				else
					mActivity.finish();//是强制升级就关闭当前页面
			}
		}
	};

	/**
	 * 开始下载最新版本的apk
	 */
	public final void startDownloadApk(){
		String downloadPath = Environment
				.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
				+ File.separator + MyEncryption.genMD5(mLoader.get().getUpdateUrl()) + ".apk";
		if (ApkUtil.isComplete(mActivity, downloadPath)) {
			ApkUtil.install(mActivity, downloadPath);
			if (mApkDownLiatener != null)
				mApkDownLiatener.onDownloadOver(DownloadListener.SUCCESS, null, null);
		}else{
			final MultiFunDownload funDownload = ApkDownloadManager.getInstance(
					mActivity.getApplicationContext()).create(mLoader.get().getUpdateUrl());
			funDownload.setShowNotification(false);
			funDownload.setDownloadListener(mApkDownLiatener);
			funDownload.start();
		}


	}

	/**
	 * 下载apk的监听
	 */
	private MultiDownloadListener mApkDownLiatener = new MultiDownloadListener() {

		@Override
		public void onStartActivity(DownloadTask task) {

		}

		@Override
		public void onInstall(DownloadTask task) {

		}

		@Override
		public void onDownloadStart(DownloadTask task) {
			mBuilder.setSmallIcon(MyR.drawable(mActivity,"ic_launcher"));//设置通知栏的图标
			mBuilder.setContentTitle(mActivity.getString(R.string.check_update_title));
			mBuilder.setContentText(mActivity.getString(R.string.down_status_begin));
			mNotifyManager.notify(NOTIFY_ID, mBuilder.build());
		}

		@Override
		public void onDownloadRefresh(DownloadTask task, int progress) {
			mBuilder.setProgress(100, progress, true);
			mNotifyManager.notify(NOTIFY_ID, mBuilder.build());
		}

		@Override
		public void onDownloadPause(DownloadTask task) {

		}

		@Override
		public void onDownloadOver(int flag, DownloadTask task, InputStream is) {
			mNotifyManager.cancel(NOTIFY_ID);
		}
	};

	private BroadcastReceiver mReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {

			if(!isForeground(mActivity, mActivity.getClass().getName()))//界面不在前台
				return;
			//接收到加载软件版本信息
			if(mLoader!=null && intent.getAction().equals(mLoader.getAction())){

				boolean loadStatus = intent.getBooleanExtra(BaseLoader.STATUS,false);
				if(loadStatus){	//从网络上加载数据成功
					
					try {
						//判断当前的版本
						String localVersion = SoftwareData.getAppliactionVersion(context);
						String netVersion = mLoader.get().getVersion();
						long localVersionL = Long.valueOf(localVersion);
						long netVersionL = Long.valueOf(netVersion);
						if(netVersionL>localVersionL){//需要更新
							showUpdateDialog(mLoader.get());
						}else{//不需要更新
							if(mAutoUpdate){//自动检测更新，不提示
								return;
							}
							Toast.makeText(context,context.getString(R.string.check_update_high_version), Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
	
				}else{	//从网络上加载数据失败
					//做下处理，变成检测更新的广播
					if(mAutoUpdate){//自动检测更新，不提示
						return;
					}
					Toast.makeText(context, intent.getStringExtra(BaseLoader.MESSAGE), Toast.LENGTH_SHORT).show();
				}
			}
		}
	};

	/**
	 * 释放对象
	 */
	public final void release(){
		mActivity.getApplicationContext().unregisterReceiver(mReceiver);
		mLoader.release();
		mActivity = null;
	}


	/**
	 * 判断某个界面是否在前台
	 * 
	 * @param context
	 * @param className
	 *            某个界面名称
	 */
	private boolean isForeground(Context context, String className) {
		if (context == null || TextUtils.isEmpty(className)) {
			return false;
		}

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningTaskInfo> list = am.getRunningTasks(1);
		if (list != null && list.size() > 0) {
			ComponentName cpn = list.get(0).topActivity;
			if (className.equals(cpn.getClassName())) {
				return true;
			}
		}

		return false;
	}
}
