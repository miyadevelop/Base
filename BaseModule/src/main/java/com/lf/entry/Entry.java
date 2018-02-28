package com.lf.entry;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;

import android.net.Uri;

import com.lf.controler.tools.JSONObjectTool;


/**
 * 入口元数据
 * 
 * @author ww
 * 
 */
public class Entry {
	/**
	 * 它的集合id
	 */
	private String parentId;

	/**
	 * 入口图片
	 */
	protected String imageUrl = "";

	/**
	 * 代表入口是开着的
	 */
	public static final int STATUS_OPEN = 1;

	/**
	 * 代表入口关闭
	 */
	public static final int STATUS_CLOSE = 0;

	/**
	 * 代表入口加载出错
	 */
	public static final int STATUS_ERR = -1;

	private String id;
	private String text;
	/**
	 * 图片资源位置
	 */
	private String image;

	private ParseableIntent intent;
	/**
	 * 状态的标识，-1代表没有状态或尚未初始化，也可以用来区分是否为工具
	 */
	private int status = STATUS_CLOSE;

	private String match;//点击跳转时匹配器的类名，简名或者全名都可以


	private Map<String,String> filters = new HashMap<String, String>(); //过滤数据，在过滤入口时用到


	private Map<String,String> showExtras = new HashMap<String, String>(); //入口显示时，所需要用到的额外数据


