package com.lf.view.tools.settings;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

/**
 * String编辑对话框
 * 
 * @author ww
 * 
 */
public class StringEditDialog extends BaseSettingDialog<StringSetting> {

	private EditModule mEditModule;// 输入框

	public StringEditDialog(Context context, BaseSetting setting) {
		super(context, setting);
	}

	public View getContentView() {

		mEditModule = new EditModule(getContext());
		// 设置提示
		mEditModule.setHint("点击此处进行编辑");
		mEditModule.setText(getSetting().getValue());
		mEditModule.setSelection(getSetting().getValue().length());
		mEditModule.setGravity(Gravity.TOP);

		return mEditModule;
	}

	@Override
	public void changeSetting() {

		// 从布局获取设置内容，从而修改设置
		String key = getSetting().getKey();
		String value = mEditModule.getText().toString();
		Settings.getInstance(getContext()).setStringSettingValue(key, value);
		// Settings.getInstance(getContext()).setSummary(key, value);
	}
}
