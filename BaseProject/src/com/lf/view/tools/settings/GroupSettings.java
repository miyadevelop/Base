package com.lf.view.tools.settings;

public class GroupSettings extends BaseTemplateSetting<Boolean> {
	private String mSummaryOn = ""; // 设置选择时的状态文字
	private String mSummaryOff = ""; // 没有选中时的状态文字
	private String mGroupId = ""; // 组件的名称
	private String mSaveValues = ""; // 里面存储的值
	public final static String GROUP_KEY = "group_key"; // 用来在从配置文件中获取设置的内容

	@Override
	public String createSummary() {
		if (getValue())
			return mSummaryOn;
		else
			return mSummaryOff;
	}

	public void setSummaryOn(String summaryOn) {
		this.mSummaryOn = summaryOn;
	}

	public void setSummaryOff(String summaryOff) {
		this.mSummaryOff = summaryOff;
	}

	public void setGroupId(String groupId) {
		mGroupId = groupId;
	}

	public String getGroupId() {
		return mGroupId;
	}

	public String getSaveValues() {
		return mSaveValues;
	}

	public void setSaveValues(String saveValues) {
		if (null == saveValues || "null".equals(saveValues))
			return;
		mSaveValues = saveValues;
	}
}
