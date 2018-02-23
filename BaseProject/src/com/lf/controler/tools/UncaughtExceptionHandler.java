package com.lf.controler.tools;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.mobi.tool.MyR;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
	private final Context mContext;
	Handler handler = new Handler();

	public UncaughtExceptionHandler(Context context) {
		this.mContext = context;
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	/**
	 * 捕获异常，并发送至com.mobi.crashreceiver
	 */
	public void uncaughtException(Thread thread, Throwable exception) {
		StringWriter stackTrace = new StringWriter();
		exception.printStackTrace(new PrintWriter(stackTrace));
		System.err.println(stackTrace);

		Intent intent = new Intent("com.mobi.crash");
		intent.putExtra("crash_msg",
				this.mContext.getResources().getString(MyR.string(this.mContext, "app_name")) + "\n"
						+ stackTrace.toString());
		ComponentName com = new ComponentName("com.mobi.crashreceiver",
				"com.mobi.crashreceiver.CrashActivity");
		intent.setComponent(com);
		intent.setFlags(268435456);
		this.mContext.startActivity(intent);

		if ((this.mContext instanceof Activity)) {
			((Activity) this.mContext).finish();
		}

		System.exit(10);
	}
}