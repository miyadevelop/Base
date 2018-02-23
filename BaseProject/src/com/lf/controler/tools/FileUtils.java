package com.lf.controler.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import android.content.Context;

/**
 * 文件操作
 * 
 * @author LinChen
 *
 */
public class FileUtils {
	/**
	 * 删除一个文件夹或者文件
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteFileOrFolder(File dir) {

		if (dir.isDirectory()) {
			String[] children = dir.list();
			if (children != null) {
				for (int i = 0; i < children.length; i++) {
					boolean success = deleteFileOrFolder(new File(dir, children[i]));
					if (!success) {
						return false;
					}
				}
			}
		}
		return dir.delete();
	}

	/**
	 * 创建文件夹
	 * 
	 * @param f
	 *            文件的绝对路径
	 * @return 文件
	 * @throws IOException
	 */
	public static File createFile(String f) throws IOException {
		File file = new File(f);
		file.getParentFile().mkdirs();
		file.createNewFile();
		return file;
	}

	/**
	 * 获取语义化的文件大小，例如 1K、1M等
	 * 
	 * @param file
	 * @return
	 */
	public static String size(File file) {
		DecimalFormat df = new DecimalFormat("#.00");
		String result = "0K";

		double realSize = getRealSize(file);

		if (realSize < 1024) {
			result = realSize + "B";
		} else if (realSize < 1048576) {
			result = df.format(realSize / 1024D) + "K";
		} else if (realSize < 1073741824) {
			result = df.format(realSize / 1048576D) + "M";
		} else {
			result = df.format(realSize / 1073741824D) + "G";
		}

		return result;
	}

	/**
	 * 获取文件或者文件夹的物理大小
	 * 
	 * @param file
	 * @return
	 */
	public static double getRealSize(File file) {

		double size = 0;

		// 判断文件是否存在
		if (file != null && file.exists()) {
			// 如果是目录则递归计算其内容的总大小
			if (file.isDirectory()) {
				File[] children = file.listFiles();
				for (File f : children)
					size += getRealSize(f);
				return size;
			} else {
				size = (double) file.length();
				return size;
			}
		} else {
			return size;
		}
	}

