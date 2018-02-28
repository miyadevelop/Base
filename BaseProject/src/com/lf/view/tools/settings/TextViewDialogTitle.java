package com.lf.view.tools.settings;

import android.content.Context;
import android.widget.TextView;

/**
 * 对话框标题栏
 * @author Administrator
 *
 */
public class TextViewDialogTitle extends TextView{

	public TextViewDialogTitle(Context context) {
		super(context);
		
		if(SettingsTheme.mDialogTitleEnableStyle > 0){
			setTextAppearance(context,SettingsTheme.mDialogTitleEnableStyle);
		}

//		//设置背景
		if(SettingsTheme.mDialogTitleBg > 0)
			setBackgroundResource(SettingsTheme.mDialogTitleBg);
//		
//		//设置文字大小
//		if(SettingsTheme.mDialogTitleSize > 0)
//			setTextSize(SettingsTheme.mDialogTitleSize);
//		//设置文字颜色
//		if(SettingsTheme.mDialogTitleColor != null)
//		{		
//			int[][] status = new int[2][];
//			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
//			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
//			ColorStateList list = new ColorStateList(status, SettingsTheme.mDialogTitleColor);
//			setTextColor(list);
//		}
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if(enabled){
			if(SettingsTheme.mDialogTitleEnableStyle > 0){
				setTextAppearance(getContext(),SettingsTheme.mDialogTitleEnableStyle);
			}
		}else{
			if(SettingsTheme.mDialogTitleDisableStyle>0){
				setTextAppearance(getContext(),SettingsTheme.mDialogTitleDisableStyle);
			}
		}
		super.setEnabled(enabled);
	}

}
