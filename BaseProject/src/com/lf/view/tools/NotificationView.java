package com.lf.view.tools;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lf.view.tools.NotificationCenter.Notification;
import com.lf.view.tools.NotificationCenter.NotifyListener;
import com.lf.view.tools.activity.NotificationAcitivity;
import com.mobi.tool.MyR;

public class NotificationView extends RelativeLayout{

	private Notification mNotification;

	public NotificationView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public NotificationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public NotificationView(Context context) {
		super(context);
		init();
	}


	public void init()
	{
		LayoutInflater.from(getContext()).inflate(MyR.layout(getContext(), "base_layout_notification"), this);

		NotifyListener listener = new NotifyListener() {

			@Override
			public void onNotify(Notification notification) {
				int size = NotificationCenter.getInstance().getNotifications().size();
				if(size > 1)
				{
					mNotification = new Notification();
					mNotification.icon = MyR.drawable(getContext(), "ic_launcher");
					mNotification.title = "您有"+ size + "条消息通知";
					mNotification.content = "点击查看";
					mNotification.intent = new Intent(getContext(), NotificationAcitivity.class);
				}
				else
					mNotification = notification;

				ImageView imageIcon = (ImageView)findViewById(MyR.id(getContext(),"notification_icon"));
				imageIcon.setImageResource(mNotification.icon);
				TextView textTitle = (TextView)findViewById(MyR.id(getContext(),"notification_title"));
				textTitle.setText(mNotification.title);
				TextView textContent = (TextView)findViewById(MyR.id(getContext(),"notification_content"));
				textContent.setText(mNotification.content);

				show();
				Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(getContext(), uri);
				r.play(); 
			}
		};

		NotificationCenter.getInstance().addListener(listener);

		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getContext().startActivity(mNotification.intent);
				hide();
			}
		};
		setOnClickListener(onClickListener);
	}


	public void show()
	{
		setVisibility(View.VISIBLE);
		ScaleAnimation animation =new ScaleAnimation(1f, 1f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		startAnimation(animation);
	}


	public void hide()
	{
		setVisibility(View.GONE);
	}
}
