package com.lf.view.tools.settings;

/**
 * 扩展设置，不对任何基础数据进行操作的设置，也没用设置值和默认值 ，设置的工作交给系统，比如设置系统的声音大小 只要跳到系统的声音设置界面即可
 * 
 * @author ww
 * 
 */
public class ExpandSetting extends BaseSetting {

	@Override
	public String createSummary() {
		return getSummary();
	}

}