	public Entry() {
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public ParseableIntent getIntent() {
		return intent;
	}

	public void setIntent(ParseableIntent intent) {
		this.intent = intent;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMatch() {
		return match;
	}

	public void setMatch(String match) {
		this.match = match;
	}

	public Map<String,String> getFilters() {
		return filters;
	}

	public void addFilter(String key, String value) {
		this.filters.put(key, value);
	}

	public void addExtra(String key, String value){
		showExtras.put(key, value);
	}

	public String getExtra(String key)
	{
		return showExtras.get(key);
	}
	

	public static Entry fromJson(JSONObjectTool jsonObjectTool)
	{
		Entry entry = new Entry();
		try {
			entry.setId(jsonObjectTool.getString("id"));
			entry.setText(jsonObjectTool.getString("name"));
			entry.setImage(jsonObjectTool.getString("image"));
			entry.setMatch(jsonObjectTool.getString("match"));
			entry.setStatus(jsonObjectTool.getInt("status",1));

			//解析intent
			{
				JSONObjectTool json = jsonObjectTool.getJSONObjectTool("intent");
				entry.intent = new ParseableIntent();
				
				String packageName = json.getString("package", null);
//				if("com.lf.linhua".equals(packageName))
//					packageName = "com.lf.linghua";
				entry.intent.setPackage(packageName);//package显式描述哪个app来接收这个intent，通常setPackage函数不会和setClass同时调用，而是在隐式调用intent时，用来指定接收的app

				String className = json.getString("class", null);
				if(null != className)//className不能传null
				{
					if(null == entry.intent.getPackage() || "".equals(entry.intent))//如果没有指定报名，仅指定class，则认为是打开本应用内的界面，则需要带上本应用的包名或者Context
						entry.intent.setClassName("", className);
					else
						entry.intent.setClassName(entry.intent.getPackage(), className);
				}
				//		ComponentName component = new ComponentName(pkg, cls);
				//		intent.setComponent(component);//Intent的Compent属性指定Intent的的目标组件的类名称。通常 Android会根据Intent 中包含的其它属性的信息，比如action、data/type、category进行查找，最终找到一个与之匹配的目标组件。但是，如果 component这个属性有指定的话，将直接使用它指定的组件，而不再执行上述查找过程。指定了这个属性以后，Intent的其它所有属性都是可选的。

				entry.intent.setAction(json.getString("action", null));//intent要执行动作的描述

				//			intent.setData(Uri.parse(json.getString("data", null)));//intent要执行的操作所携带的数据，类似于网址的格式，Uri中描述了host 和 scheme 以及参数，其中host和scheme都有“指定谁来执行这个Intent”的作用
				//			intent.setType(json.getString("type", null));//指定打开文件的格式类型，Intent的Type属性显式指定Intent的数据类型（MIME）。一般Intent的数据类型能够根据数据本身进行判定，但是通过设置这个属性，可以强制采用显式指定的类型而不再进行推导。
				String dataString = json.getString("data", null);
				Uri data = null;
				if(null != dataString)
					data = Uri.parse(dataString);
				entry.intent.setDataAndType(data, json.getString("type", null));//setData和setType，相互冲突，不能同时调用，用这个函数代替

				JSONArray jsonCategory = json.getJSONArray("category", new JSONArray());//执行动作的附加描述 CATEGORY_HOME CATEGORY_LAUNCHER
				for (int i = 0; i < jsonCategory.length(); i++) {
					String category = jsonCategory.getString(i);
					entry.intent.addCategory(category);
				}

				JSONArray jsonArrayExtra = json.getJSONArray("extra", new JSONArray());
				for (int i = 0; i < jsonArrayExtra.length(); i++) {
					JSONObjectTool jsonExtra = new JSONObjectTool(jsonArrayExtra.getJSONObject(i));
					//bug，这里可能需要添加extra类型，否则非string类型的extra可能会读取的时候读不到
					entry.intent.putExtra(jsonExtra.getString("key"),jsonExtra.getString("value"));
				}

				//		intent.setFlags(flags);//打开界面的方式
			}

			//解析entry中的filter
			{
				JSONArray jsonArray = jsonObjectTool.getJSONArray("filter", new JSONArray());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObjectTool json = new JSONObjectTool(jsonArray.getJSONObject(i));
					entry.addFilter(json.getString("key"),json.getString("value"));
				}
			}

			//解析entry中的extra
			{
				JSONArray jsonArray = jsonObjectTool.getJSONArray("showExtras", new JSONArray());
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObjectTool json = new JSONObjectTool(jsonArray.getJSONObject(i));
					entry.addExtra(json.getString("key"),json.getString("value"));
				}
			}

			return entry;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}


//		{
//			    "data":[
//			        {
//			            "id":"1",
//			            "name":"打开自家浏览器",
//			            "image":"http://s1.sinaimg.cn/orignal/4a4bcec10f06d9ca6c4f0",
//			            "match":"Launcher",
//			            "intent":{
//			                "class":"com.lf.view.tools.activity.WebActivity",
//			                "extra":[
//			                    {
//			                        "key":"showUri",
//			                        "value":"https://www.baidu.com"
//			                    },
//			                    {
//			                        "key":"title",
//			                        "value":"标题"
//			                    }
//			                ]
//			            },
//			            "filter":{
//			                "notInstall":"com.lf.linhua",
//			                "startTime":"2016/11/24 13:00:00",
//			                "endTime":"2016/11/24 14:00:00",
//			                "launchMoreThan":"2",
//			                "launchLessThan":"6"
//			            },
//			            "extra":{
//			                "subTitle":"副标题"
//			            }
//			        },
//			        {
//			            "id":"2",
//			            "name":"打开内置浏览器",
//			            "image":"http://pic.58pic.com/58pic/13/80/09/66r58PICBK4_1024.jpg",
//			            "match":"launch",
//			            "intent":{
//			                "action":"android.intent.action.VIEW",
//			                "data":"https://www.baidu.com",
//			                "category":[
//			                    "android.intent.category.DEFAULT"
//			                ]
//			            }
//			        },
//			        {
//			            "id":"3",
//			            "name":"打开零花中浏览器",
//			            "image":"http://pic5.997788.com/pic_search/00/23/95/28/se23952876.jpg",
//			            "match":"Launcher",
//			            "intent":{
//			                "class":"com.lf.view.tools.activity.WebActivity",
//			                "package":"com.lf.linhua",
//			                "extra":[
//			                    {
//			                        "key":"showUri",
//			                        "value":"https://www.baidu.com"
//			                    },
//			                    {
//			                        "key":"title",
//			                        "value":"标题"
//			                    }
//			                ]
//			            },
//			            "filter":{
//			                "install":"com.lf.linhua"
//			            },
//			            "extra":{
//	
//			            }
//			        },
//			        {
//			            "id":"4",
//			            "name":"发送广播",
//			            "image":"http://img3.redocn.com/20120305/Redocn_2012030507470389.jpg",
//			            "match":"broadCast",
//			            "intent":{
//			                "action":"com.lf.TestAction",
//			                "extra":[
//			                    {
//			                        "key":"data",
//			                        "value":"数据"
//			                    }
//			                ]
//			            },
//			            "filter":{
//	
//			            },
//			            "extra":{
//	
//			            }
//			        },
//			        {
//			            "id":"5",
//			            "name":"测试解析",
//			            "image":"http://www.dabaoku.com/sucai/ziranlei/hf/0026.jpg",
//			            "match":"Launcher",
//			            "intent":{
//			                "action":"abc",
//			                "data":"https://www.baidu.com",
//			                "category":[
//			                    "android.intent.category.DEFAULT"
//			                ],
//			                "type":"mp3",
//			                "class":"com.lf.view.tools.activity.WebActivity",
//			                "package":"com.lf.linhua"
//			            }
//			        }
//			    ],
//			    "error":"",
//			    "message":"",
//			    "need_update":"true",
//			    "pageCount":0,
//			    "status":"ok",
//			    "time":"2016-11-18 13:41:50"
//			}
//	
}