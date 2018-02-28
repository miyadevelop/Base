package com.lf.entry;

import android.content.Intent;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 可被解析的intent，用于将解析工具的解析方法和intent匹配
 * 
 * @author ww
 * 
 */
public class ParseableIntent extends Intent implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5594934413077929709L;
	/**
	 * 用于在动态生成xml时使用
	 */
	protected Map<String, String> extraMap;
	private static final String MATCH_STRING = "match";

	public ParseableIntent() {
		super();
		extraMap = new HashMap<String, String>();
	}

	public ParseableIntent(Intent intent) {
		super(intent);
		extraMap = new HashMap<String, String>();
	}

	/**
	 * 设置匹配类的名称
	 * 
	 * @param match
	 */
	public void setMatch(String match) {
		putExtra(MATCH_STRING, match);
	}

	@Override
	public Intent putExtra(String name, String value) {
		if (!MATCH_STRING.equals(name))
			extraMap.put(name, value);
		return super.putExtra(name, value);
	}

	@Override
	public Intent putExtra(String name, boolean value) {
		return putExtra(name, String.valueOf(value));
	}

	@Override
	public Intent putExtra(String name, double value) {
		return putExtra(name, String.valueOf(value));
	}

	@Override
	public Intent putExtra(String name, float value) {
		return putExtra(name, String.valueOf(value));
	}

	@Override
	public Intent putExtra(String name, int value) {
		return putExtra(name, String.valueOf(value));
	}

	@Override
	public Intent putExtra(String name, long value) {
		return putExtra(name, String.valueOf(value));
	}

	/**
	 * 获取匹配类的名称
	 * 
	 * @return
	 */
	public String getMatch() {
		return getStringExtra(MATCH_STRING);
	}

	public Map<String, String> getKV() {
		return extraMap;
	}
}