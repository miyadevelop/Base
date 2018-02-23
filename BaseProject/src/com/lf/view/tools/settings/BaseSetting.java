package com.lf.view.tools.settings;

/**
 * 设置数据基类
 * 
 * @author ww
 * 
 */
public abstract class BaseSetting {

	private String mKey = ""; // 存储的关键字，key这个属性可能是冗余
	private String mTitle = ""; // 设置的标题
	private String mIcon = ""; // 设置的图标，看需求，不一定要在配置文件中给它赋值
	private String mParentKey; // 连带设置的关键字
	private boolean mIsEnable = true; // 是否有效，由mParentKey对应的设置决定本设置是否能被用户修改，是否可用
	private String mSummary = ""; // 摘要
	private String mSummaryPre = ""; // 摘要前缀
	private String mSummarySuf = ""; // 摘要后缀
	private boolean mNeedUpdate = false; // 需不需要显示更新的提示

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setKey(String key) {
		mKey = key;
	}

	public String getKey() {
		return mKey;
	}

	public boolean isEnable() {
		return mIsEnable;
	}

	public void setEnable(boolean isEnable) {
		mIsEnable = isEnable;
	}

	public String getParentKey() {
		return mParentKey;
	}

	public void setParentKey(String parentKey) {
		mParentKey = parentKey;
	}

	public void setSummaryPre(String summaryPre) {
		if (null != summaryPre)
			mSummaryPre = summaryPre;
	}

	public void setSummarySuf(String summarySuf) {
		if (null != summarySuf)
			mSummarySuf = summarySuf;
	}

	public void setSummary(String summary) {
		if (null != summary)
			mSummary = summary;
	}

	public String getSummary() {
		return mSummary;
	}

	public void setIcon(String icon) {
		this.mIcon = icon;
	}

	public String getIcon() {
		return mIcon;
	}

	/**
	 * 动态生成summary
	 * 
	 * @return
	 */
	public abstract String createSummary();

	/**
	 * 获取摘要文字
	 * 
	 * @return
	 */
	public String getFullSummary() {
		if ("".equals(mSummary)) {
			return mSummaryPre + createSummary() + mSummarySuf;
		} else {
			return mSummaryPre + mSummary + mSummarySuf;
		}
	}

	/**
	 * 设置需不需要更新
	 */
	public void setNeedUpdate(boolean needUpdate) {
		mNeedUpdate = needUpdate;
	}

	public boolean getNeedUpdate() {
		return mNeedUpdate;
	}

	// 设置内属性的名称，用来在从配置文件中获取设置的内容（name_space）
	public final static String ATTR_KEY = "key";
	public final static String ATTR_TITLE = "title";
	public final static String ATTR_DEF_VALUE = "def_value";
	public final static String ATTR_PARENT_KEY = "parent_key";
	public final static String ATTR_SUMMARY = "summary";
	public final static String ATTR_SUMMARY_PRE = "summary_pre";
	public final static String ATTR_SUMMARY_SUF = "summary_suf";
	public final static String ATTR_ICON = "icon";
	public final static String ATIR_UPDATE_NOTIFICE = "update_notifice";

}
