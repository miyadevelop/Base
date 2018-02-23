package com.lf.view.tools.settings;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lf.view.tools.UnitConvert;
import com.mobi.tool.MyR;

public class DrawableRadioButton extends RelativeLayout{

	private TagCheckBox mCheckBox;

	private TextView mTextView;

	public DrawableRadioButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		View view = LayoutInflater.from(getContext()).inflate(MyR.layout(getContext(), "layout_drawable_raidobutton"), null);
		mCheckBox = (TagCheckBox) view.findViewById(MyR.id(getContext(), "tagcheckbox"));
		mTextView = (TextView) view.findViewById(MyR.id(getContext(), "tagcheckbox_text"));
		if (SettingsTheme.mRadioButtonBg > 0){
			mCheckBox.setBackgroundDrawable(getContext().getResources().getDrawable(SettingsTheme.mRadioButtonBg));
		}
		// 设置文字大小
		if (SettingsTheme.mDialogSummarySize > 0)
			mTextView.setTextSize(SettingsTheme.mDialogSummarySize);
		// 设置文字颜色
		if (SettingsTheme.mDialogSummaryColor != null) {
			int[][] status = new int[2][];
			status[0] = TextView.ENABLED_STATE_SET;// 可用时的颜色
			status[1] = TextView.EMPTY_STATE_SET;// 不可用时的颜色
			ColorStateList list = new ColorStateList(status,
					SettingsTheme.mDialogSummaryColor);
			mTextView.setTextColor(list);
		}
		addView(view, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, UnitConvert.DpToPx(getContext(), 50)));
	}

	public DrawableRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public DrawableRadioButton(Context context) {
		super(context);
		init();
	}	

	public void setText(String des){
		mTextView.setText(des);
	}

	public void setDrawable(Drawable d){
		mCheckBox.setBackgroundDrawable(d);
	}

	public void setId(int id){
		mCheckBox.setCheckTagId(id);
	}

	public TagCheckBox getTagCheckBox(){
		return mCheckBox;
	}

	public void setChecked(boolean check){
		mCheckBox.setChecked(check);
	}
	
	//是否勾选
	public boolean isChecked(){
		return mCheckBox.isChecked();
	}
}
