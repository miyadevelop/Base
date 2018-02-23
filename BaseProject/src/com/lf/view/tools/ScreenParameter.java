package com.lf.view.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

/**
 * 手机屏幕的参数
 * @author ludeyuan
 *
 */
public class ScreenParameter {
	
	/**
	 * 获取手机屏幕的宽和高
	 * @return 返回一个二维数组，第一个参数是宽度、第二个参数是高度
	 */
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	public static int[] getDisplayWidthAndHeight(Context context) {
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE))
				.getDefaultDisplay();
		if(android.os.Build.VERSION.SDK_INT>=13){
			Point size = new Point();
			display.getSize(size);
			return new int[] {size.x,size.y};
		}else{
			return new int[] { display.getWidth(), display.getHeight()};
		}
	}
	
	
	/**
     * 获得通知栏的高度
     * @param context
     * @return
     * by Hankkin at:2015-10-07 21:16:43
     */
    public static int getNotificationBarHeight(Context context) {

        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.MyR$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height")
                    .get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }
}
