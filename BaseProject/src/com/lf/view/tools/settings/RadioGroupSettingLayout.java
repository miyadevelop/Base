package com.lf.view.tools.settings;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;

import com.mobi.tool.MyR;

/**
 * 单选项RelativeLayout
 * @author zzq
 *
 * @param <T>
 */
public class RadioGroupSettingLayout  extends RelativeLayout{
	
	private RadioGroupModule mRadioGroup;// 单选布局
	
	private IntSetting mSetting;// 设置内容
	
	private RadioGroupSettingListener mListener;
	
	public interface RadioGroupSettingListener{
		public void checked(int index);
	}
	
	public void setRadioGroupSettingListener(RadioGroupSettingListener listener){
		mListener = listener;
	}

	public RadioGroupSettingLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public RadioGroupSettingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public RadioGroupSettingLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	//根据key 显示
	public void setView(String key) {
		
		mSetting = (IntSetting) Settings.getInstance(getContext()).getSetting(key);;
		// 添加单选组
		mRadioGroup = new RadioGroupModule(getContext());
		mRadioGroup.setOrientation(RadioGroup.VERTICAL);
		ArrayList<String> summarys = mSetting.getSummarys();
		for (int i = 0; i < summarys.size(); i++) {
			String summary = summarys.get(i);
			RadioButtonModule radioButton = (RadioButtonModule) LayoutInflater.from(getContext()).inflate(MyR.layout(getContext(), "layout_radiobuttonmodule"), null);
			
			// 设置每个单选项的文字
			radioButton.setText(summary);
			radioButton.setId(i);
			mRadioGroup.addView(radioButton, new LayoutParams(
					LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			// 根据设置的值设置checked的单选项
			if (i == mSetting.getValue())
				radioButton.setChecked(true);
		}
		mRadioGroup.setOnCheckedChangeListener(new RadigGroupCheckedListener());
		addView(mRadioGroup,  new LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

	}
	
	
	//选择哪一项
	public int changeSetting() {

		// 从布局获取设置值，从而修改设置
		int value = mRadioGroup.getCheckedRadioButtonId();
		Settings.getInstance(getContext()).setIntSettingValue(
				mSetting.getKey(), value);
		return value;
	}
	
	//单选选择监听
	class RadigGroupCheckedListener implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			mListener.checked(changeSetting());
			
		}
		
	}
}
