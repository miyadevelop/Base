package com.lf.controler.tools;

import android.annotation.SuppressLint;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密工具，采用的是AES加密（即使这个已经不安全了）
 * 
 * @author LinChen
 *
 */
public class MyEncryption {
	/**
	 * 密钥算法
	 */
	private static final String KEY_ALGORITHM = "AES";

	private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

	/**
	 * 随机生成一串密钥
	 * 
	 * @return byte[] 密钥
	 * @throws Exception
	 */
	@SuppressLint("TrulyRandom")
	public synchronized static byte[] initSecretKey() {
		// 返回生成指定算法的秘密密钥的 KeyGenerator 对象
		KeyGenerator kg = null;
		try {
			kg = KeyGenerator.getInstance(KEY_ALGORITHM);
			kg.init(new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new byte[0];
		}
		// 初始化此密钥生成器，使其具有确定的密钥大小
		// AES 要求密钥长度为 128
		kg.init(128);
		// 生成一个密钥
		SecretKey secretKey = kg.generateKey();
		return secretKey.getEncoded();
	}

	/**
	 * 转换密钥
	 * 
	 * @param key
	 *            二进制密钥
	 * @return 密钥
	 */
	private static Key toKey(byte[] key) {
		// 生成密钥
		return new SecretKeySpec(key, KEY_ALGORITHM);
	}

	/***************************************************** 加密部分开始 ********************************************************/

	/**
	 * 将String加密，并返回String
	 * 
	 * @param data
	 *            加密的字符串
	 * @param key
	 *            随机密钥
	 * @return
	 * @throws Exception
	 */
	public synchronized static String encryptToString(String data, byte[] key) throws Exception {
		return bytesToHexString(encrypt(data.getBytes(), key));
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            二进制密钥
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	public synchronized static byte[] encrypt(byte[] data, byte[] key) throws Exception {
		return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            二进制密钥
	 * @param cipherAlgorithm
	 *            加密算法/工作模式/填充方式
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	private synchronized static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm)
			throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return encrypt(data, k, cipherAlgorithm);
	}

	/**
	 * 加密
	 * 
	 * @param data
	 *            待加密数据
	 * @param key
	 *            密钥
	 * @param cipherAlgorithm
	 *            加密算法/工作模式/填充方式
	 * @return byte[] 加密数据
	 * @throws Exception
	 */
	private synchronized static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm)
			throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为加密模式
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	/*
	 * Convert byte[] to hex string.这里我们可以将byte转换成int，然后利用Integer.toHexString(int)来转换成16进制字符串。
	 * 
	 * @param src byte[] data
	 * 
	 * @return hex string
	 */
	private static String bytesToHexString(byte[] src) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (src == null || src.length <= 0) {
			return null;
		}
		for (int i = 0; i < src.length; i++) {
			int v = src[i] & 0xFF;
			String hv = Integer.toHexString(v);
			if (hv.length() < 2) {
				stringBuilder.append(0);
			}
			stringBuilder.append(hv);
		}
		return stringBuilder.toString();
	}

	/***************************************************** 加密部分结束 ********************************************************/

	/***************************************************** 解密部分开始 ********************************************************/

	/**
	 * 解密
	 * 
	 * @param data
	 *            加密后的数据
	 * @param key
	 *            密钥
	 * @return 解密后的数据
	 * @throws Exception
	 */
	public synchronized static String decryptToString(String data, byte[] key) throws Exception {
		return new String(decrypt(hexStringToBytes(data), key));
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            二进制密钥
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	public synchronized static byte[] decrypt(byte[] data, byte[] key) throws Exception {
		return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            二进制密钥
	 * @param cipherAlgorithm
	 *            加密算法/工作模式/填充方式
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	private synchronized static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm)
			throws Exception {
		// 还原密钥
		Key k = toKey(key);
		return decrypt(data, k, cipherAlgorithm);
	}

	/**
	 * 解密
	 * 
	 * @param data
	 *            待解密数据
	 * @param key
	 *            密钥
	 * @param cipherAlgorithm
	 *            加密算法/工作模式/填充方式
	 * @return byte[] 解密数据
	 * @throws Exception
	 */
	private synchronized static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm)
			throws Exception {
		// 实例化
		Cipher cipher = Cipher.getInstance(cipherAlgorithm);
		// 使用密钥初始化，设置为解密模式
		cipher.init(Cipher.DECRYPT_MODE, key);
		// 执行操作
		return cipher.doFinal(data);
	}

	/**
	 * Convert hex string to byte[]
	 * 
	 * @param hexString
	 *            the hex string
	 * @return byte[]
	 */
	private static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	/**
	 * Convert char to byte
	 * 
	 * @param c
	 *            char
	 * @return byte
	 */
	private static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}

	/***************************************************** 解密部分结束 ********************************************************/

	/**
	 * 创建一个安全的单元，保存字符串信息
	 * 
	 * @return
	 */
	public static SecureMsg createSecureMsg() {
		SecureMsg msg = new SecureMsg();
		return msg;
	}

	/**
	 * 将字符串转化为MD5
	 * 
	 * @param key
	 *            字符串
	 * @return 32位小写字符
	 */
	public static String genMD5(String key) {
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

	/**
	 * 加密单元，将指定的内容加密
	 * 
	 * @author LinChen
	 *
	 */
	static class SecureMsg {
		/**
		 * 对称加密的密钥
		 */
		private static final byte[] KEY = new byte[] { -35, -36, -36, -71, 127, 117, 9, -117, 119,
				-80, -57, -77, 126, 29, 106, 57 };
		private byte[] msg;

		/**
		 * 获取信息
		 * 
		 * @return
		 */
		public String getMsg() {
			if (msg == null) {
				return "";
			} else {
				String result = null;
				try {
					result = new String(MyEncryption.decrypt(msg, KEY));
				} catch (Exception e) {
					e.printStackTrace();
					result = "";
				}
				return result;
			}
		}

		/**
		 * 设置信息
		 * 
		 * @param msg
		 */
		public void setMsg(String msg) {
			try {
				this.msg = MyEncryption.encrypt(msg.getBytes(), KEY);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
