package com.lf.view.tools.settings;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * 勾选框，没有标题
 * @author Administrator
 *
 */
public class CheckLeftModule extends CheckBox{

	public CheckLeftModule(Context context) {
		super(context);
		if(SettingsTheme.mCheckLeftDrawable > 0)
		{
			setButtonDrawable(new ColorDrawable());
			setBackgroundResource(SettingsTheme.mCheckLeftDrawable);
		}
	}
	
	public CheckLeftModule(Context context,AttributeSet attributeSet) {
		super(context, attributeSet);
	}

}
