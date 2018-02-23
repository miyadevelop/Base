package com.lf.controler.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 字符串的快捷工具类
 * 
 * @author LinChen,code when 2015年3月31日
 */
public class StringUtil {

	/**
	 * 从文件中读取文字信息
	 * 
	 * @param fromFile
	 * @return
	 * @throws IOException
	 */
	public static String from(File fromFile) throws IOException {
		return from(new FileInputStream(fromFile));
	}

	/**
	 * 从流中读取文字信息
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String from(InputStream is) throws IOException {
		if (is != null) {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[1024];
			int len = 0;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			while ((len = br.read(buffer)) != -1) {
				sb.append(buffer, 0, len);
			}
			br.close();
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 将文字信息写入文件
	 * 
	 * @param str
	 *            文字
	 * @param toFile
	 *            目标文件
	 * @param isAppend
	 *            是否追加
	 * @throws IOException
	 */
	public static void to(String str, File toFile, boolean isAppend) throws IOException {
		if (str != null && str.length() > 0) {
			toFile.getParentFile().mkdirs();
			BufferedWriter bw = new BufferedWriter(new FileWriter(toFile, isAppend));
			bw.write(str);
			bw.flush();
			bw.close();
		}
	}

	/**
	 * 将文字信息写入文件，如果文件存在则覆盖
	 * 
	 * @param str
	 * @param toFile
	 * @throws IOException
	 */
	public static void to(String str, File toFile) throws IOException {
		to(str, toFile, false);
	}

	/**
	 * 判断是否为null
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0) {
			return true;
		} else {
			return false;
		}
	}
}
