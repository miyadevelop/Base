package com.lf.view.tools.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;


/**
 * 设置的标题
 * @author ww
 *
 */
public class TextViewTitle extends TextView{

	public TextViewTitle(Context context) {
		super(context);

		//设置文字可以点击时候的样式
		if(SettingsTheme.mTitleEnableStyle > 0){
			setTextAppearance(context,SettingsTheme.mTitleEnableStyle);
		}
		//		
		//		//设置文字大小
		//		if(SettingsTheme.mTitleSize > 0)
		//			setTextSize(SettingsTheme.mTitleSize);
		//		//设置文字颜色
		//		if(SettingsTheme.mTitleColor != null)
		//		{		
		//			int[][] status = new int[2][];
		//			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
		//			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
		//			ColorStateList list = new ColorStateList(status, SettingsTheme.mTitleColor);
		//			setTextColor(list);
		//		}
	}

	public TextViewTitle(Context context, AttributeSet attrs){
		super(context, attrs);

		//设置文字可以点击时候的样式
		if(SettingsTheme.mTitleEnableStyle > 0){
			setTextAppearance(context,SettingsTheme.mTitleEnableStyle);
		}

		//		//设置文字大小
		//		if(SettingsTheme.mTitleSize > 0)
		//			setTextSize(SettingsTheme.mTitleSize);
		//		//设置文字颜色
		//		if(SettingsTheme.mTitleColor != null)
		//		{		
		//			int[][] status = new int[2][];
		//			status[0] = TextView.ENABLED_STATE_SET;//可用时的颜色
		//			status[1] = TextView.EMPTY_STATE_SET;//不可用时的颜色
		//			ColorStateList list = new ColorStateList(status, SettingsTheme.mTitleColor);
		//			setTextColor(list);
		//		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(enabled){
			//设置文字可以点击时候的样式
			if(SettingsTheme.mTitleEnableStyle > 0){
				setTextAppearance(getContext(),SettingsTheme.mTitleEnableStyle);
			}
		}else{
			//设置文字不可以点击时候的样式
			if(SettingsTheme.mTitleDisableStyle > 0){
				setTextAppearance(getContext(),SettingsTheme.mTitleDisableStyle);
			}
		}
		super.setEnabled(enabled);
	}

}
