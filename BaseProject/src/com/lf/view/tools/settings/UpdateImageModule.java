package com.lf.view.tools.settings;

import android.content.Context;
import android.widget.ImageView;

/**
 * 提示有更新的图片
 * @author Administrator
 *
 */
public class UpdateImageModule extends ImageView{

	public UpdateImageModule(Context context) {
		super(context);
		if(SettingsTheme.mUpdateImageDrawable>0){
			setImageResource(SettingsTheme.mUpdateImageDrawable);
		}
	}
}
