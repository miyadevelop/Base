package com.lf.view.tools;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 软键盘的控制
 * @author ldy
 *
 */
public class KeyBoardCenter {
	
	/**
	 * 关闭系统的键盘
	 */
	public static final void closeKeyboard(Context context,View view){
		InputMethodManager imm=(InputMethodManager)context.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘 
		}
	}
	
	/**
	 * 打开系统的键盘
	 * @param context
	 */
	public static final void openKeyBoard(Context context,View view){	
		InputMethodManager imm=(InputMethodManager)context.getSystemService(
				Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){
			imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);  
		}
	}
}
