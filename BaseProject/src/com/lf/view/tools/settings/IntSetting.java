package com.lf.view.tools.settings;

import java.util.ArrayList;

/**
 * 单选的设置，在与界面结合时，一般是以RadioGroup的形式呈现，供用户设置
 * 
 * @author ww
 * 
 */
public class IntSetting extends BaseTemplateSetting<Integer> {

	private ArrayList<String> mSummarys = new ArrayList<String>(); // 每一个选项所对的摘要

	@Override
	public String createSummary() {
		if (mSummarys.size() > 0)
			return mSummarys.get(getValue());
		else
			return getValue().toString();
	}

	/**
	 * 添加一条摘要
	 * 
	 * @param summary
	 */
	public void addSummary(String summary) {
		mSummarys.add(summary);
	}

	/**
	 * 获取所有摘要文字
	 * 
	 * @return
	 */
	public ArrayList<String> getSummarys() {
		return mSummarys;
	}

}
