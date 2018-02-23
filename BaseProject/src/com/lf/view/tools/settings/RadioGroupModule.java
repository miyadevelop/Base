package com.lf.view.tools.settings;

import android.content.Context;
import android.widget.RadioGroup;

/**
 * 设置页面中的单选组
 * @author ww
 *
 */
public class RadioGroupModule extends RadioGroup{

	public RadioGroupModule(Context context) {
		super(context);

		//设置背景
		if(SettingsTheme.mRadioGroupBg > 0)
			setBackgroundResource(SettingsTheme.mRadioGroupBg);
		
	}

}
