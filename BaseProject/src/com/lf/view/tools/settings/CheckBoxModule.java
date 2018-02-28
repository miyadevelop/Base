package com.lf.view.tools.settings;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * 设置界面的CheckBox
 * @author ww
 *
 */
public class CheckBoxModule extends CheckBox{

	public CheckBoxModule(Context context) {
		super(context);
		if(SettingsTheme.mCheckBoxDrawable > 0)
		{
			setButtonDrawable(new ColorDrawable());
			setBackgroundResource(SettingsTheme.mCheckBoxDrawable);
		}
	}


	public CheckBoxModule(Context context,AttributeSet attributeSet) {
		super(context, attributeSet);
	}
}
