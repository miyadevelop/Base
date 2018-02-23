package com.lf.view.tools.settings;

import android.content.Context;
import android.widget.Button;

/**
 * 设置界面的按钮
 * @author ww
 *
 */
public class ButtonModule extends Button{

	public ButtonModule(Context context) {
		super(context);

		if(SettingsTheme.mButtonBg > 0)
			setBackgroundResource(SettingsTheme.mButtonBg);
		
		if(SettingsTheme.mButtonTextStyle > 0){
			setTextAppearance(context, SettingsTheme.mButtonTextStyle);
		}

//		if(SettingsTheme.mButtonTextSize > 0)
//			setTextSize(SettingsTheme.mButtonTextSize);
//		//设置文字颜色
//
//		setTextColor(SettingsTheme.mButtonTextColor);

		


	}



}
