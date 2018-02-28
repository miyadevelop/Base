package com.lf.view.tools.settings;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;

/**
 * 单选设置对话框
 * 
 * @author ww
 * 
 */
public class RadioGroupDialog extends BaseSettingDialog<IntSetting> {

//	private RadioGroupModule mRadioGroup;// 单选布局
	
	private DrawableRadioGroup mRadioGroup;
	private ScrollView mScrollView;
	
	public RadioGroupDialog(Context context, BaseSetting setting) {
		super(context, setting);
		
	}

	@Override
	public void changeSetting() {
		
		// 从布局获取设置值，从而修改设置
//		int value = mRadioGroup.getCheckedRadioButtonId();
		int value = mRadioGroup.getCheckedIndex();
		Settings.getInstance(getContext()).setIntSettingValue(
				getSetting().getKey(), value);
	}

	@Override
	public View getContentView() {
		mScrollView = new ScrollView(getContext());

		// 添加单选组
//		mRadioGroup = new RadioGroupModule(getContext());
//		mRadioGroup.setOrientation(RadioGroup.VERTICAL);
//		ArrayList<String> summarys = getSetting().getSummarys();
//		for (int i = 0; i < summarys.size(); i++) {
//			String summary = summarys.get(i);
//			RadioButtonModule radioButton = new RadioButtonModule(getContext());
//			// 设置每个单选项的文字
//			radioButton.setText(summary);
//			radioButton.setId(i);
//			mRadioGroup.addView(radioButton, new LayoutParams(
//					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			// 根据设置的值设置checked的单选项
//			if (i == getSetting().getValue())
//				radioButton.setChecked(true);
//		}
		//zzq 2015/7/22
		mRadioGroup = new DrawableRadioGroup(getContext());
		mRadioGroup.setOrientation(RadioGroup.VERTICAL);
		ArrayList<String> summarys = getSetting().getSummarys();
		for (int i = 0; i < summarys.size(); i++) {
			String summary = summarys.get(i);
			DrawableRadioButton radioButton = new DrawableRadioButton(getContext());
			// 设置每个单选项的文字
			radioButton.setText(summary);
			radioButton.setId(i);
			mRadioGroup.addView(radioButton, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// 根据设置的值设置checked的单选项
			if (i == getSetting().getValue())
				radioButton.setChecked(true);
		}
	
		mScrollView.addView(mRadioGroup, new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		return mScrollView;

	}

}
