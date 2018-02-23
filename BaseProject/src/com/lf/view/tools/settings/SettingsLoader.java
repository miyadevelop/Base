package com.lf.view.tools.settings;

import java.io.IOException;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.XmlResourceParser;

import com.mobi.tool.MyR;

/**
 * 将设置的内容从XML加载到Settings中的加载者
 * 
 * @author ww
 * 
 */
public class SettingsLoader {

	/**
	 * 从assets/settings.xml下加载设置信息
	 * help
	 * @param context
	 */
	public static HashMap<String, BaseSetting> load(Context context) {
		//获取到xml下面的settings.xml文件
		XmlResourceParser parser = context.getResources().getXml(
				MyR.xml(context,"settings"));
		// 解析设置文件
		return parse(context, parser);
		
//		try {
//			// 获取设置文件流
//			InputStream is = context.getAssets().open("settings.xml");
//			// 初始化解析器
//			XmlPullParser parser = Xml.newPullParser();
//			parser.setInput(is, "utf-8");
//			// 解析设置文件
//			return parse(context, parser);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//			return new HashMap<String, BaseSetting>();// 防止null
//		}
	}

	/**
	 * 解析
	 * 
	 * @param is
	 * @return
	 */
	public static HashMap<String, BaseSetting> parse(Context context, XmlPullParser parser) {
		// 要返回的内容
		HashMap<String, BaseSetting> settings = new HashMap<String, BaseSetting>();

		SharedPreferences sp = context.getSharedPreferences("settings", 0);
		SharedPreferences mUpdateSp = context.getSharedPreferences("settings_update", 0);
		try {
		
			int type = parser.getEventType();
//			String namespace = parser.getNamespace();
			String namespace = null;

			BaseSetting setting = null;
			while (type != XmlPullParser.END_DOCUMENT) {

				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					// 布尔型设置
					if ("BooleanSetting".equals(parser.getName())) {
						BooleanSetting singleSetting = new BooleanSetting();

						singleSetting.setSummaryOff(getDiyValue(context, parser, namespace,
								BooleanSetting.ATTR_SUMMARY_OFF));
						singleSetting.setSummaryOn(getDiyValue(context, parser, namespace,
								BooleanSetting.ATTR_SUMMARY_ON));
						Boolean defValue = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_DEF_VALUE));
						singleSetting.setDefValue(defValue);

						singleSetting.setKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_KEY));
						singleSetting.setParentKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_PARENT_KEY));
						singleSetting.setTitle(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_TITLE));
						singleSetting.setIcon(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_ICON));
						singleSetting.setValue(sp.getBoolean(singleSetting.getKey(), defValue));
						singleSetting.setSummary(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY));
						singleSetting.setSummaryPre(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_PRE));
						singleSetting.setSummarySuf(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_SUF));
						Boolean needUpdate = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATIR_UPDATE_NOTIFICE));
						// singleSetting.setNeedUpdate(needUpdate);
						singleSetting.setNeedUpdate(mUpdateSp.getBoolean(singleSetting.getKey(),
								needUpdate));
						setting = singleSetting;
					}
					// 选项组设置
					else if ("GroupItemSrtting".equals(parser.getName())) {
						GroupSettings group = new GroupSettings();

						group.setSummaryOff(getDiyValue(context, parser, namespace,
								BooleanSetting.ATTR_SUMMARY_OFF));
						group.setSummaryOn(getDiyValue(context, parser, namespace,
								BooleanSetting.ATTR_SUMMARY_ON));
						Boolean defValue = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_DEF_VALUE));
						group.setDefValue(defValue);

						group.setKey(getDiyValue(context, parser, namespace, BaseSetting.ATTR_KEY));
						group.setParentKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_PARENT_KEY));
						group.setTitle(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_TITLE));
						group.setGroupId(getDiyValue(context, parser, namespace,
								GroupSettings.GROUP_KEY));
						// group.setValue(sp.getBoolean(group.getKey(),defValue));
						if (group.getKey().equals(sp.getString(group.getGroupId(), null)))
							group.setValue(true);
						else
							group.setValue(false);
						group.setSummary(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY));
						group.setSummaryPre(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_PRE));
						group.setSummarySuf(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_SUF));
						Boolean needUpdate = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATIR_UPDATE_NOTIFICE));
						// group.setNeedUpdate(needUpdate);
						group.setNeedUpdate(mUpdateSp.getBoolean(group.getKey(), needUpdate));
						group.setSaveValues(sp.getString(group.getKey(), null));
						group.setNeedUpdate(mUpdateSp.getBoolean(group.getKey(), needUpdate));
						setting = group;
					}

					// 整型设置
					else if ("IntSetting".equals(parser.getName())) {
						IntSetting singleSetting = new IntSetting();

						String defString = getDiyValue(context, parser, namespace,
								IntSetting.ATTR_DEF_VALUE);
						int defValue = Integer.valueOf(defString);
						singleSetting.setDefValue(defValue);

						singleSetting.setKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_KEY));
						singleSetting.setParentKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_PARENT_KEY));
						singleSetting.setTitle(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_TITLE));
						singleSetting.setIcon(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_ICON));
						singleSetting.setValue(sp.getInt(singleSetting.getKey(), defValue));
						singleSetting.setSummary(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY));
						singleSetting.setSummaryPre(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_PRE));
						singleSetting.setSummarySuf(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_SUF));
						Boolean needUpdate = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATIR_UPDATE_NOTIFICE));
						// singleSetting.setNeedUpdate(needUpdate);
						singleSetting.setNeedUpdate(mUpdateSp.getBoolean(singleSetting.getKey(),
								needUpdate));
						setting = singleSetting;
					}
					// 单选设置
					else if ("StringSetting".equals(parser.getName())) {
						StringSetting singleSetting = new StringSetting();

						String defString = getDiyValue(context, parser, namespace,
								IntSetting.ATTR_DEF_VALUE);

						singleSetting.setDefValue(defString);
						singleSetting.setKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_KEY));
						singleSetting.setParentKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_PARENT_KEY));
						singleSetting.setTitle(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_TITLE));
						singleSetting.setIcon(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_ICON));
						singleSetting.setValue(sp.getString(singleSetting.getKey(), defString));
						singleSetting.setSummary(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY));
						singleSetting.setSummaryPre(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_PRE));
						singleSetting.setSummarySuf(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_SUF));
						Boolean needUpdate = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATIR_UPDATE_NOTIFICE));
						// singleSetting.setNeedUpdate(needUpdate);
						singleSetting.setNeedUpdate(mUpdateSp.getBoolean(singleSetting.getKey(),
								needUpdate));
						setting = singleSetting;
					}
					// 扩展设置
					else if ("ExpandSetting".equals(parser.getName())) {
						ExpandSetting singleSetting = new ExpandSetting();

						singleSetting.setKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_KEY));
						singleSetting.setParentKey(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_PARENT_KEY));
						singleSetting.setTitle(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_TITLE));
						singleSetting.setIcon(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_ICON));
						singleSetting.setSummary(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY));
						singleSetting.setSummaryPre(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_PRE));
						singleSetting.setSummarySuf(getDiyValue(context, parser, namespace,
								BaseSetting.ATTR_SUMMARY_SUF));
						Boolean needUpdate = "true".equals(getDiyValue(context, parser, namespace,
								BaseSetting.ATIR_UPDATE_NOTIFICE));
						// singleSetting.setNeedUpdate(needUpdate);
						singleSetting.setNeedUpdate(mUpdateSp.getBoolean(singleSetting.getKey(),
								needUpdate));
						setting = singleSetting;
					} else if ("summary".equals(parser.getName())) {
						String summary = getDiyValueNextText(context, parser);
						((IntSetting) setting).addSummary(summary);
					}

					break;
				case XmlPullParser.END_TAG:
					if ("BooleanSetting".equals(parser.getName())
							|| "IntSetting".equals(parser.getName())
							|| "StringSetting".equals(parser.getName())
							|| "ExpandSetting".equals(parser.getName())
							|| "GroupItemSrtting".equals(parser.getName())) {
						if (null != settings && null != setting) {
							String key = setting.getKey();
							settings.put(key, setting);
						}
					}

					break;
				}
				type = parser.next();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return settings;
	}

	private static String getDiyValueNextText(Context context, XmlPullParser parser)
			throws XmlPullParserException, IOException {
		String diyContent = parser.nextText();
		String result;
		if (diyContent != null && diyContent.indexOf("@string/") == 0) {
			String key = diyContent.substring(8);
			result = context.getResources().getString(MyR.string(context, key));
		} else {
			result = diyContent;
		}
		return result;
	}

	private static String getDiyValue(Context context, XmlPullParser parser, String namespace,
			String name) {
		String diyContent = parser.getAttributeValue(namespace, name);
		String result;
		if (diyContent != null && diyContent.indexOf("@string/") == 0) {
			String key = diyContent.substring(8);
			result = context.getResources().getString(MyR.string(context, key));
		} else {
			result = diyContent;
		}
		return result;
	}
}
