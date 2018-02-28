package com.lf.view.tools.settings;

import android.content.Context;
import android.widget.ImageView;

public class ExpandIconModule extends ImageView{

	public ExpandIconModule(Context context) {
		super(context);
		setImageResource(SettingsTheme.mExpandableIconDrawable);
	}

}
