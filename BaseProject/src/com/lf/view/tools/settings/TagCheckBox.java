package com.lf.view.tools.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * 只能勾选的CheckBox
 * @author zzq
 *
 */
public class TagCheckBox extends CheckBox{
	
	private int checkTagId;
	
	public TagCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public TagCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public TagCheckBox(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public int getCheckTagId() {
		return checkTagId;
	}

	public void setCheckTagId(int checkTagId) {
		this.checkTagId = checkTagId;
	}

	@Override
	public void toggle() {
		setChecked(true);
	}
}
