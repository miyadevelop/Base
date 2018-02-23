package com.lf.view.tools;

import android.view.View;

/**
 * 对话上的按钮点击的监听
 * @author ludeyuan
 *
 */
public interface DialogClickListener {
	
	/**
	 * dialog中控件的点击，调用的代码可以依据view中的id来区分具体的控件
	 * @param dialogId 区分当前Dialog身份的ID
	 * @param view
	 */
	public void onDialogItemClick(View view,String dialogId);
}
