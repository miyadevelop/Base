package com.lf.tool.data_persistence;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;

/**
 * 读写工具,json的保存读取操作
 * 
 * @author lc
 * 
 */
public class ReadAndWriteUtil {

	/**
	 * 密钥
	 */
	private static byte[] KEY = new byte[] { 54, 21, -101, -58, -32, -83, 92,
			68, 19, -116, 57, 44, -77, -107, 57, 107 };
	/**
	 * 读写锁
	 */
	private static final Object R_W_LOCK = new Object();
	/**
	 * 正常读写
	 */
	public static final int MODE_NOR = 1;
	/**
	 * 加密读写
	 */
	public static final int MODE_ENCRYPTION = 2;
	/**
	 * 文件写入的头标识
	 */
	private static final String PLACEHOLDER_HEAD = "head";
	/**
	 * 文件写入的内容标识
	 */
	private static final String PLACEHOLDER_CONTENT = "content";
	/**
	 * 内存中保存的文件
	 */
	// private File dataFile;
	/**
	 * sd卡上保存的文件
	 */
	// private File sdFile;
	/**
	 * 保存和读写的模式
	 */
	private static int MODE = MODE_ENCRYPTION;

	/**
	 * 将map转化为jsonobject
	 * 
	 * @param map
	 * @return
	 */
	public static JSONObject mapToJsonObject(Map<String, Object> map) {
		JSONObject result = new JSONObject(map);
		return result;
	}

	/**
	 * 将map转为json保存
	 * 
	 * @param map
	 * @throws Exception
	 */
	public static void save(Context context, String fileName,
			Map<String, Object> map) throws Exception {
		JSONObject object = mapToJsonObject(map);
		save(context, fileName, object, false);
	}

	/**
	 * 将map转为json保存
	 * 
	 * @param map
	 * @throws Exception
	 */
	public static void save(Context context, String fileName,
			Map<String, Object> map, boolean isAppend) throws Exception {
		JSONObject object = mapToJsonObject(map);
		save(context, fileName, object, isAppend);
	}

	/**
	 * 将map数组转为JsonArray保存
	 * 
	 * @param list
	 * @throws Exception
	 */
	public static void save(Context context, String fileName,
			List<Map<String, Object>> list) throws Exception {
		JSONArray array = new JSONArray();
		for (Map<String, Object> map : list) {
			JSONObject object = mapToJsonObject(map);
			array.put(object);
		}
		save(context, fileName, array, false);
	}

	/**
	 * 将map数组转为JsonArray保存
	 * 
	 * @param list
	 * @throws Exception
	 */
	public static void save(Context context, String fileName,
			List<Map<String, Object>> list, boolean isAppend) throws Exception {
		JSONArray array = new JSONArray();
		for (Map<String, Object> map : list) {
			JSONObject object = mapToJsonObject(map);
			array.put(object);
		}
		save(context, fileName, array, isAppend);
	}

	/**
	 * 保存数组，如果需要追加数据，则在原先的数组基础上增加数据
	 * 
	 * @param array
	 * @param isAppend
	 *            true：追加数据，false：不追加数据
	 * @throws Exception
	 */
	public static void save(Context context, String fileName, JSONArray array,
			boolean isAppend) throws Exception {
		if (isAppend) {// 是否追加
			// 先去获取原先的数据，拼接后再保存
			JSONArray jsonArray = getJsonArray(context, fileName);
			if (array != null) {// 需要的数据不为空，就添加进去
				int len = array.length();
				for (int i = 0; i < len; ++i) {
					jsonArray.put(array.get(i));
				}
			}
			save(context, fileName, jsonArray);
		} else {
			save(context, fileName, array);
		}
	}

	/**
	 * 保存JSONArray
	 * 
	 * @param array
	 * @throws Exception
	 */
	public static void save(Context context, String fileName, JSONArray array)
			throws Exception {
		JSONObject json = new JSONObject();

		JSONObject headJson = new JSONObject();
		headJson.put("mode", MODE);

		json.put(PLACEHOLDER_HEAD, headJson).put(PLACEHOLDER_CONTENT, array);
		writeDataToFile(context, fileName, json.toString());
	}

