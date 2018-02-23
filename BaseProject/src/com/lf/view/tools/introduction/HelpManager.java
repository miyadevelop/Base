package com.lf.view.tools.introduction;

import java.io.IOException;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

import com.mobi.tool.MyR;

public class HelpManager {

	/**
	 * 解析帮助文档,只解析“group_function”下面的标签
	 * @param context
	 * @return 有可能是一个长度为0的集合
	 */
	public static ArrayList<HelpItemBean> getHelp(Context context){
		return getHelp(context,"group_function");
	}
	
	/**
	 * 解析帮助文档,只解析“group_function”下面的标签
	 * @param helpGroup 自定义help_item中属性的值
	 * @return 有可能是一个长度为0的集合
	 */
	public static ArrayList<HelpItemBean> getHelp(Context context,String helpGroup){
		ArrayList<HelpItemBean> helpItemBeans = new ArrayList<HelpItemBean>();
		HelpItemBean helpItemBean = null;
		XmlResourceParser parser = context.getResources().getXml(MyR.xml(context, "help"));
		String namespace = null;
		try{
			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				String tag = parser.getName();
				switch (parser.getEventType()) {
				case XmlPullParser.START_TAG:
					if("help_item".equals(tag)){//检测到help_item标签
						String value = parser.getAttributeValue(namespace,"value");
						if(value.equals(helpGroup)){//只有标签对应，才新建对象
							helpItemBean = new HelpItemBean();
						}
					}else if("title".equals(tag) && helpItemBean!=null){
						//检测到标题，并且helpGroup的值和当前的标签对应
						helpItemBean.setTitle(parser.nextText());
					}else if("message".equals(tag) && helpItemBean!=null){
						helpItemBean.setMessage(parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					if("help_item".equals(tag)){
						if(helpItemBean!=null){
							helpItemBeans.add(helpItemBean);
							helpItemBean=null;	//重新设置为空，让下次循环继续使用
						}
					}
					break;
				default:
					break;
				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return helpItemBeans;
	}
}
