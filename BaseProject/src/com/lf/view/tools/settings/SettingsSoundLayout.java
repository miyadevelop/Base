package com.lf.view.tools.settings;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.lf.view.tools.settings.DrawableRadioGroup.DrawalbeRadioGroupCheckListener;

/**
 * 音效勾选模式布局 因radiButton无法改变图片大小所以替换checkBox
 * 缺点: 写死了 如果音效模式变多，则需要改布局及代码
 * @author zzq
 *
 */
public class SettingsSoundLayout extends RelativeLayout{

	private IntSetting mSetting;// 设置内容

	private SettingSoundListener mListener;

	private DrawableRadioGroup 		view;

	public interface SettingSoundListener{
		public void checked(int index);
	}

	public void setSettingSoundListener(SettingSoundListener listener){
		mListener = listener;
	}

	public SettingsSoundLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SettingsSoundLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SettingsSoundLayout(Context context) {
		super(context);
	}

	public void setView(String key){
		mSetting = (IntSetting) Settings.getInstance(getContext()).getSetting(key);
		view = new DrawableRadioGroup(getContext());
		view.setOnDrawableRadioGroupCheckedListener(new SoundDrawalbeRadioGroupCheckListener());
		view.setOrientation(RadioGroup.VERTICAL);
		ArrayList<String> summarys = mSetting.getSummarys();
		for (int i = 0; i < summarys.size(); i++) {
			String summary = summarys.get(i);
			DrawableRadioButton radioButton = new DrawableRadioButton(getContext());
			// 设置每个单选项的文字
			radioButton.setText(summary);
			radioButton.setId(i);
			view.addView(radioButton, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			Log.d("zzqceshi", "设置对话框添加了自定义RadioButton-->" + radioButton.getTagCheckBox().getCheckTagId());
			// 根据设置的值设置checked的单选项
			if (i == mSetting.getValue())
				radioButton.setChecked(true);
		}


		addView(view,  new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
	}

	
	
	class SoundDrawalbeRadioGroupCheckListener implements DrawalbeRadioGroupCheckListener{

		@Override
		public void onCheckedChange(int checkIndex) {
			Settings.getInstance(getContext()).setIntSettingValue(
					mSetting.getKey(), checkIndex);
			mListener.checked(checkIndex);
		}
		
	}
	
}
