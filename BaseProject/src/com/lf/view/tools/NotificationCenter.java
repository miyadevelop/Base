package com.lf.view.tools;

import java.util.ArrayList;

import android.content.Intent;

public class NotificationCenter {

	private static  NotificationCenter mInstance;
	private ArrayList<Notification> mNotifications;
	private ArrayList<NotifyListener> mListeners;

	private NotificationCenter()
	{
		mNotifications = new ArrayList<Notification>();
		mListeners = new ArrayList<NotificationCenter.NotifyListener>();
	}

	public static NotificationCenter getInstance()
	{
		if(null == mInstance)
		{
			mInstance = new NotificationCenter();
		}
		return mInstance;
	}


	public void notify(Notification notification)
	{
		mNotifications.add(notification);
		for(NotifyListener listener : mListeners)
		{
			listener.onNotify(notification);
		}
	}


	public void addListener(NotifyListener listener)
	{
		mListeners.add(listener);
	}

	
	public ArrayList<Notification> getNotifications() {
		return mNotifications;
	}

	public interface NotifyListener
	{
		public void onNotify(Notification notification);
	}
	
	
	public static class Notification
	{
		public String title;
		public String content;
		public Intent intent;
		public int icon;
	}
}