	public static void save(Context context, String fileName,
			JSONObject object, boolean isAppend) throws Exception {
		if (isAppend) {// 是否追加
			// 先去获取原先的数据，拼接后再保存
			JSONArray jsonArray = getJsonArray(context, fileName);
			jsonArray.put(object);
			save(context, fileName, jsonArray);
		} else {
			save(context, fileName, object);
		}
	}

	/**
	 * 保存JSONObject
	 * 
	 * @param array
	 * @throws Exception
	 */
	public static void save(Context context, String fileName, JSONObject object)
			throws Exception {
		JSONArray array = new JSONArray();
		array.put(object);
		save(context, fileName, array);
	}

	/**
	 * 获取JSONObject，没有数据时返回new JSONObject()
	 * 
	 * @return
	 */
	public static JSONObject getJsonObject(Context context, String fileName) {
		try {
			String content = readDataFromFile(context, fileName);
			if (content == null) {
				return new JSONObject();
			} else {
				JSONArray array = new JSONObject(content)
						.getJSONArray(PLACEHOLDER_CONTENT);
				int len = array.length();
				JSONObject result;

				if (len > 0) {
					result = array.getJSONObject(0);
				} else {
					result = new JSONObject();
				}

				return result;
			}
		} catch (Exception e) {
			return new JSONObject();
		}
	}

