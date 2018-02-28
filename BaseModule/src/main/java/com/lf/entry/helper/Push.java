package com.lf.entry.helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.widget.RemoteViews;

import com.lf.entry.Entry;
import com.lf.entry.EntryManager;
import com.lf.view.tools.imagecache.BitmapLoadedCallBack;
import com.lf.view.tools.imagecache.SimpleBitmapManager;
import com.mobi.tool.MyR;
import com.my.m.R;

import java.util.ArrayList;


/**
 * 推送控制，单例，调用{@link #init(String)}初始化，并自动开始进行推送入口，调用{@link Push#release()释放资源，停止推送}
 * @author wangwei
 *
 */
public class Push {
	private Context mContext;
	private String mEntryListId;//入口集合Id
	private static  Push mInstance;
	public static final String ACTION_CLICK = "com.lf.entry.push.action.click";
	public static final String KEY_ENTRY_ID = "entry_id";
	public static final String KEY_ENTRY_LIST_ID = "entry_list_id";
	//这里之所以不用单例的EntryManager，是因为Push需要全局存在，不会随着app的退出而释放
	public EntryManager mEntryManager;
	
	private Push(Context context) {
		mContext = context.getApplicationContext();
	}


	/**
	 * 单例
	 * @param context
	 * @return
	 */
	public static Push getInstance(Context context)
	{
		if(null == mInstance)
			mInstance = new Push(context);
		return mInstance;
	}


	/**
	 * 根据入口集合id初始化
	 * @param entryListId 入口集合id
	 */
	public void init(String entryListId)
	{
		mEntryManager = new EntryManager(mContext);
		mEntryManager.init();
		
		mEntryListId = entryListId;
		
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(mEntryManager.getAction());
		filter.addAction(ACTION_CLICK);
		mContext.registerReceiver(mReceiver, filter);
		
		ArrayList<Entry> entries = mEntryManager.get(entryListId);

		for(Entry entry : entries)//所有的推送都立即发出
		{
			doPush(entry);
		}

		//加载入口集合
		mEntryManager.load(entryListId);
	}


	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			String action = intent.getAction();

			if(mEntryManager.getAction().equals(action))
			{
				String id = intent.getStringExtra("id");
				if (null != mEntryListId && id.contains(mEntryListId)) {
					ArrayList<Entry> entries = mEntryManager.get(mEntryListId);
					for(final Entry entry : entries)//所有的推送都立即发出
					{
						doPush(entry);
					}
				}
			}
			else if(Push.ACTION_CLICK.equals(action))
			{
				String entryId = intent.getStringExtra(Push.KEY_ENTRY_ID);
				String entryListId = intent.getStringExtra(Push.KEY_ENTRY_LIST_ID);

				ArrayList<Entry> entries = mEntryManager.get(entryListId);
				for(Entry entry : entries)
				{
					if(entry.getId().equals(entryId))
					{
						mEntryManager.goTo(context, entry);
					}
				}
			}
		}
	};


	/**
	 * 执行推送
	 * @param entry
	 */
	private void doPush(final Entry entry)
	{
		BitmapLoadedCallBack callBack = new BitmapLoadedCallBack() {

			@Override
			public void loadOver(final Bitmap bitmap, String url) {

				NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
				Notification mNotification = new Notification();
				mNotification.icon = MyR.drawable(mContext, "ic_launcher");
				mNotification.flags = Notification.FLAG_AUTO_CANCEL; 
				RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.base_entry_layout_notification);
				remoteViews.setImageViewBitmap(R.id.image, bitmap);
				remoteViews.setTextViewText(R.id.title, entry.getText());
				String subTitle = entry.getExtra("subTitle");
				if(null != subTitle)
				{
					remoteViews.setTextViewText(R.id.subtitle, subTitle);
				}
				mNotification.contentView = remoteViews;
				Intent notificationIntent = new Intent(ACTION_CLICK);
				notificationIntent.putExtra(KEY_ENTRY_ID, entry.getId());
				notificationIntent.putExtra(KEY_ENTRY_LIST_ID, mEntryListId);
				//第二个参数起到区分广播的作用，第四个参数使得每次广播不会被覆盖
				PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, Long.valueOf(System.currentTimeMillis()).intValue(), notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				mNotification.contentIntent = contentIntent;

				mNotificationManager.cancel(entry.getId(), 1);
				mNotificationManager.notify(entry.getId(), 1, mNotification);
				//入口操作行为统计，这里表示记录该入口的显示事件
				mEntryManager.onShow(entry);

			}
		};

		//先加载要显示的图片，加载成功后，才能把推送显示出来
		SimpleBitmapManager.getInstance().getBitmap(mContext, entry.getImage(), callBack);
	}


	/**
	 * 释放
	 */
	public void release()
	{
		mContext.unregisterReceiver(mReceiver);
		mEntryManager.release();
		mEntryManager = null;
		mContext = null;
		mInstance = null;
	}

}