	/**
	 * 删除SD卡指定路径下面指定格式的文件，删除的不仅是文件夹下面第一层的目录下的文件，也包含目录下文件夹里面的文件。
	 * 例如删除sdcard下面的xml文件，在sd卡上存在这样一个文件sdcard/folder/demo.xml,那么这个文件也会被删除。
	 * 
	 * @param context
	 * @param fileRoute
	 *            文件的地址，这里的地址是绝对地址，例如sdcard/floder/
	 * @param fileType
	 *            删除所有以fileType后缀格式结尾的文件，如xml文件就是“.xml” 如果值为null就删除所有的文件
	 */
	public static void removeFileFromSD(Context context, String fileRoute, String fileType) {
		if (!fileRoute.endsWith("/"))
			fileRoute += "/"; // 保证文件夹是以"/"结尾，保证再递归的时候路径不会出错
		File file = new File(fileRoute);
		if (!file.exists()) {
			return;
		}
		final String[] subStance = file.list();
		if (fileType != null) {
			for (int i = 0; i < subStance.length; i++) {
				if (!new File(fileRoute + subStance[i]).isDirectory()) {
					if ((fileRoute + subStance[i]).endsWith(fileType))
						new File(fileRoute + subStance[i]).delete();
					else
						continue;
				} else {// 如果是一个文件，就进行递归操作
					final String subFileRoute = fileRoute + subStance[i];
					removeFileFromSD(context, subFileRoute, fileType);
				}
			}
		} else {// 尽管判断空和非空的代码几乎一样，但是在删除文件夹的时候，为了减少判断，只有增加代码量
			if (subStance == null) {
				return;
			}
			for (int i = 0; i < subStance.length; i++) {// error
				if (!new File(fileRoute + subStance[i]).isDirectory()) {
					new File(fileRoute + subStance[i]).delete();
				} else {// 如果是一个文件，就进行递归操作
					final String subFileRoute = fileRoute + subStance[i];
					removeFileFromSD(context, subFileRoute, fileType);
				}
			}
			new File(fileRoute).delete();
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param fromFileUrl
	 * @param toFileUrl
	 * @return
	 */
	public static boolean copyFile(String fromFileUrl, String toFileUrl) {
		File fromFile = null;
		File toFile = null;
		try {
			fromFile = new File(fromFileUrl);
		} catch (Exception e) {
			return false;
		}
		try {
			toFile = new File(toFileUrl);
		} catch (Exception e) {
			return false;
		}
		if (!fromFile.exists()) {
			return false;
		}
		if (!fromFile.isFile()) {
			return false;
		}
		if (!fromFile.canRead()) {
			return false;
		}
		// 复制到的路径如果不存在就创建
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists()) {
			toFile.delete();
		}
		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFileUrl);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c); // 将内容写到新文件当中
			}
			// 关闭数据流
			fosfrom.close();
			fosto.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			// message = "备份失败!";

		}
		return true;
	}

	/**
	 * 灏嗗熀鏈殑鏂囦欢澶嶅埗鍒版寚瀹氱殑浣嶇疆,濡傛灉鏂囦欢鍔犲瘑锛屽氨鎶婅В瀵嗗悗鐨勬暟鎹斁鍒版寚瀹氱殑鏂囦欢銆傝繖閲岀殑鍔犲瘑鐨勬枃浠惰绗﹀
	 * 悎鍔犲瘑鐨勮鑼冿紝涓嶇劧浼氳В瀵嗗け璐ャ�
	 * 
	 * @param orginalFileRoute
	 *            瑕佸鍒剁殑鏂囦欢鐨勫湴鍧�紝杩欓噷鐨勫湴鍧�槸涓�釜缁濆鐨勫湴鍧�紝骞朵笖鍖呭惈鐨勬枃浠剁殑鍚嶇О锛屼互鏂囦欢鐨勫悗缂�
	 *            悕缁撳熬锛屼緥濡傦細sdcard/mnt/snow.png銆傚鏋滄枃浠剁殑璺 緞鏄湪assets涓嬮潰锛屽垯璺緞鐨勫紑濮嬭
	 *            鏄痑ssets,濡俛ssets/1.zip鎴栬�assets/color/2.png銆�
	 * @param saveFolderRoute
	 *            鏂囦欢澶嶅埗鍦ㄨ繖涓矾寰勪笅闈紝杩欎釜璺緞涔熸槸缁濆鐨勫湴鍧�紝涓嶅寘鍚枃浠跺悕銆備緥濡傦細sdcard/ newpath/
	 * @param saveFileName
	 *            鏂囦欢瑕佸瓨鍌ㄧ殑鍚嶇О锛屼互鏂囦欢鐨勫悗缂�悕缁撳熬锛屽姞涓婃枃浠剁殑璺緞銆備緥濡俷ewpath/newFile.png
	 * @param keyWord
	 *            鎿嶄綔鍔犲瘑鐨勬枃浠讹紝濡傛灉鏂囦欢鍔犲瘑浜嗭紝瀹冨氨鏄枃浠惰В瀵嗗瘑鐮併�濡傛灉鏂囦欢鍚楋紝娌℃ 湁鍔犲瘑锛岃繖閲岀洿鎺ョ粰涓�釜null鍊煎氨鍙互浜嗐�
	 */
	public static boolean copyBaseFile(Context context, String orginalFileRoute,
			String saveFolderRoute, String saveFileName, String keyWord) {
		boolean success = false;
		BufferedInputStream buis = null;
		if (orginalFileRoute.startsWith("assets")) {
			try {
				InputStream readIs = context.getAssets().open(
						orginalFileRoute.substring(orginalFileRoute.indexOf("/") + 1));
				buis = new BufferedInputStream(readIs);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			try {
				FileInputStream readFis = new FileInputStream(orginalFileRoute);
				buis = new BufferedInputStream(readFis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (buis != null) {
			if (keyWord != null)
				WriteToSD(saveFolderRoute, saveFileName, buis, keyWord);
			try {
				buis.close();
				success = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}

	/**
	 * 瀹炵幇鏂囦欢鐨勫鍒跺姛鑳�鍦ㄥ唴閮ㄨ皟鐢�
	 * 
	 * @param folder
	 *            鏂囦欢瀛樺偍鐨勮矾寰勶紝涓嶅寘鍚枃浠�
	 * @param name
	 *            鏂囦欢鐨勬枃浠跺悕,灏辨槸瀛樺偍鏃跺�鐨勭粷瀵硅矾寰�
	 * @param buis
	 *            瑕佸鍒舵枃浠剁殑娴�
	 * @param key
	 */
	private static boolean WriteToSD(String saveFolder, String name, BufferedInputStream buis,
			String key) {
		boolean doneCopy = false;
		if (!saveFolder.endsWith("/"))
			saveFolder += "/";
		File folder = new File(saveFolder);
		if (!folder.exists())
			folder.mkdirs();
		File file = new File(name);
		if (file.exists())
			file.delete();
		if (!file.exists() || file.length() == 0) {
			try {
				file.createNewFile();
				BufferedOutputStream buos = new BufferedOutputStream(new FileOutputStream(file));
				int len = -1;
				while ((len = buis.read()) != -1) {
					buos.write(len ^ key.hashCode());
				}
				buos.flush();
				if (buos != null)
					buos.close();
				doneCopy = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return doneCopy;
	}
}
