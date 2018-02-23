package com.lf.view.tools;

import android.content.Context;

/**
 * 单位转换器
 * @author ldy
 *
 */
public class UnitConvert {
	
	/**
	 * dp装换乘像素
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int DpToPx(Context context,float dp){
		float density = context.getResources().getDisplayMetrics().density;
		return (int) (dp * density + 0.5f);
	}
	
	/**
	 * px转dp
	 * @param c
	 * @param px
	 * @return
	 */
	public static int PxToDp(Context c, float px){
		float density =	c.getResources().getDisplayMetrics().density;
		return (int) (px / density + 0.5f);
	}
	
	/**
	 * 密度转px
	 * @param c
	 * @param sp
	 * @return
	 */
	public static int SpToPx(Context c, float sp){
		float scaledDensity = c.getResources().getDisplayMetrics().scaledDensity;
		return (int) (sp * scaledDensity + 0.5f);
	}
	
	/**
	 * px转密度
	 * @param c
	 * @param px
	 * @return
	 */
	public static int PxToSp(Context c, float px){
		float scaledDensity = c.getResources().getDisplayMetrics().scaledDensity;
		return (int) (px / scaledDensity + 0.5f);
	}
}
