package com.lf.view.tools;

import android.content.Context;
import android.os.Handler;
import android.view.Gravity;
import android.widget.Toast;

/**
 * toast的显示，防止多个Toast重复出现，影响用户的体验
 */
public class CustomToastShow {
	private static Toast mToast;
	private static Handler mHandler = new Handler(); 
	private static Runnable r=new Runnable() {
		
		@Override
		public void run() {
			mToast.cancel();
		}
	};
	/**
	 * 显示Toast
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context,String text,int showTime){
		mHandler.removeCallbacks(r);
		if(mToast!=null)mToast.setText(text);
		else{
			mToast=Toast.makeText(context, text,showTime);
			mToast.setGravity(Gravity.CENTER,0,-UnitConvert.DpToPx(context,20));
		}
		mToast.show();
	}
	
	/**
	 * Toast退出
	 */
	public static void cancel(){
		if(null!=mToast)
			mToast.cancel();
	}
}
