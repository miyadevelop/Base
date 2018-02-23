package com.lf.controler.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 用于将字符串转化为MD5
 * 
 * @author LinChen,code when 2015年3月31日
 */
public class MyMD5 {

	/**
	 * 将字符串转化为MD5
	 * 
	 * @param key
	 *            字符串
	 * @return 32位小写字符
	 */
	public synchronized static String generator(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		// http://stackoverflow.com/questions/332079
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}
}
