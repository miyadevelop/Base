package com.lf.view.tools.settings;


import android.content.Context;
import android.content.res.ColorStateList;
import android.widget.TextView;

/**
 * 设置的摘要
 * @author ww
 *
 */
public class TextViewDialogSummary extends TextView{

	public TextViewDialogSummary(Context context) {
		super(context);

		//设置文字大小
		if(SettingsTheme.mDialogSummarySize > 0)
			setTextSize(SettingsTheme.mDialogSummarySize);
		//设置文字颜色
		if(SettingsTheme.mDialogSummaryColor != null)
		{		
			int[][] status = new int[2][];
			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
			ColorStateList list = new ColorStateList(status, SettingsTheme.mDialogSummaryColor);
			setTextColor(list);
		}
		
	}

}
