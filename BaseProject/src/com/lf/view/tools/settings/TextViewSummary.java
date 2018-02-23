package com.lf.view.tools.settings;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * 设置的摘要
 * @author ww
 *
 */
public class TextViewSummary extends TextView{

	public TextViewSummary(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if(SettingsTheme.mSummaryEnableStyle > 0){
			setTextAppearance(context,SettingsTheme.mSummaryEnableStyle);
		}

//		//设置文字大小
//		if(SettingsTheme.mSummarySize > 0)
//			setTextSize(SettingsTheme.mSummarySize);
//		//设置文字颜色
//		if(SettingsTheme.mSummaryColor != null)
//		{		
//			int[][] status = new int[2][];
//			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
//			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
//			ColorStateList list = new ColorStateList(status, SettingsTheme.mSummaryColor);
//			setTextColor(list);
//		}
		
	}


	public TextViewSummary(Context context) {
		super(context);
		
		if(SettingsTheme.mSummaryEnableStyle > 0){
			setTextAppearance(context,SettingsTheme.mSummaryEnableStyle);
		}
		
//		//设置文字大小
//		if(SettingsTheme.mSummarySize > 0)
//			setTextSize(SettingsTheme.mSummarySize);
//		//设置文字颜色
//		if(SettingsTheme.mSummaryColor != null)
//		{		
//			int[][] status = new int[2][];
//			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
//			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
//			ColorStateList list = new ColorStateList(status, SettingsTheme.mSummaryColor);
//			setTextColor(list);
//		}
		
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		if(enabled){
			//设置文字可以点击时候的样式
			if(SettingsTheme.mSummaryEnableStyle > 0){
				setTextAppearance(getContext(),SettingsTheme.mSummaryEnableStyle);
			}
		}else{
			//设置文字不可以点击时候的样式
			if(SettingsTheme.mSummaryDisableStyle > 0){
				setTextAppearance(getContext(),SettingsTheme.mSummaryDisableStyle);
			}
		}
		super.setEnabled(enabled);
	}
	

}
