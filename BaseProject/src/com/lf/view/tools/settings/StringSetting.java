package com.lf.view.tools.settings;

/**
 * 字符串的设置，需要用户编辑（比如个性签名）或者设置一个特殊的字符串，比如一个软件的包名
 * 
 * @author ww
 * 
 */
public class StringSetting extends BaseTemplateSetting<String> {

	@Override
	public String createSummary() {
		return getValue();
	}

}
