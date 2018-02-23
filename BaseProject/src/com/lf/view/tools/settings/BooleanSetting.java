package com.lf.view.tools.settings;

/**
 * 布尔型的设置基类，在用户设置时，它的表现形式就是一个勾选框
 * 
 * @author ww
 * 
 */
public class BooleanSetting extends BaseTemplateSetting<Boolean> {

	private String mSummaryOn = ""; // 设置为true时的状态文字
	private String mSummaryOff = ""; // 设置为false时的状态文字
	// 设置内属性的名称，用来在从配置文件中获取设置的内容（name_space）
	public final static String ATTR_SUMMARY_ON = "summary_on";
	public final static String ATTR_SUMMARY_OFF = "summary_off";

	@Override
	public String createSummary() {
		if (getValue())
			return mSummaryOn;
		return mSummaryOff;
	}

	public void setSummaryOn(String summaryOn) {
		this.mSummaryOn = summaryOn;
	}

	public void setSummaryOff(String summaryOff) {
		this.mSummaryOff = summaryOff;
	}

	@Override
	public void setNeedUpdate(boolean needUpdate) {
		super.setNeedUpdate(needUpdate);

	}
}
