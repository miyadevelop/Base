package com.lf.view.tools.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lf.view.tools.NotificationCenter;
import com.lf.view.tools.NotificationCenter.Notification;
import com.mobi.tool.MyR;


/**
 * 系统通知界面
 * @author wangwei
 *
 */
public class NotificationAcitivity extends Activity implements OnItemClickListener{


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(MyR.layout(this, "activity_notificaiton"));
		ListView listView = (ListView)findViewById(MyR.id(this, "notification_list"));
		ArrayList<Notification> notifications = NotificationCenter.getInstance().getNotifications();
		listView.setAdapter(new NotificationAdapter(this, notifications));
		listView.setOnItemClickListener(this);
	}


	public class NotificationAdapter extends ArrayAdapter<Notification>
	{

		public NotificationAdapter(Context context, ArrayList<Notification> notifications) {
			super(context, 0, notifications);
		}


		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;

			if(null == convertView)
			{
				convertView = View.inflate(getContext(), MyR.layout(getContext(), "base_layout_notification"), null);
				holder = new ViewHolder();
				holder.mImageIcon = (ImageView)convertView.findViewById(MyR.id(getContext(), "notification_icon"));
				holder.mTextTitle = (TextView)convertView.findViewById(MyR.id(getContext(), "notification_title"));
				holder.mTextContent = (TextView)convertView.findViewById(MyR.id(getContext(), "notification_content"));
				convertView.setTag(holder);
				convertView.setBackgroundResource(MyR.drawable(NotificationAcitivity.this, "view_module_1_click"));
				holder.mTextTitle.setTextColor(getResources().getColor(MyR.color(getContext(), "module_1_text_1")));
				holder.mTextContent.setTextColor(getResources().getColor(MyR.color(getContext(), "module_1_text_3")));
			}
			else
				holder = (ViewHolder)convertView.getTag();

			Notification notification = getItem(position);
			holder.mImageIcon.setImageResource(notification.icon);
			holder.mTextTitle.setText(notification.title);
			holder.mTextContent.setText(notification.content);

			return convertView;
		}
	}


	public class ViewHolder
	{
		public TextView mTextTitle;
		public TextView mTextContent;
		public ImageView mImageIcon;
	}


	@Override
	public void onItemClick(AdapterView<?> listView, View arg1, int arg2, long arg3) {

		@SuppressWarnings("unchecked")
		Notification notification = ((ArrayAdapter<Notification>)listView.getAdapter()).getItem(arg2);
		startActivity(notification.intent);
	}
}