	/**
	 * 获取JSONArray，没有数据时返回new JSONArray()
	 * 
	 * @return
	 */
	public static JSONArray getJsonArray(Context context, String fileName) {
		try {
			String content = readDataFromFile(context, fileName);
			if (content == null) {
				return new JSONArray();
			} else {
				return new JSONObject(content)
						.getJSONArray(PLACEHOLDER_CONTENT);
			}
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	/**
	 * 指定包名获取JSONArray，没有数据时返回new JSONArray()
	 * 
	 * @return
	 */
	public static JSONArray getJsonArray(Context context, String fileName,
			String packageName) {
		try {

			File dataFile = new File(Environment.getExternalStorageDirectory()
					+ File.separator + packageName, fileName);

			String content = readStringFromFile(dataFile);
			if (content == null) {
				return new JSONArray();
			} else {
				return new JSONObject(content)
						.getJSONArray(PLACEHOLDER_CONTENT);
			}
		} catch (Exception e) {
			return new JSONArray();
		}
	}

	/**
	 * 将数据保存进文件，并对数据同步
	 * 
	 * @param content
	 * @throws Exception
	 */
	private static void writeDataToFile(Context context, String fileName,
			String content) throws Exception {
		synchronized (R_W_LOCK) {
			File dataFile = getDataFile(context, fileName);
			File sdFile = getSdFile(context, fileName);
			if (dataFile != null && sdFile != null && content != null) {
				if ((dataFile.exists() && sdFile.exists())
						|| (!dataFile.exists() && !sdFile.exists())) {// 文件都存在或者不存在的时候
					writeStringToFile(content, dataFile, false);
					writeStringToFile(content, sdFile, false);
				} else if (dataFile.exists() && !sdFile.exists()) {// 内存中有，sd卡上没
					writeStringToFile(content, dataFile, false);
					copyFile(dataFile, sdFile);
				} else if (!dataFile.exists() && sdFile.exists()) {// 内存中没，sd卡上有
					writeStringToFile(content, sdFile, false);
					copyFile(sdFile, dataFile);
				}
			}
		}
	}

	/**
	 * 获取数据，并对数据同步
	 * 
	 * @return
	 * @throws Exception
	 */
	private static String readDataFromFile(Context context, String fileName)
			throws Exception {
		synchronized (R_W_LOCK) {
			String result = null;
			File dataFile = getDataFile(context, fileName);
			File sdFile = getSdFile(context, fileName);
			if (dataFile != null && sdFile != null) {
				if (dataFile.exists() && sdFile.exists()) {// 文件都存在
					result = readStringFromFile(dataFile);
				} else if (dataFile.exists() && !sdFile.exists()) {
					result = readStringFromFile(dataFile);
					copyFile(dataFile, sdFile);
				} else if (!dataFile.exists() && sdFile.exists()) {
					result = readStringFromFile(sdFile);
					copyFile(sdFile, dataFile);
				}
			}
			return result;
		}
	}

	private static File getDataFile(Context context, String fileName) {
		return new File(context.getFilesDir() + File.separator
				+ "data_persistence", fileName);
	}

	private static File getSdFile(Context context, String fileName) {
		return new File(Environment.getExternalStorageDirectory()
				+ File.separator + context.getPackageName(), fileName);
	}

	public static String getMetaData(Context context, String key) {
		Bundle bundle;
		try {
			bundle = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA).metaData;
			if (bundle != null) {
				String result = bundle.getString(key);
				if (result == null) {
					result = String.valueOf(bundle.getInt(key));
				}
				if ("0".equals(result)) {
					result = "";
				}
				return result;
			}
		} catch (NameNotFoundException e1) {
		}
		return "";
	}

	/**
	 * 拷贝文件
	 * 
	 * @param from
	 * @param to
	 * @throws IOException
	 */
	private static void copyFile(File from, File to) throws IOException {

		byte[] buffer = new byte[1024 * 1];

		FileInputStream fis = new FileInputStream(from);

		to.getParentFile().mkdirs();
		FileOutputStream fos = new FileOutputStream(to);

		int len;

		while ((len = fis.read(buffer)) != -1) {
			fos.write(buffer, 0, len);
		}
		fis.close();
		fos.flush();
		fos.close();
	}

	/**
	 * 将字符串写入文件
	 * 
	 * @param str
	 * @param toFile
	 * @param isAppend
	 * @throws Exception
	 */
	private static void writeStringToFile(String str, File toFile,
			boolean isAppend) throws Exception {
		if (str != null && str.length() > 0) {
			toFile.getParentFile().mkdirs();
			byte[] temp;// 需要保存的字节
			switch (MODE) {// 以什么样的方式报错
			case MODE_NOR:
				temp = str.getBytes();
				break;
			case MODE_ENCRYPTION:
				temp = AESCoder.encrypt(str.getBytes(), KEY);
				break;
			default:
				temp = str.getBytes();
				break;
			}
			FileOutputStream fos = new FileOutputStream(toFile, isAppend);
			fos.write(temp);
			fos.flush();
			fos.close();

			temp = null;
		}
	}

	/**
	 * 从目标文件中读取内容
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private static String readStringFromFile(File file) throws Exception {

		ByteArrayOutputStream baos;

		byte[] buffer = new byte[1024];

		FileInputStream is = new FileInputStream(file);
		BufferedInputStream bis = new BufferedInputStream(is);
		baos = new ByteArrayOutputStream(bis.available());

		int len;
		while ((len = bis.read(buffer)) != -1) {
			baos.write(buffer, 0, len);
		}
		bis.close();

		String result;

		switch (MODE) {// 以什么样的方式报错
		case MODE_NOR:
			result = baos.toString();
			break;
		case MODE_ENCRYPTION:
			result = new String(AESCoder.decrypt(baos.toByteArray(), KEY));
			break;
		default:
			result = baos.toString();
			break;
		}
		baos = null;
		return result;
	}

	public static void clearFile(Context context ,String fileName) {
		deleteFile(getDataFile(context, fileName));
		deleteFile(getSdFile(context, fileName));
	}
	
	public static void clearFile(Context context ,String fileName,String packageName) {
		deleteFile(new File(Environment.getExternalStorageDirectory()
				+ File.separator + packageName, fileName));
	}

	public static void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
		}
	}

}