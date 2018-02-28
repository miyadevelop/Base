package com.lf.view.tools.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * 设置界面的单选项
 * 
 * @author ww
 * 
 */
public class RadioButtonModule extends RadioButton {

	public RadioButtonModule(Context context) {
		super(context);

		initView();

	}

	private void initView() {
		if(SettingsTheme.mRadioButtoEnableStyle > 0){
			setTextAppearance(getContext(), SettingsTheme.mRadioButtoEnableStyle);
		}
		if(SettingsTheme.mRadioButtonBg > 0){
			setButtonDrawable(getContext().getResources().getDrawable(SettingsTheme.mRadioButtonBg));
		}
		
		
//		if (SettingsTheme.mRadioButtonBg > 0){
//			
//		}
//		//zzq修改radiobutton 图片大小 15/7/10
////		setCompoundDrawables(getContext().getResources().getDrawable(SettingsTheme.mRadioButtonBg), null, null, null);
////		Drawable[] maleDrawables = getCompoundDrawables();
////		maleDrawables[0].setBounds(0,0,35,35);
////		setCompoundDrawables(maleDrawables[0],null,null,null);
//		setButtonDrawable(getContext().getResources().getDrawable(SettingsTheme.mRadioButtonBg));
//
//		// 设置文字大小
//		if (SettingsTheme.mDialogSummarySize > 0)
//			setTextSize(SettingsTheme.mDialogSummarySize);
//		// 设置文字颜色
//		if (SettingsTheme.mDialogSummaryColor != null) {
//			int[][] status = new int[2][];
//			status[0] = TextView.ENABLED_STATE_SET;// 可用时的颜色
//			status[1] = TextView.EMPTY_STATE_SET;// 不可用时的颜色
//			ColorStateList list = new ColorStateList(status,
//					SettingsTheme.mDialogSummaryColor);
//			setTextColor(list);
//		}
	}

	public RadioButtonModule(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public RadioButtonModule(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}
	
}
