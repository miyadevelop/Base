package com.lf.view.tools.imagecache;


/**
 * 图片名字的生成类
 * 
 * @author LinChen,code when 2015年4月7日
 */
public class BitmapSimpleName {

	/**
	 * 生成图片名字的简称
	 * 
	 * @param url
	 * @return
	 */
	public static String gen(String url) {
		if (url == null || "".equals(url.trim())) {
			return "null.1";
		} else {
			// return url.substring(url.lastIndexOf("/") + 1) + ".1";//这样子生成的名字很可能重复
			return FileNameGenerator.generator(url);
		}
	}

	/**
	 * 生成可见的图片简称
	 * 
	 * @param url
	 * @return
	 */
	public static String visibleGen(String url) {
		return FileNameGenerator.generator(url);
		// return url.substring(url.lastIndexOf("/") + 1);
	}
}
