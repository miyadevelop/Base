package com.lf.view.tools.settings;

/**
 * 设置类型为T的设置基类
 * 
 * @author ww
 * 
 * @param <T>
 */
public abstract class BaseTemplateSetting<T> extends BaseSetting {

	private T mDefValue; // 设置的默认值
	private T mValue = null; // 设置的值

	public T getDefValue() {
		return mDefValue;
	}

	public void setDefValue(T defValue) {
		mDefValue = defValue;
	}

	public T getValue() {
		return mValue;
	}

	public void setValue(T value) {
		this.mValue = value;
	}

}
