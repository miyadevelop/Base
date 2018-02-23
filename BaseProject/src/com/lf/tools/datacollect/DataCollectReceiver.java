package com.lf.tools.datacollect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lf.controler.tools.NetWorkManager;

/**
 * 广播接收器，用来接收某些指定的广播， 根据广播的情况来决定是否需要上传事件到服务器
 * 
 * @author ww
 * 
 */
public class DataCollectReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		if (NetWorkManager.getInstance(context).isConnect()) {
			DataCollect.getInstance(context).notifyUpload();
		}
	}
}
